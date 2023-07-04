package me.bymartrixx.regionalchat;

import me.bymartrixx.regionalchat.config.RegionalChatConfig;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class RegionalChat implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();
    public static RegionalChatConfig CONFIG;

    @Override
    public void onInitialize() {
        CONFIG = RegionalChatConfig.getConfig();
    }

    public static Predicate<ServerPlayer> getPlayerFilter(ServerPlayer senderPlayer) {
        MinecraftServer server = senderPlayer.getServer();
        boolean shouldBypass = CONFIG.doOpBypass() && isOp(server, senderPlayer);

        if (shouldBypass) {
            return player -> true;
        }

        return player -> {
            if (player == senderPlayer) {
                return true;
            }

            if (player.level() != senderPlayer.level()) {
                return false;
            }

            double distance = Math.sqrt(player.distanceToSqr(senderPlayer));
            return distance <= CONFIG.getRange() ||
                    CONFIG.hasOpUnlimitedRange() && isOp(server, player);
        };
    }

    private static boolean isOp(@Nullable MinecraftServer server, ServerPlayer player) {
        if (server == null) {
            throw new IllegalStateException("Server is null");
        }

        return server.getProfilePermissions(player.getGameProfile()) >= CONFIG.getOpRequiredPermissionLevel();
    }
}
