package dev.breezes.settlements.application.ai.modeldialogue.parse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.breezes.settlements.compat.create.build.BuildPlan;
import lombok.CustomLog;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * 把模型输出的「蓝图 JSON」解析为村民可逐格建造的 {@link BuildPlan}。
 * <p>
 * 模型应保证方块 ID 为合法注册名（如 {@code minecraft:oak_planks} 或 {@code oak_planks}）；
 * 无法解析的方块会被跳过并记日志，避免建造中断。单份蓝图方块数超过 {@code maxBlocks} 上限时返回 {@code null}（交由上层拒绝）。
 */
@CustomLog
public final class BlueprintParser {

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    private BlueprintParser() {
    }

    @Nonnull
    public static BlueprintParser create() {
        return new BlueprintParser();
    }

    /**
     * 解析蓝图信封 JSON 字符串（可能来自模型原始输出中抽出的 {@code "blueprint": {...}} 片段）。
     *
     * @return 解析成功返回 {@link BuildPlan}；方块数超限或 JSON 非法时返回 {@code null}
     */
    public BuildPlan parseJson(@Nonnull String json, int maxBlocks) {
        try {
            BlueprintDTO dto = GSON.fromJson(json, BlueprintDTO.class);
            if (dto == null || dto.blocks() == null || dto.blocks().isEmpty()) {
                return null;
            }
            return fromDto(dto, maxBlocks);
        } catch (Exception e) {
            log.error("Failed to parse blueprint JSON", e);
            return null;
        }
    }

    public BuildPlan fromDto(@Nonnull BlueprintDTO dto, int maxBlocks) {
        List<BlockSpecDTO> specs = dto.blocks();
        if (specs.size() > maxBlocks) {
            log.warn("Blueprint rejected: {} blocks exceeds cap {}", specs.size(), maxBlocks);
            return null;
        }

        List<BuildPlan.PlaceEntry> entries = new ArrayList<>(specs.size());
        for (BlockSpecDTO spec : specs) {
            BlockState state = resolveBlock(spec.block());
            if (state == null) {
                continue;
            }
            entries.add(new BuildPlan.PlaceEntry(new BlockPos(spec.x(), spec.y(), spec.z()), state));
        }
        return new BuildPlan(entries, entries.size());
    }

    /**
     * 把已有的 {@link BuildPlan} 反向序列化为蓝图 DTO（供离线模拟器产出与真实模型一致的信封 JSON）。
     */
    @Nonnull
    public BlueprintDTO toDto(@Nonnull BuildPlan plan) {
        List<BlockSpecDTO> specs = new ArrayList<>(plan.size());
        for (BuildPlan.PlaceEntry entry : plan.entries()) {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(entry.state().getBlock());
            specs.add(new BlockSpecDTO(
                    entry.offset().getX(),
                    entry.offset().getY(),
                    entry.offset().getZ(),
                    id.toString()));
        }
        return new BlueprintDTO("blueprint", specs);
    }

    @Nonnull
    public String toJson(@Nonnull BuildPlan plan) {
        return GSON.toJson(toDto(plan));
    }

    /**
     * 公开给 {@link MapParser} 复用的方块解析（不合法 ID 返回 {@code null}）。
     */
    public BlockState resolveBlockUnchecked(@Nonnull String id) {
        return resolveBlock(id);
    }

    private static BlockState resolveBlock(@Nonnull String id) {
        String normalized = id.strip();
        if (normalized.isEmpty() || normalized.equals(".") || normalized.equalsIgnoreCase("air")
                || normalized.equalsIgnoreCase("minecraft:air")) {
            return null;
        }
        ResourceLocation rl;
        try {
            rl = ResourceLocation.parse(normalized);
        } catch (Exception e) {
            log.warn("Invalid block id '{}', skipping", normalized);
            return null;
        }
        Block block = BuiltInRegistries.BLOCK.get(rl);
        if (block == Blocks.AIR) {
            return null;
        }
        return block.defaultBlockState();
    }
}
