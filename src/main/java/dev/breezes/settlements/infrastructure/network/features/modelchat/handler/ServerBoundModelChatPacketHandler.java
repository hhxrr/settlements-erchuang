package dev.breezes.settlements.infrastructure.network.features.modelchat.handler;

import dev.breezes.settlements.application.ai.modeldialogue.parse.BlueprintParser;
import dev.breezes.settlements.application.ai.modeldialogue.parse.ParsedModelOutput;
import dev.breezes.settlements.application.ai.modeldialogue.service.ModelDialogueService;
import dev.breezes.settlements.infrastructure.minecraft.entities.villager.BaseVillager;
import dev.breezes.settlements.infrastructure.network.core.ServerSidePacketHandler;
import dev.breezes.settlements.infrastructure.network.features.modelchat.packet.ClientBoundModelChatPacket;
import dev.breezes.settlements.infrastructure.network.features.modelchat.packet.ServerBoundModelChatPacket;
import jakarta.inject.Inject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;

/**
 * 处理玩家发来的对话消息：交给 {@link ModelDialogueService} 走模型 + 解析，再把结果回传客户端。
 */
@CustomLog
@AllArgsConstructor(access = AccessLevel.PACKAGE, onConstructor_ = @Inject)
public class ServerBoundModelChatPacketHandler implements ServerSidePacketHandler<ServerBoundModelChatPacket> {

    private final ModelDialogueService dialogueService;
    private final BlueprintParser blueprintParser;

    @Override
    public void runOnServer(@Nonnull IPayloadContext context, @Nonnull ServerBoundModelChatPacket packet) {
        if (!(context.player() instanceof ServerPlayer player) || !player.isAlive() || player.isSpectator()) {
            return;
        }
        Entity target = player.serverLevel().getEntity(packet.villagerId());
        if (!(target instanceof BaseVillager villager) || !villager.isAlive() || villager.isRemoved()) {
            return;
        }

        int villagerId = packet.villagerId();
        int turnId = packet.turnId();
        this.dialogueService.converse(player, villager, packet.message())
                .whenComplete((result, ex) -> player.server.execute(() -> {
                    if (ex != null) {
                        log.error("Model dialogue failed", ex);
                        PacketDistributor.sendToPlayer(player, new ClientBoundModelChatPacket(
                                villagerId, turnId, "（村民愣住了）出了点岔子……", "", "", false, String.valueOf(ex.getMessage())));
                        return;
                    }
                    this.sendResult(player, villagerId, turnId, result);
                }));
    }

    private void sendResult(@Nonnull ServerPlayer player, int villagerId, int turnId, @Nonnull ParsedModelOutput result) {
        String blueprintJson = result.blueprint()
                .map(this.blueprintParser::toJson)
                .orElse("");
        PacketDistributor.sendToPlayer(player, new ClientBoundModelChatPacket(
                villagerId,
                turnId,
                result.reply(),
                result.mapText().orElse(""),
                blueprintJson,
                result.blueprint().isPresent(),
                result.error().orElse("")));
    }
}
