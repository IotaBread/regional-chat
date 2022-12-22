package me.bymartrixx.regionalchat.mixin;

import me.bymartrixx.regionalchat.access.PlayerChatMessageAccess;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Predicate;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    @Unique
    private PlayerChatMessage regional_chat$message;

    @Inject(at = @At(value = "HEAD"), method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Ljava/util/function/Predicate;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V")
    private void beforeBroadcast(PlayerChatMessage playerChatMessage, Predicate<ServerPlayer> predicate, ServerPlayer serverPlayer, ChatType.Bound bound, CallbackInfo ci) {
        regional_chat$message = playerChatMessage;
    }

    @Redirect(at = @At(value = "FIELD", target = "Lnet/minecraft/server/players/PlayerList;players:Ljava/util/List;"), method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Ljava/util/function/Predicate;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V")
    private List<ServerPlayer> getPlayers(PlayerList instance) {
        PlayerChatMessageAccess message = PlayerChatMessageAccess.cast(regional_chat$message);
        if (message.regional_chat$hasPlayerFilter()) {
            return instance.getPlayers().stream().filter(message.regional_chat$getPlayerFilter()).toList();
        }

        return instance.getPlayers();
    }
}
