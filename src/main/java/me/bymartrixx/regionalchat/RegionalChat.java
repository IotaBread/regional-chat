package me.bymartrixx.regionalchat;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class RegionalChat implements ModInitializer {
    private static final String CONFIG_FILE = "regional_chat.json";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .create();
    public static RegionalChatConfig CONFIG;

    @Override
    public void onInitialize() {
        Path configFile = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE);

        RegionalChatConfig config = null;
        try {
            config = RegionalChatConfig.readConfig(configFile, GSON);
        } catch (IOException e) {
            LOGGER.error("Failed to read config file", e);
        } catch (JsonSyntaxException e) {
            LOGGER.error("Failed to parse config file", e);
        }

        if (config == null) {
            config = new RegionalChatConfig();
            LOGGER.info("Using default config");
            try {
                RegionalChatConfig.writeConfig(configFile, GSON, config);
            } catch (IOException e) {
                LOGGER.error("Failed to write default config file", e);
            }
        }

        CONFIG = config;
    }

    public static void broadcastMessage(ServerPlayerEntity senderPlayer, Text serverMessage, Function<ServerPlayerEntity, Text> playerMessageFactory) {
        if (senderPlayer.getServer() == null) {
            return; // Should not happen
        }

        ServerWorld world = senderPlayer.getWorld();
        MinecraftServer server = senderPlayer.getServer();
        List<ServerPlayerEntity> players = world.getPlayers();
        UUID sender = senderPlayer.getUuid();
        boolean shouldDoOpBypass = CONFIG.opBypass && server.getPermissionLevel(senderPlayer.getGameProfile()) >= CONFIG.opRequiredPermissionLevel;

        server.sendSystemMessage(serverMessage, sender);

        for (ServerPlayerEntity player : players) {
            Text text = playerMessageFactory.apply(player);
            if (text == null) {
                continue;
            }

            if (player == senderPlayer) {
                // Sender should always see their own message
                player.sendMessage(text, MessageType.CHAT, sender);
            } else {
                double distance = Math.sqrt(player.squaredDistanceTo(senderPlayer));
                String prefix = String.format(CONFIG.distancePrefix, distance);
                Text message = CONFIG.notifyDistance ? new LiteralText(prefix).append(text) : text;

                if (shouldDoOpBypass || distance  <= CONFIG.range
                        // opUnlimitedRange is enabled and receiver is op
                        || (CONFIG.opUnlimitedRange && server.getPermissionLevel(player.getGameProfile()) >= CONFIG.opRequiredPermissionLevel)) {
                    player.sendMessage(message, MessageType.CHAT, sender);
                }
            }
        }
    }
}
