package me.bymartrixx.regionalchat.mixin;

import me.bymartrixx.regionalchat.RegionalChat;
import me.bymartrixx.regionalchat.access.PlayerChatMessageAccess;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignatureCache;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.SignedMessageBody;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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
        PlayerChatMessageAccess access = PlayerChatMessageAccess.cast(message);
        access.regional_chat$setPlayerFilter(RegionalChat.getPlayerFilter(this.player));
        access.regional_chat$setSender(this.player);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/PlayerChatMessage;unsignedContent()Lnet/minecraft/network/chat/Component;"), method = "sendPlayerChatMessage")
    private Component addContentDistancePrefix(PlayerChatMessage instance) {
        Component content = instance.unsignedContent();
        if (RegionalChat.CONFIG.shouldNotifyDistance()) {
            Player sender = PlayerChatMessageAccess.cast(instance).regional_chat$getSender();
            if (sender != this.player) {
                double distance = Math.sqrt(this.player.distanceToSqr(sender));
                return content != null ? Component.literal(RegionalChat.CONFIG.getDistancePrefix().formatted(distance)).append(content) : null;
            }
        }

        return content;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/SignedMessageBody;pack(Lnet/minecraft/network/chat/MessageSignatureCache;)Lnet/minecraft/network/chat/SignedMessageBody$Packed;"), method = "sendPlayerChatMessage")
    private SignedMessageBody.Packed addSignedBodyDistancePrefix(SignedMessageBody instance, MessageSignatureCache messageSignatureCache, PlayerChatMessage playerChatMessage, ChatType.Bound bound) {
        String content = instance.content();
        if (RegionalChat.CONFIG.shouldNotifyDistance()) {
            Player sender = PlayerChatMessageAccess.cast(playerChatMessage).regional_chat$getSender();
            if (sender != this.player) {
                double distance = Math.sqrt(this.player.distanceToSqr(sender));
                content = RegionalChat.CONFIG.getDistancePrefix().formatted(distance) + content;
            }
        }

        return new SignedMessageBody.Packed(content, instance.timeStamp(), instance.salt(), instance.lastSeen().pack(messageSignatureCache));
    }
}
