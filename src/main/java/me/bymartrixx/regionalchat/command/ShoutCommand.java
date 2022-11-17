package me.bymartrixx.regionalchat.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.bymartrixx.regionalchat.RegionalChat;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.ChatType;
import net.minecraft.server.MinecraftServer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ShoutCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(
                literal("shout")
                        .requires(source -> source.hasPermission(RegionalChat.CONFIG.opRequiredPermissionLevel))
                        .then(argument("message", MessageArgument.message())
                                .executes(context -> execute(
                                        context.getSource(),
                                        MessageArgument.getChatMessage(context, "message"))
                                )
                        )
        );

        dispatcher.register(literal("sh").redirect(node));
        dispatcher.register(literal("s").redirect(node));
    }

    private static int execute(CommandSourceStack source, MessageArgument.ChatMessage message) {
        MinecraftServer server = source.getServer();

        message.resolve(source, chatMessage ->
                server.getPlayerList().broadcastChatMessage(chatMessage, source, ChatType.bind(ChatType.CHAT, source)));

        return 1;
    }
}
