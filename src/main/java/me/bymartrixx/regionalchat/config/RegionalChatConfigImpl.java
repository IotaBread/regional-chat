package me.bymartrixx.regionalchat.config;

import me.bymartrixx.regionalchat.RegionalChat;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.Constraint;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.values.TrackedValue;

import java.io.IOException;
import java.nio.file.Files;

public class RegionalChatConfigImpl implements RegionalChatConfig {
    private static TrackedValue<Integer> RANGE;
    private static TrackedValue<Boolean> NOTIFY_DISTANCE;
    private static TrackedValue<String> DISTANCE_PREFIX;
    private static TrackedValue<Boolean> OP_BYPASS;
    private static TrackedValue<Integer> OP_REQUIRED_PERMISSION_LEVEL;
    private static TrackedValue<Boolean> OP_UNLIMITED_RANGE;

    protected static Config config;

    private RegionalChatConfigImpl() {
    }

    public static RegionalChatConfigImpl create(Config.Builder builder) {
        builder.field(RANGE = TrackedValue.create(DEFAULT_RANGE, "range", creator -> {
            creator.constraint(Constraint.range(0, Short.MAX_VALUE)); // could theoretically be Integer.MAX_VALUE, but we probably don't want that
            creator.metadata(Comment.TYPE, comments -> comments.add("The range in blocks a player can talk and be heard"));
        }));
        builder.field(NOTIFY_DISTANCE = TrackedValue.create(NOTIFY_DISTANCE_DEFAULT, "notifyDistance", creator ->
                creator.metadata(Comment.TYPE, comments -> comments.add("Whether to notify players about the distance a message was sent from"))));
        builder.field(DISTANCE_PREFIX = TrackedValue.create(DEFAULT_DISTANCE_PREFIX, "distancePrefix", creator ->
                creator.metadata(Comment.TYPE, comments -> comments.add("The name prefix to use when notifying players about the distance a message was sent from.\n" +
                        "Since 1.20.2 the prefix is added to the username, ie. '<Player123> hello' becomes <{prefix}Player123> hello'"))));
        builder.field(OP_BYPASS = TrackedValue.create(OP_BYPASS_DEFAULT, "opBypass", creator ->
                creator.metadata(Comment.TYPE, comments -> comments.add("Whether to allow operators to bypass the range limit"))));
        builder.field(OP_REQUIRED_PERMISSION_LEVEL = TrackedValue.create(DEFAULT_OP_PERMISSION_LEVEL, "opRequiredPermissionLevel", creator -> {
            creator.constraint(Constraint.range(0, 4));
            creator.metadata(Comment.TYPE, comments -> comments.add("The permission level required to bypass the range limit"));
        }));
        builder.field(OP_UNLIMITED_RANGE = TrackedValue.create(OP_UNLIMITED_RANGE_DEFAULT, "opUnlimitedRange", creator ->
                creator.metadata(Comment.TYPE, comments -> comments.add("Whether operators should have an unlimited hearing range"))));

        return new RegionalChatConfigImpl();
    }

    public static void loadLegacy(@Nullable LegacyRegionalChatConfig legacyConfig) {
        if (legacyConfig != null) {
            RegionalChat.LOGGER.info("Importing settings from legacy configuration file");

            RANGE.setValue(legacyConfig.getRange(), false);
            NOTIFY_DISTANCE.setValue(legacyConfig.shouldNotifyDistance(), false);
            DISTANCE_PREFIX.setValue(legacyConfig.getDistancePrefix(), false);
            OP_BYPASS.setValue(legacyConfig.doOpBypass(), false);
            OP_REQUIRED_PERMISSION_LEVEL.setValue(legacyConfig.getOpRequiredPermissionLevel(), false);
            OP_UNLIMITED_RANGE.setValue(legacyConfig.hasOpUnlimitedRange(), false);

            try {
                Files.deleteIfExists(LegacyRegionalChatConfig.getFile());
            } catch (IOException e) {
                RegionalChat.LOGGER.error("Error deleting legacy configuration file. Please delete it manually", e);
            }
        }
    }

    @Override
    public int getRange() {
        return RANGE.getRealValue();
    }

    @Override
    public boolean shouldNotifyDistance() {
        return NOTIFY_DISTANCE.getRealValue();
    }

    @Override
    public String getDistancePrefix() {
        return DISTANCE_PREFIX.getRealValue();
    }

    @Override
    public boolean doOpBypass() {
        return OP_BYPASS.getRealValue();
    }

    @Override
    public int getOpRequiredPermissionLevel() {
        return OP_REQUIRED_PERMISSION_LEVEL.getRealValue();
    }

    @Override
    public boolean hasOpUnlimitedRange() {
        return OP_UNLIMITED_RANGE.getRealValue();
    }
}
