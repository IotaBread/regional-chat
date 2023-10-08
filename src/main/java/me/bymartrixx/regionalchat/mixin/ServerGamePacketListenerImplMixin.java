package me.bymartrixx.regionalchat.mixin;

import me.bymartrixx.regionalchat.RegionalChat;
import me.bymartrixx.regionalchat.access.PlayerChatMessageAccess;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.FilteredText;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Shadow
    public ServerPlayer player;

    @Inject(at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;)V"
    ), method = "method_45064", locals = LocalCapture.CAPTURE_FAILHARD) // lambda$handleChat$0
    private void broadcastMessage(PlayerChatMessage playerChatMessage, Component component, FilteredText filteredText, CallbackInfo ci, PlayerChatMessage message) {
        PlayerChatMessageAccess access = PlayerChatMessageAccess.cast(message);
        access.regional_chat$setPlayerFilter(RegionalChat.getPlayerFilter(this.player));
        access.regional_chat$setSender(this.player);
    }

    @ModifyArgs(method = "sendPlayerChatMessage", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/network/protocol/game/ClientboundPlayerChatPacket;<init>(Ljava/util/UUID;ILnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/network/chat/SignedMessageBody$Packed;Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/FilterMask;Lnet/minecraft/network/chat/ChatType$BoundNetwork;)V"))
    private void modifyPacket(Args args, PlayerChatMessage playerChatMessage, ChatType.Bound bound) {
        if (RegionalChat.CONFIG.shouldNotifyDistance()) {
            Player sender = PlayerChatMessageAccess.cast(playerChatMessage).regional_chat$getSender();
            if (sender != this.player) {
                double distance = Math.sqrt(this.player.distanceToSqr(sender));
                // chatType
                ChatType.BoundNetwork chatType = args.get(6);
                ChatType.BoundNetwork updatedChatType = new ChatType.BoundNetwork(
                        chatType.chatType(),
                        Component.literal(RegionalChat.CONFIG.getDistancePrefix().formatted(distance)).append(chatType.name()),
                        chatType.targetName());
                args.set(6, updatedChatType);
            }
        }
    }
}
