package me.bymartrixx.regionalchat.mixin;

import me.bymartrixx.regionalchat.access.PlayerChatMessageAccess;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Predicate;

@Mixin(PlayerChatMessage.class)
public class PlayerChatMessageMixin implements PlayerChatMessageAccess {
    @Unique
    private Predicate<ServerPlayer> regional_chat$playerFilter;

    @Override
    public boolean regional_chat$hasPlayerFilter() {
        return regional_chat$playerFilter != null;
    }

    @Override
    public Predicate<ServerPlayer> regional_chat$getPlayerFilter() {
        return regional_chat$playerFilter;
    }

    @Override
    public void regional_chat$setPlayerFilter(Predicate<ServerPlayer> playerFilter) {
        regional_chat$playerFilter = playerFilter;
    }
}
