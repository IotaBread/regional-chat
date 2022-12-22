package me.bymartrixx.regionalchat.mixin;

import me.bymartrixx.regionalchat.RegionalChat;
import me.bymartrixx.regionalchat.access.PlayerChatMessageAccess;
import net.minecraft.network.chat.FilterMask;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.FilteredText;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.concurrent.CompletableFuture;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Shadow
    public ServerPlayer player;

    @Inject(at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;)V"
    ), method = "method_45064", locals = LocalCapture.CAPTURE_FAILHARD) // lambda$handleChat$0
    private void broadcastMessage(PlayerChatMessage playerChatMessage, CompletableFuture<?> completableFuture, CompletableFuture<?> completableFuture2, Void void_, CallbackInfo ci, PlayerChatMessage message) {
        PlayerChatMessageAccess.cast(message).regional_chat$setPlayerFilter(RegionalChat.getPlayerFilter(this.player));
    }
}
