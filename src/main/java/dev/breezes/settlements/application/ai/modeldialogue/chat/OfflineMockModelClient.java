package dev.breezes.settlements.application.ai.modeldialogue.chat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.breezes.settlements.application.ai.modeldialogue.mock.MockBlueprintLibrary;
import dev.breezes.settlements.application.ai.modeldialogue.parse.BlueprintDTO;
import dev.breezes.settlements.application.ai.modeldialogue.parse.BlueprintParser;
import jakarta.inject.Inject;
import lombok.CustomLog;
import dev.breezes.settlements.application.ai.modeldialogue.ModelConfig;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 离线兜底模型：不连接任何外部服务，完全由关键词匹配 + 程序化蓝图库驱动。
 * <p>
 * 输出与真实模型一致的「JSON 信封」格式（{@code reply} + {@code blueprint} + 可选 {@code map}），
 * 因此解析机制（{@link BlueprintParser} / 地图解析）对在线与离线两条路径完全通用。
 * 当玩家未配置模型端点、或配置关闭时，由 {@code ModelDialogueModule} 选择本实现，保证全流程可玩。
 */
@CustomLog
public final class OfflineMockModelClient implements ModelClient {

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    @Nonnull
    private final ModelConfig config;
    @Nonnull
    private final BlueprintParser blueprintParser;

    @Inject
    public OfflineMockModelClient(@Nonnull ModelConfig config, @Nonnull BlueprintParser blueprintParser) {
        this.config = config;
        this.blueprintParser = blueprintParser;
    }

    @Override
    public boolean isAvailable() {
        // 离线模拟器永远可用：它是未配置在线端点时的兜底。
        return true;
    }

    @Override
    @Nonnull
    public CompletableFuture<String> complete(@Nonnull String systemPrompt,
                                              @Nonnull List<ChatTurn> history,
                                              @Nonnull String userText) {
        String lower = userText.toLowerCase();
        String archetype = detectArchetype(lower);

        JsonObject envelope = new JsonObject();
        if (archetype == null) {
            envelope.addProperty("reply",
                    "（村民擦了擦汗）我没听太懂。你可以跟我说想盖点啥，比如「帮我盖座小木屋」「来个瞭望塔」"
                            + "「围成一圈矮墙」「整片农田」，或者「中式小院」「两层小楼」也行！");
            return CompletableFuture.completedFuture(GSON.toJson(envelope));
        }

        BlueprintDTO dto = MockBlueprintLibrary.build(archetype);
        envelope.addProperty("reply",
                "（村民拍拍图纸）好嘞！这就给你盖一座" + dto.name() + "。点界面上的「批准建造」，我立马动手！");
        envelope.add("blueprint", GSON.toJsonTree(dto));
        envelope.addProperty("map", sampleMap(dto.name()));
        return CompletableFuture.completedFuture(GSON.toJson(envelope));
    }

    private static String detectArchetype(@Nonnull String lower) {
        if (lower.contains("塔") || lower.contains("瞭望") || lower.contains("tower")) return "tower";
        if (lower.contains("井") || lower.contains("水井") || lower.contains("well")) return "well";
        if (lower.contains("墙") || lower.contains("围栏") || lower.contains("围墙") || lower.contains("wall")) return "wall";
        if (lower.contains("农田") || lower.contains("农场") || lower.contains("庄稼") || lower.contains("菜") || lower.contains("farm")) return "farm";
        if (lower.contains("熔炉") || lower.contains("锻造") || lower.contains("铁匠") || lower.contains("炉") || lower.contains("forge")) return "forge";
        if (lower.contains("四合院") || lower.contains("中式") || lower.contains("院子") || lower.contains("庭院") || lower.contains("山东")) return "courtyard";
        if (lower.contains("两层") || lower.contains("二楼") || lower.contains("小楼") || lower.contains("楼房")) return "twostory";
        if (lower.contains("木屋") || lower.contains("小屋") || lower.contains("房子") || lower.contains("民房")
                || lower.contains("别墅") || lower.contains("家") || lower.contains("屋") || lower.contains("cottage") || lower.contains("house")) return "cottage";
        return null;
    }

    private static String sampleMap(@Nonnull String name) {
        return "legend: #=minecraft:oak_planks O=minecraft:oak_door G=minecraft:glass\n"
                + "#####\n"
                + "#GOG#\n"
                + "#...#\n"
                + "#G.G#\n"
                + "#####\n"
                + "（" + name + " 俯视图示，#=墙 G=窗 O=门 .=空）";
    }
}
