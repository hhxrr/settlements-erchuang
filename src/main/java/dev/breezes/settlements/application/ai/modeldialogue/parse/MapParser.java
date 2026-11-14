package dev.breezes.settlements.application.ai.modeldialogue.parse;

import dev.breezes.settlements.compat.create.build.BuildPlan;
import lombok.CustomLog;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 把模型生成的「ASCII 俯视地图」解析为 {@link BuildPlan}。
 * <p>
 * 约定格式（模型在 {@code map} 字段中输出）：
 * <pre>
 * legend: #=minecraft:oak_planks O=minecraft:oak_door .=air
 * #####
 * #...#
 * #...#
 * #####
 * </pre>
 * 未提供 {@code legend:} 时使用内置默认图例（{@code #}木板、{@code O}木门、{@code X}石头等）。
 * 空行表示抬升一层（y+1）；也可用 {@code y=N} 显式指定当前层高度。列沿 X、行沿 Z 展开。
 */
@CustomLog
public final class MapParser {

    private static final Map<Character, String> DEFAULT_LEGEND = Map.ofEntries(
            Map.entry('#', "minecraft:oak_planks"),
            Map.entry('O', "minecraft:oak_door"),
            Map.entry('T', "minecraft:oak_log"),
            Map.entry('X', "minecraft:stone"),
            Map.entry('S', "minecraft:cobblestone"),
            Map.entry('B', "minecraft:bricks"),
            Map.entry('W', "minecraft:white_wool"),
            Map.entry('G', "minecraft:glass"),
            Map.entry('D', "minecraft:dark_oak_planks"),
            Map.entry('L', "minecraft:lantern"),
            Map.entry('R', "minecraft:red_terracotta"),
            Map.entry('C', "minecraft:terracotta")
    );

    private MapParser() {
    }

    @Nonnull
    public static MapParser create() {
        return new MapParser();
    }

    public BuildPlan parse(@Nonnull String mapText, int maxBlocks) {
        Map<Character, String> legend = new LinkedHashMap<>(DEFAULT_LEGEND);
        List<BuildPlan.PlaceEntry> entries = new ArrayList<>();

        int currentY = 0;
        int z = 0;
        for (String rawLine : mapText.split("\\R")) {
            String line = rawLine.strip();
            if (line.isEmpty()) {
                currentY++;
                continue;
            }
            if (line.toLowerCase().startsWith("legend:")) {
                parseLegend(line.substring("legend:".length()), legend);
                continue;
            }
            if (line.toLowerCase().startsWith("y=")) {
                try {
                    currentY = Integer.parseInt(line.substring(2).strip());
                } catch (NumberFormatException ignored) {
                    log.warn("Malformed map layer marker '{}', ignored", line);
                }
                continue;
            }
            int x = 0;
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                if (c == ' ' || c == '.') {
                    x++;
                    continue;
                }
                String blockId = legend.get(c);
                if (blockId == null) {
                    x++;
                    continue;
                }
                BlockState state = BlueprintParser.create().resolveBlockUnchecked(blockId);
                if (state != null) {
                    entries.add(new BuildPlan.PlaceEntry(new BlockPos(x, currentY, z), state));
                }
                x++;
            }
            z++;
        }

        if (entries.isEmpty()) {
            return null;
        }
        if (entries.size() > maxBlocks) {
            log.warn("Map rejected: {} blocks exceeds cap {}", entries.size(), maxBlocks);
            return null;
        }
        return new BuildPlan(entries, entries.size());
    }

    private static void parseLegend(@Nonnull String spec, @Nonnull Map<Character, String> legend) {
        for (String pair : spec.split("\\s+")) {
            if (pair.isEmpty() || !pair.contains("=")) {
                continue;
            }
            String[] kv = pair.split("=", 2);
            if (kv[0].length() == 1) {
                legend.put(kv[0].charAt(0), kv[1].strip());
            }
        }
    }
}
