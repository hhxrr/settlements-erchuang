package dev.breezes.settlements.application.ai.modeldialogue;

import dev.breezes.settlements.infrastructure.config.annotations.BehaviorConfig;
import dev.breezes.settlements.infrastructure.config.annotations.ConfigurationType;
import dev.breezes.settlements.infrastructure.config.annotations.booleans.BooleanConfig;
import dev.breezes.settlements.infrastructure.config.annotations.doubles.DoubleConfig;
import dev.breezes.settlements.infrastructure.config.annotations.integers.IntegerConfig;
import dev.breezes.settlements.infrastructure.config.annotations.strings.StringConfig;

/**
 * 村民「模型对话建造」子系统的配置。
 * <p>
 * 接入一个小模型（默认本地 Ollama 的 OpenAI 兼容端点），让玩家与村民自然语言对话：
 * 玩家可以告诉村民想造什么房子，模型返回结构化蓝图（JSON）或 ASCII 地图，
 * 由解析机制转换为可建造的 {@code BuildPlan}，再由村民逐格盖出来。
 * <p>
 * 当 {@code enabled} 为 {@code false} 或 {@code endpoint_base_url} 为空时，模组自动降级为
 * 内置的离线模拟模型（{@code OfflineMockModelClient}），无需任何外部服务即可体验全流程。
 */
@BehaviorConfig(name = "model_chat", type = ConfigurationType.INFERENCE)
public record ModelConfig(

        @BooleanConfig(
                type = ConfigurationType.INFERENCE,
                identifier = "enabled",
                description = "Enable the villager model-chat building subsystem (requires a chat-completions endpoint)",
                defaultValue = false)
        boolean enabled,

        @StringConfig(
                type = ConfigurationType.INFERENCE,
                identifier = "endpoint_base_url",
                description = "Base URL of an OpenAI-compatible chat endpoint, e.g. http://localhost:11434/v1",
                defaultValue = "http://localhost:11434/v1")
        String endpointBaseUrl,

        @StringConfig(
                type = ConfigurationType.INFERENCE,
                identifier = "api_key",
                description = "Pre-shared bearer token. Empty disables authentication",
                defaultValue = "")
        String apiKey,

        @StringConfig(
                type = ConfigurationType.INFERENCE,
                identifier = "model_name",
                description = "Model identifier sent to the endpoint, e.g. qwen2.5:3b or llama3.2",
                defaultValue = "qwen2.5:3b")
        String modelName,

        @StringConfig(
                type = ConfigurationType.INFERENCE,
                identifier = "chat_path",
                description = "Path appended to the base URL, e.g. /chat/completions (OpenAI/Ollama) or /api/chat",
                defaultValue = "/chat/completions")
        String chatPath,

        @StringConfig(
                type = ConfigurationType.INFERENCE,
                identifier = "locale",
                description = "Locale used in the villager system prompt",
                defaultValue = "zh_cn")
        String locale,

        @DoubleConfig(
                type = ConfigurationType.INFERENCE,
                identifier = "temperature",
                description = "Sampling temperature for the model",
                defaultValue = 0.8,
                min = 0.0,
                max = 2.0)
        double temperature,

        @IntegerConfig(
                type = ConfigurationType.INFERENCE,
                identifier = "max_tokens",
                description = "Maximum tokens the model may generate per reply",
                defaultValue = 1024,
                min = 16,
                max = 8192)
        int maxTokens,

        @IntegerConfig(
                type = ConfigurationType.INFERENCE,
                identifier = "max_history_turns",
                description = "How many prior user/model turns are kept per conversation",
                defaultValue = 8,
                min = 1,
                max = 40)
        int maxHistoryTurns,

        @IntegerConfig(
                type = ConfigurationType.INFERENCE,
                identifier = "build_max_blocks",
                description = "Safety cap: a single model-generated blueprint is rejected above this many blocks",
                defaultValue = 1200,
                min = 16,
                max = 8000)
        int buildMaxBlocks

) {

    public boolean hasEndpoint() {
        return this.endpointBaseUrl != null && !this.endpointBaseUrl.isBlank();
    }

    public boolean hasApiKey() {
        return this.apiKey != null && !this.apiKey.isBlank();
    }

    public String normalizedBaseUrl() {
        if (this.endpointBaseUrl == null) {
            return "";
        }
        return this.endpointBaseUrl.stripTrailing().replaceAll("/+$", "");
    }
}
