package dev.breezes.settlements.application.ai.modeldialogue.parse;

import dev.breezes.settlements.compat.create.build.BuildPlan;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * 解析模型输出后的统一结果：一段自然语言回复 + 可选的「可建造蓝图」+ 可选的「地图文本」+ 可选的错误。
 */
public record ParsedModelOutput(@Nonnull String reply,
                               @Nonnull Optional<BuildPlan> blueprint,
                               @Nonnull Optional<String> mapText,
                               @Nonnull Optional<String> error) {

    public static ParsedModelOutput chat(@Nonnull String reply) {
        return new ParsedModelOutput(reply, Optional.empty(), Optional.empty(), Optional.empty());
    }

    public static ParsedModelOutput withError(@Nonnull String reply, @Nonnull String error) {
        return new ParsedModelOutput(reply, Optional.empty(), Optional.empty(), Optional.of(error));
    }

    public static ParsedModelOutput withBlueprint(@Nonnull String reply,
                                                 @Nonnull BuildPlan blueprint,
                                                 @Nonnull Optional<String> mapText) {
        return new ParsedModelOutput(reply, Optional.of(blueprint), mapText, Optional.empty());
    }

    @Nullable
    public String errorOrNull() {
        return this.error().orElse(null);
    }
}
