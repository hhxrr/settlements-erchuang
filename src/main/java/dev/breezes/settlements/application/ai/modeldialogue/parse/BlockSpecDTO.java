package dev.breezes.settlements.application.ai.modeldialogue.parse;

import javax.annotation.Nonnull;

/**
 * 模型输出的单个方块规格（相对坐标 + 方块 ID）。由 {@link BlueprintParser} 转换为 {@code BlockState}。
 */
public record BlockSpecDTO(int x, int y, int z, @Nonnull String block) {
}
