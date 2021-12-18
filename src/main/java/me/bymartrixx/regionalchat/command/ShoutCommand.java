package me.bymartrixx.regionalchat.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.bymartrixx.regionalchat.RegionalChat;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ShoutCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> node = dispatcher.register(
                literal("shout")
                        .requires(source -> source.hasPermissionLevel(RegionalChat.CONFIG.opRequiredPermissionLevel))
                        .then(argument("message", MessageArgumentType.message())
                                .executes(context -> execute(
                                        context.getSource(),
                                        MessageArgumentType.getMessage(context, "message"))
                                )
                        )
        );

        dispatcher.register(literal("sh").redirect(node));
        dispatcher.register(literal("s").redirect(node));
    }

    private static int execute(ServerCommandSource source, Text message) {
        Entity entity = source.getEntity();
        UUID sender = entity == null ? Util.NIL_UUID : entity.getUuid();
        MinecraftServer server = source.getServer();
        Text displayName = entity != null ? entity.getDisplayName() : null;
        Text text = new TranslatableText("chat.type.text", displayName, message);

        server.getPlayerManager().broadcastChatMessage(text, MessageType.CHAT, sender);

        return 1;
    }
}
