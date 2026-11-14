package dev.breezes.settlements.application.ai.modeldialogue.service;

import dev.breezes.settlements.application.ai.modeldialogue.ModelConfig;
import dev.breezes.settlements.application.ai.modeldialogue.chat.ChatTurn;
import dev.breezes.settlements.application.ai.modeldialogue.chat.ModelClient;
import dev.breezes.settlements.application.ai.modeldialogue.parse.BlueprintParser;
import dev.breezes.settlements.application.ai.modeldialogue.parse.ModelOutputParser;
import dev.breezes.settlements.application.ai.modeldialogue.parse.ParsedModelOutput;
import dev.breezes.settlements.infrastructure.minecraft.entities.villager.BaseVillager;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.CustomLog;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 村民「模型对话建造」的核心服务（服务端单例）。
 * <p>
 * 职责：
 * <ol>
 *   <li>维护每个（玩家 × 村民）会话的短期记忆（{@link ChatTurn} 历史）；</li>
 *   <li>把玩家的话连同系统提示交给 {@link ModelClient} 补全；</li>
 *   <li>用 {@link ModelOutputParser} 把模型自由文本解析为结构化结果；</li>
 *   <li>玩家批准建造时，把蓝图 {@link BuildOrderQueue#submit 入队}，交给村民逐格盖出。</li>
 * </ol>
 */
@CustomLog
@Singleton
public final class ModelDialogueService {

    private static final List<String> PALETTE = List.of(
            "oak_planks", "spruce_planks", "dark_oak_planks", "oak_log", "oak_door", "glass",
            "cobblestone", "stone", "stone_bricks", "bricks", "terracotta", "red_terracotta",
            "white_wool", "lantern", "oak_fence", "water", "farmland", "wheat", "furnace",
            "polished_andesite", "sandstone"
    );

    @Nonnull
    private final ModelConfig config;
    @Nonnull
    private final ModelClient modelClient;
    @Nonnull
    private final BuildOrderQueue buildOrderQueue;
    @Nonnull
    private final ModelOutputParser parser;

    private final Map<String, List<ChatTurn>> histories = new ConcurrentHashMap<>();

    @Inject
    public ModelDialogueService(@Nonnull ModelConfig config,
                               @Nonnull ModelClient modelClient,
                               @Nonnull BuildOrderQueue buildOrderQueue,
                               @Nonnull ModelOutputParser parser) {
        this.config = config;
        this.modelClient = modelClient;
        this.buildOrderQueue = buildOrderQueue;
        this.parser = parser;
    }

    /**
     * 处理玩家的一句话，返回（异步）解析后的模型结果。
     */
    @Nonnull
    public CompletableFuture<ParsedModelOutput> converse(@Nonnull ServerPlayer player,
                                                        @Nonnull BaseVillager villager,
                                                        @Nonnull String message) {
        String key = player.getUUID() + "|" + villager.getUUID();
        List<ChatTurn> history = this.histories.computeIfAbsent(key, k -> new ArrayList<>());
        this.trimHistory(history);

        if (!this.modelClient.isAvailable()) {
            return CompletableFuture.completedFuture(
                    ParsedModelOutput.chat("（村民摊手）我这会儿没法连上模型，先歇会儿吧。"));
        }

        String systemPrompt = this.buildSystemPrompt(villager);
        // history 仅包含历史轮次；本次玩家输入由 modelClient 作为 userText 追加，
        // 避免把同一句话在请求里发送两次。
        return this.modelClient.complete(systemPrompt, history, message)
                .thenApply(raw -> {
                    ParsedModelOutput output = this.parser.parse(raw, this.config);
                    history.add(ChatTurn.user(message));
                    history.add(ChatTurn.assistant(output.reply()));
                    this.trimHistory(history);
                    return output;
                });
    }

    /**
     * 玩家在对话界面批准一份蓝图后调用：解析并入队，安排村民建造。
     */
    @Nonnull
    public ParsedModelOutput enqueueBuild(@Nonnull ServerPlayer player,
                                          @Nonnull BaseVillager villager,
                                          @Nonnull String blueprintJson) {
        dev.breezes.settlements.compat.create.build.BuildPlan plan =
                BlueprintParser.create().parseJson(blueprintJson, this.config.buildMaxBlocks());
        if (plan == null) {
            return ParsedModelOutput.withError("（村民摇头）这份蓝图我看不懂，或者太大了盖不下。", "蓝图解析失败");
        }
        this.buildOrderQueue.submit(plan, player.serverLevel(), villager.blockPosition());
        return ParsedModelOutput.chat("（村民招呼同伴）收到！这就安排人把 " + plan.size() + " 个方块盖起来，等着瞧吧。");
    }

    private void trimHistory(@Nonnull List<ChatTurn> history) {
        int max = Math.max(2, this.config.maxHistoryTurns() * 2);
        while (history.size() > max) {
            history.removeFirst();
        }
    }

    @Nonnull
    private String buildSystemPrompt(@Nonnull BaseVillager villager) {
        return """
                你是一个生活在村庄里的村民建筑师，性格朴实、爱用口语，用中文和玩家聊天。
                玩家可以和你闲聊，也可以让你盖房子。当玩家想盖建筑时，你必须只输出一个 JSON 信封（不要多余解释）：
                {"reply":"<一句村民口吻的回复>","blueprint":{"name":"<建筑名>","blocks":[{"x":0,"y":0,"z":0,"block":"minecraft:oak_planks"}]},"map":"<可选的 ASCII 俯视地图，含 legend: 图例行>"}
                规则：
                - x 向右、z 向前、y 向上（0 为地面）。坐标应为非负或小的整数，结构控制在 %d 个方块以内。
                - block 必须是下列之一（可加 minecraft: 前缀）：%s。
                - 闲聊时不要输出 JSON，直接说人话。
                - 可以附带一张 ASCII 地图（legend 定义字符到方块的映射，'.' 表示空气），地图与 blueprint 二选一或都给。
                """.formatted(this.config.buildMaxBlocks(), String.join(", ", PALETTE));
    }
}
