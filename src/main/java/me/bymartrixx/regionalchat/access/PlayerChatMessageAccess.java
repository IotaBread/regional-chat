package me.bymartrixx.regionalchat.access;

import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Predicate;

public interface PlayerChatMessageAccess {
    boolean regional_chat$hasPlayerFilter();

    Predicate<ServerPlayer> regional_chat$getPlayerFilter();

    void regional_chat$setPlayerFilter(Predicate<ServerPlayer> playerFilter);

    ServerPlayer regional_chat$getSender();

    void regional_chat$setSender(ServerPlayer player);

    static PlayerChatMessageAccess cast(PlayerChatMessage message) {
        return (PlayerChatMessageAccess) (Object) message;
    }
}
