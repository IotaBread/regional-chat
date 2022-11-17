package me.bymartrixx.regionalchat.config;

import me.bymartrixx.regionalchat.RegionalChat;
import org.quiltmc.loader.api.config.QuiltConfig;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public interface RegionalChatConfig {
    int DEFAULT_RANGE = 100;
    boolean NOTIFY_DISTANCE_DEFAULT = true;
    String DEFAULT_DISTANCE_PREFIX = "[From %.0f blocks away] ";
    boolean OP_BYPASS_DEFAULT = true;
    int DEFAULT_OP_PERMISSION_LEVEL = 3;
    boolean OP_UNLIMITED_RANGE_DEFAULT = false;

    static RegionalChatConfig getConfig() {
        if (hasQuiltConfig()) {
            AtomicReference<RegionalChatConfig> ref = new AtomicReference<>();
            RegionalChatConfigImpl.config = QuiltConfig.create("regional_chat", "regional_chat", builder -> ref.set(RegionalChatConfigImpl.create(builder)));

            LegacyRegionalChatConfig legacy = LegacyRegionalChatConfig.readConfig();
            RegionalChatConfigImpl.loadLegacy(legacy);

            RegionalChatConfigImpl.config.save();
            return ref.get();
        } else {
            LegacyRegionalChatConfig config = LegacyRegionalChatConfig.readConfig();
            if (config == null) {
                config = new LegacyRegionalChatConfig();
                RegionalChat.LOGGER.info("Using default config");
                try {
                    LegacyRegionalChatConfig.writeConfig(config);
                } catch (IOException e) {
                    RegionalChat.LOGGER.error("Failed to write default config file", e);
                }
            }

            return config;
        }
    }

    private static boolean hasQuiltConfig() {
        try {
            Class.forName("org.quiltmc.loader.api.config.QuiltConfig");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    int getRange();

    boolean shouldNotifyDistance();

    String getDistancePrefix();

    boolean doOpBypass();

    int getOpRequiredPermissionLevel();

    boolean hasOpUnlimitedRange();
}
