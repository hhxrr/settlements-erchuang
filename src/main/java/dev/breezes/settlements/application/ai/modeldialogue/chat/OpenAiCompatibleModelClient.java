package dev.breezes.settlements.application.ai.modeldialogue.chat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.inject.Inject;
import lombok.CustomLog;
import lombok.Getter;
import dev.breezes.settlements.application.ai.modeldialogue.ModelConfig;

import javax.annotation.Nonnull;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 调用任意 OpenAI 兼容的 chat-completions 端点（默认本地 Ollama 的 {@code /v1/chat/completions}）。
 * <p>
 * 请求/响应遵循 OpenAI schema，因此既支持云端模型也支持本地小模型（Ollama、LM Studio、vLLM 等）。
 * 任何传输错误都会以带有 {@code error} 字段的 JSON 信封兜底返回，确保对话界面始终有内容可显示。
 */
@CustomLog
public final class OpenAiCompatibleModelClient implements ModelClient {

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(120);

    @Getter
    private final ModelConfig config;
    private final HttpClient httpClient;
    private final String endpoint;

    @Inject
    public OpenAiCompatibleModelClient(@Nonnull ModelConfig config) {
        this.config = config;
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(CONNECT_TIMEOUT)
                .build();
        this.endpoint = config.normalizedBaseUrl() + (config.chatPath().startsWith("/") ? config.chatPath() : "/" + config.chatPath());
    }

    @Override
    public boolean isAvailable() {
        return this.config.enabled() && this.config.hasEndpoint();
    }

    @Override
    @Nonnull
    public CompletableFuture<String> complete(@Nonnull String systemPrompt,
                                              @Nonnull List<ChatTurn> history,
                                              @Nonnull String userText) {
        JsonObject body = new JsonObject();
        body.addProperty("model", this.config.modelName());
        body.addProperty("temperature", this.config.temperature());
        body.addProperty("max_tokens", this.config.maxTokens());
        body.addProperty("stream", false);

        JsonArray messages = new JsonArray();
        messages.add(toMessage("system", systemPrompt));
        for (ChatTurn turn : history) {
            messages.add(toMessage(turn.role(), turn.content()));
        }
        messages.add(toMessage("user", userText));
        body.add("messages", messages);

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(this.endpoint))
                .header("Content-Type", "application/json")
                .timeout(REQUEST_TIMEOUT)
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(body), StandardCharsets.UTF_8));
        if (this.config.hasApiKey()) {
            builder.header("Authorization", "Bearer " + this.config.apiKey());
        }

        return this.httpClient.sendAsync(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                .thenApply(response -> {
                    if (response.statusCode() < 200 || response.statusCode() >= 300) {
                        log.warn("Model endpoint returned HTTP {} for {}", response.statusCode(), this.endpoint);
                        return errorEnvelope("模型服务返回 HTTP " + response.statusCode());
                    }
                    return extractContent(response.body());
                })
                .exceptionally(throwable -> {
                    log.error("Model request to {} failed", this.endpoint, throwable);
                    return errorEnvelope("无法连接模型服务：" + throwable.getMessage());
                });
    }

    private static JsonObject toMessage(String role, String content) {
        JsonObject message = new JsonObject();
        message.addProperty("role", role);
        message.addProperty("content", content);
        return message;
    }

    private static String extractContent(String body) {
        try {
            JsonObject root = GSON.fromJson(body, JsonObject.class);
            if (root == null) {
                return errorEnvelope("模型返回空响应");
            }
            if (root.has("error")) {
                String msg = root.get("error").isJsonObject()
                        ? root.getAsJsonObject("error").get("message").getAsString()
                        : root.get("error").getAsString();
                return errorEnvelope("模型错误：" + msg);
            }
            JsonArray choices = root.getAsJsonArray("choices");
            if (choices == null || choices.size() == 0) {
                return errorEnvelope("模型未返回内容");
            }
            JsonObject message = choices.get(0).getAsJsonObject().getAsJsonObject("message");
            return message.get("content").getAsString();
        } catch (Exception e) {
            log.error("Failed to parse model response", e);
            return errorEnvelope("模型响应解析失败");
        }
    }

    private static String errorEnvelope(String message) {
        JsonObject envelope = new JsonObject();
        envelope.addProperty("reply", "（模型暂不可用）" + message);
        envelope.addProperty("error", message);
        return GSON.toJson(envelope);
    }
}
