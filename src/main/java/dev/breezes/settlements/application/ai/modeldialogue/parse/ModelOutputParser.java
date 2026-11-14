package dev.breezes.settlements.application.ai.modeldialogue.parse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.breezes.settlements.application.ai.modeldialogue.ModelConfig;
import lombok.CustomLog;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * 解析机制核心：把模型返回的自由文本转换为结构化结果 {@link ParsedModelOutput}。
 * <p>
 * 解析策略（对真实模型与离线模拟器通用）：
 * <ol>
 *   <li>剥离 ```json 代码块围栏，抽取最外层 JSON 对象；</li>
 *   <li>若含 {@code blueprint} 字段 → 交给 {@link BlueprintParser} 解析为 {@link BuildPlan}；</li>
 *   <li>若含 {@code map} 字段 → 交给 {@link MapParser} 解析为 {@link BuildPlan}；</li>
 *   <li>否则把整段文本当作普通聊天回复。</li>
 * </ol>
 * 任一环节失败都降级为「只显示回复」，保证对话界面不会因为模型格式偏差而崩溃。
 */
@CustomLog
public final class ModelOutputParser {

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    private final BlueprintParser blueprintParser;
    private final MapParser mapParser;

    public ModelOutputParser(@Nonnull BlueprintParser blueprintParser, @Nonnull MapParser mapParser) {
        this.blueprintParser = blueprintParser;
        this.mapParser = mapParser;
    }

    @Nonnull
    public static ModelOutputParser create(@Nonnull BlueprintParser blueprintParser, @Nonnull MapParser mapParser) {
        return new ModelOutputParser(blueprintParser, mapParser);
    }

    @Nonnull
    public ParsedModelOutput parse(@Nonnull String raw, @Nonnull ModelConfig config) {
        String cleaned = stripFences(raw).trim();
        if (cleaned.isEmpty()) {
            return ParsedModelOutput.chat("(模型返回为空)");
        }

        String json = extractJsonObject(cleaned);
        if (json == null) {
            // 没有结构化 JSON：作为普通闲聊回复
            return ParsedModelOutput.chat(cleaned);
        }

        try {
            JsonObject envelope = JsonParser.parseString(json).getAsJsonObject();
            String reply = envelope.has("reply")
                    ? envelope.get("reply").getAsString()
                    : envelope.has("content") ? envelope.get("content").getAsString() : cleaned;
            String error = envelope.has("error") ? envelope.get("error").getAsString() : null;

            Optional<dev.breezes.settlements.compat.create.build.BuildPlan> blueprint = Optional.empty();
            if (envelope.has("blueprint") && envelope.get("blueprint").isJsonObject()) {
                String blueprintJson = GSON.toJson(envelope.getAsJsonObject("blueprint"));
                dev.breezes.settlements.compat.create.build.BuildPlan plan = this.blueprintParser.parseJson(blueprintJson, config.buildMaxBlocks());
                if (plan == null) {
                    reply += "（蓝图过大或非法，已被拒绝）";
                } else {
                    blueprint = Optional.of(plan);
                }
            } else if (envelope.has("blocks") && envelope.get("blocks").isJsonArray()) {
                // 模型直接返回了蓝图对象（无 envelope 包裹）
                dev.breezes.settlements.compat.create.build.BuildPlan plan = this.blueprintParser.parseJson(json, config.buildMaxBlocks());
                if (plan != null) {
                    blueprint = Optional.of(plan);
                }
            }

            Optional<String> mapText = Optional.empty();
            if (envelope.has("map") && envelope.get("map").isJsonPrimitive()) {
                String map = envelope.get("map").getAsString();
                if (!map.isBlank()) {
                    mapText = Optional.of(map);
                    dev.breezes.settlements.compat.create.build.BuildPlan mapPlan = this.mapParser.parse(map, config.buildMaxBlocks());
                    if (mapPlan != null) {
                        blueprint = blueprint.isPresent() ? blueprint : Optional.of(mapPlan);
                    }
                }
            }

            if (error != null) {
                return ParsedModelOutput.withError(reply, error);
            }
            if (blueprint.isPresent()) {
                return ParsedModelOutput.withBlueprint(reply, blueprint.get(), mapText);
            }
            return ParsedModelOutput.chat(reply);
        } catch (Exception e) {
            log.error("Failed to parse model envelope", e);
            return ParsedModelOutput.chat(cleaned);
        }
    }

    /**
     * 抽取最外层平衡的花括号 JSON 对象；找不到返回 {@code null}。
     */
    private static String extractJsonObject(@Nonnull String text) {
        int start = text.indexOf('{');
        if (start < 0) {
            return null;
        }
        int depth = 0;
        boolean inString = false;
        for (int i = start; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '"' && (i == 0 || text.charAt(i - 1) != '\\')) {
                inString = !inString;
            } else if (!inString && c == '{') {
                depth++;
            } else if (!inString && c == '}') {
                depth--;
                if (depth == 0) {
                    return text.substring(start, i + 1);
                }
            }
        }
        return null;
    }

    private static String stripFences(@Nonnull String text) {
        String[] fences = {"```json", "```"};
        for (String fence : fences) {
            int idx = text.indexOf(fence);
            if (idx >= 0) {
                int end = text.indexOf("```", idx + fence.length());
                if (end > idx) {
                    return text.substring(idx + fence.length(), end);
                }
            }
        }
        return text;
    }
}
