package me.bymartrixx.regionalchat.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.bymartrixx.regionalchat.RegionalChat;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.MinecraftServer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ShoutCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(
                literal("shout")
                        .requires(source -> source.hasPermission(RegionalChat.CONFIG.getOpRequiredPermissionLevel()))
                        .then(argument("message", MessageArgument.message())
                                .executes(context -> {
                                    MessageArgument.resolveChatMessage(context, "message", message -> {
                                        execute(context.getSource(), message);
                                    });
                                    return 1;
                                })
                        )
        );

        dispatcher.register(literal("sh").redirect(node));
        dispatcher.register(literal("s").redirect(node));
    }

    private static void execute(CommandSourceStack source, PlayerChatMessage message) {
        MinecraftServer server = source.getServer();

        server.getPlayerList().broadcastChatMessage(message, source, ChatType.bind(ChatType.CHAT, source));
    }
}
