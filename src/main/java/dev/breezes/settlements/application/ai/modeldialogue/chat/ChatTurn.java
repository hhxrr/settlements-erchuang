package dev.breezes.settlements.application.ai.modeldialogue.chat;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * 单条对话记录，角色为 {@code system} / {@code user} / {@code assistant}。
 */
@Immutable
public record ChatTurn(@Nonnull String role, @Nonnull String content) {

    public static ChatTurn system(@Nonnull String content) {
        return new ChatTurn("system", content);
    }

    public static ChatTurn user(@Nonnull String content) {
        return new ChatTurn("user", content);
    }

    public static ChatTurn assistant(@Nonnull String content) {
        return new ChatTurn("assistant", content);
    }
}
