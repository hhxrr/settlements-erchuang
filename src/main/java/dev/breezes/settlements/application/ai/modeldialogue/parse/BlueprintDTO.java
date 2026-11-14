package dev.breezes.settlements.application.ai.modeldialogue.parse;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 模型输出的「建筑蓝图」数据结构（JSON 信封中的 {@code blueprint} 字段）。
 * <p>
 * 与 {@code BuildPlan} 的桥梁：{@link BlueprintParser#fromDto} 把本类解析为村民可逐格建造的
 * {@code BuildPlan}；{@link BlueprintParser#toDto} 则把已有的 {@code BuildPlan} 反向序列化为本类，
 * 供离线模拟器直接产出与真实模型一致的信封 JSON。
 */
public record BlueprintDTO(@Nonnull String name, @Nonnull List<BlockSpecDTO> blocks) {
}
