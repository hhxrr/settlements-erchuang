package dev.breezes.settlements.application.ai.modeldialogue.di;

import dagger.Module;
import dagger.Provides;
import dev.breezes.settlements.application.ai.modeldialogue.ModelConfig;
import dev.breezes.settlements.application.ai.modeldialogue.chat.ModelClient;
import dev.breezes.settlements.application.ai.modeldialogue.chat.OfflineMockModelClient;
import dev.breezes.settlements.application.ai.modeldialogue.chat.OpenAiCompatibleModelClient;
import dev.breezes.settlements.application.ai.modeldialogue.parse.BlueprintParser;
import dev.breezes.settlements.application.ai.modeldialogue.parse.MapParser;
import dev.breezes.settlements.application.ai.modeldialogue.parse.ModelOutputParser;
import dev.breezes.settlements.application.ai.modeldialogue.service.BuildOrderQueue;

import jakarta.inject.Singleton;

/**
 * 把「模型对话建造」子系统的服务装配进 Dagger 图。
 * <p>
 * 关键点：{@link ModelClient} 按配置二选一——
 * 配置了可用端点时走 {@link OpenAiCompatibleModelClient}（本地 Ollama / 任意 OpenAI 兼容服务），
 * 否则降级为 {@link OfflineMockModelClient}，无需任何外部依赖即可体验全流程。
 */
@Module
public final class ModelDialogueModule {

    @Provides
    @Singleton
    static ModelClient modelClient(ModelConfig config, BlueprintParser blueprintParser) {
        if (config.enabled() && config.hasEndpoint()) {
            return new OpenAiCompatibleModelClient(config);
        }
        return new OfflineMockModelClient(config, blueprintParser);
    }

    @Provides
    @Singleton
    static BuildOrderQueue buildOrderQueue() {
        return new BuildOrderQueue();
    }

    @Provides
    @Singleton
    static BlueprintParser blueprintParser() {
        return BlueprintParser.create();
    }

    @Provides
    @Singleton
    static MapParser mapParser() {
        return MapParser.create();
    }

    @Provides
    @Singleton
    static ModelOutputParser modelOutputParser(BlueprintParser blueprintParser, MapParser mapParser) {
        return ModelOutputParser.create(blueprintParser, mapParser);
    }

    // ModelDialogueService 使用 @Inject 构造器，并已在模块图中提供其全部依赖
    // （ModelConfig 来自 ConfigModule；ModelClient/BuildOrderQueue/ModelOutputParser 见上方），
    // 因此这里不再用 @Provides 重复绑定，避免 Dagger 重复绑定（DuplicateBindings）报错。
}
