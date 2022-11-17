package me.bymartrixx.regionalchat;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Predicate;

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

    public static Predicate<ServerPlayer> getPlayerFilter(ServerPlayer senderPlayer) {
        MinecraftServer server = senderPlayer.getServer();
        boolean shouldBypass = CONFIG.opBypass && isOp(server, senderPlayer);

        if (shouldBypass) {
            return player -> true;
        }

        return player -> {
            if (player == senderPlayer) {
                return true;
            }

            if (player.getLevel() != senderPlayer.getLevel()) {
                return false;
            }

            double distance = Math.sqrt(player.distanceToSqr(senderPlayer));
            return distance <= CONFIG.range ||
                    CONFIG.opUnlimitedRange && isOp(server, player);
        };
    }

    private static boolean isOp(@Nullable MinecraftServer server, ServerPlayer player) {
        if (server == null) {
            throw new IllegalStateException("Server is null");
        }

        return server.getProfilePermissions(player.getGameProfile()) >= CONFIG.opRequiredPermissionLevel;
    }
}
