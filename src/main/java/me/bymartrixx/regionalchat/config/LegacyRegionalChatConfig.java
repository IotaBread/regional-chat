package me.bymartrixx.regionalchat.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import me.bymartrixx.regionalchat.RegionalChat;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LegacyRegionalChatConfig implements RegionalChatConfig {
    private static final String CONFIG_FILE = "regional_chat.json";
    public static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .create();

    private final int range;
    private final boolean notifyDistance;
    private final String distancePrefix;
    private final boolean opBypass;
    private final int opRequiredPermissionLevel;
    private final boolean opUnlimitedRange;

    public LegacyRegionalChatConfig() {
        this.range = DEFAULT_RANGE;
        this.notifyDistance = NOTIFY_DISTANCE_DEFAULT;
        this.distancePrefix = DEFAULT_DISTANCE_PREFIX;
        this.opBypass = OP_BYPASS_DEFAULT;
        this.opRequiredPermissionLevel = DEFAULT_OP_PERMISSION_LEVEL;
        this.opUnlimitedRange = OP_UNLIMITED_RANGE_DEFAULT;
    }

    protected static Path getFile() {
        return FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE);
    }

    public static LegacyRegionalChatConfig readConfig() {
        Path file = getFile();
        if (!Files.exists(file)) {
            return null;
        }

        try (BufferedReader reader = Files.newBufferedReader(file)) {
            return GSON.fromJson(reader, LegacyRegionalChatConfig.class);
        } catch (IOException e) {
            RegionalChat.LOGGER.error("Failed to read config file", e);
            return null;
        } catch (JsonSyntaxException e) {
            RegionalChat.LOGGER.error("Failed to parse config file", e);
            return null;
        }
    }

    public static void writeConfig(LegacyRegionalChatConfig config) throws IOException {
        Path file = getFile();
        if (!Files.exists(file.getParent()) || !Files.exists(file)) {
            Files.createDirectories(file.getParent());
            Files.createFile(file);
        }

        Files.writeString(file, GSON.toJson(config));
    }

    @Override
    public int getRange() {
        return this.range;
    }

    @Override
    public boolean shouldNotifyDistance() {
        return this.notifyDistance;
    }

    @Override
    public String getDistancePrefix() {
        return this.distancePrefix;
    }

    @Override
    public boolean doOpBypass() {
        return this.opBypass;
    }

    @Override
    public int getOpRequiredPermissionLevel() {
        return this.opRequiredPermissionLevel;
    }

    @Override
    public boolean hasOpUnlimitedRange() {
        return this.opUnlimitedRange;
    }
}
