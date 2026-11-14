package dev.breezes.settlements.infrastructure.network.features.modelchat.handler;

import dev.breezes.settlements.application.ai.modeldialogue.parse.BlueprintParser;
import dev.breezes.settlements.application.ai.modeldialogue.parse.ParsedModelOutput;
import dev.breezes.settlements.application.ai.modeldialogue.service.ModelDialogueService;
import dev.breezes.settlements.infrastructure.minecraft.entities.villager.BaseVillager;
import dev.breezes.settlements.infrastructure.network.core.ServerSidePacketHandler;
import dev.breezes.settlements.infrastructure.network.features.modelchat.packet.ClientBoundModelChatPacket;
import dev.breezes.settlements.infrastructure.network.features.modelchat.packet.ServerBoundModelApproveBuildPacket;
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
 * 处理玩家「批准建造」：把蓝图 JSON 解析为 {@code BuildPlan} 并入队，安排村民施工。
 */
@CustomLog
@AllArgsConstructor(access = AccessLevel.PACKAGE, onConstructor_ = @Inject)
public class ServerBoundModelApproveBuildPacketHandler implements ServerSidePacketHandler<ServerBoundModelApproveBuildPacket> {

    private final ModelDialogueService dialogueService;
    private final BlueprintParser blueprintParser;

    @Override
    public void runOnServer(@Nonnull IPayloadContext context, @Nonnull ServerBoundModelApproveBuildPacket packet) {
        if (!(context.player() instanceof ServerPlayer player) || !player.isAlive() || player.isSpectator()) {
            return;
        }
        Entity target = player.serverLevel().getEntity(packet.villagerId());
        if (!(target instanceof BaseVillager villager) || !villager.isAlive() || villager.isRemoved()) {
            return;
        }

        int villagerId = packet.villagerId();
        ParsedModelOutput result = this.dialogueService.enqueueBuild(player, villager, packet.blueprintJson());
        String blueprintJson = result.blueprint().map(this.blueprintParser::toJson).orElse("");
        PacketDistributor.sendToPlayer(player, new ClientBoundModelChatPacket(
                villagerId, -1, result.reply(), result.mapText().orElse(""), blueprintJson,
                result.blueprint().isPresent(), result.error().orElse("")));
    }
}
