package dev.breezes.settlements.application.ai.modeldialogue.chat;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 一个「小模型」客户端抽象：给定系统提示、历史对话和玩家最新一句话，返回模型的原始文本输出。
 * <p>
 * 实现可以是本地 Ollama / 任意 OpenAI 兼容端点（{@code OpenAiCompatibleModelClient}），
 * 也可以是完全离线的内置模拟器（{@code OfflineMockModelClient}），保证无外部依赖时整套流程可用。
 */
public interface ModelClient {

    /**
     * @return 该客户端当前是否可用（例如离线模拟器始终可用；在线客户端需要已配置端点）。
     */
    boolean isAvailable();

    /**
     * 向模型发送一次对话补全请求。
     *
     * @param systemPrompt 系统提示（村民人设 + 方块调色板 + 输出契约）
     * @param history      历史对话（不含本次 user 句）
     * @param userText     玩家本次输入
     * @return 模型原始输出文本（可能包含结构化 JSON 信封 / 代码块，由解析器处理）
     */
    @Nonnull
    CompletableFuture<String> complete(@Nonnull String systemPrompt,
                                       @Nonnull List<ChatTurn> history,
                                       @Nonnull String userText);
}
