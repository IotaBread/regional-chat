package me.bymartrixx.regionalchat;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RegionalChatConfig {
    public final int range;
    public final boolean notifyDistance;
    public final String distancePrefix;
    public final boolean opBypass;
    public final int opRequiredPermissionLevel;
    public final boolean opUnlimitedRange;

    public RegionalChatConfig() {
        this.range = 100;
        this.notifyDistance = true;
        this.opBypass = true;
        this.opRequiredPermissionLevel = 3;
        this.opUnlimitedRange = false;
        this.distancePrefix = "[From %.0f blocks away] ";
    }

    public RegionalChatConfig(int range, boolean notifyDistance, boolean opBypass, int requiredPermissionLevel, boolean opUnlimitedRange, String distancePrefix) {
        this.range = range;
        this.notifyDistance = notifyDistance;
        this.opBypass = opBypass;
        this.opRequiredPermissionLevel = requiredPermissionLevel;
        this.opUnlimitedRange = opUnlimitedRange;
        this.distancePrefix = distancePrefix;
    }

    public static RegionalChatConfig readConfig(Path file, Gson gson) throws IOException, JsonSyntaxException {
        if (!Files.exists(file)) {
            return null;
        }

        try (BufferedReader reader = Files.newBufferedReader(file)) {
            return gson.fromJson(reader, RegionalChatConfig.class);
        }
    }

    public static void writeConfig(Path file, Gson gson, RegionalChatConfig config) throws IOException {
        if (!Files.exists(file.getParent()) || !Files.exists(file)) {
            Files.createDirectories(file.getParent());
            Files.createFile(file);
        }

        Files.writeString(file, gson.toJson(config));
    }
}
