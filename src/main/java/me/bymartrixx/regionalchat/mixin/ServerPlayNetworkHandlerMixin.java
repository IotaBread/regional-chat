package me.bymartrixx.regionalchat.mixin;

import me.bymartrixx.regionalchat.RegionalChat;
import net.minecraft.network.MessageType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;
import java.util.function.Function;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Redirect(at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Ljava/util/function/Function;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"
    ), method = "handleMessage")
    private void broadcastMessage(PlayerManager instance, Text serverMessage, Function<ServerPlayerEntity, Text> playerMessageFactory, MessageType playerMessageType, UUID sender) {
        RegionalChat.broadcastMessage(((ServerPlayNetworkHandler) (Object) this).player, serverMessage, playerMessageFactory);
    }
}
