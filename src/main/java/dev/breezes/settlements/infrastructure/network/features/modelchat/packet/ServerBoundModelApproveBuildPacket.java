package dev.breezes.settlements.infrastructure.network.features.modelchat.packet;

import dev.breezes.settlements.infrastructure.network.core.ServerBoundPacket;
import dev.breezes.settlements.shared.util.ResourceLocationUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;

/**
 * 玩家在对话界面批准一份建筑蓝图（客户端 → 服务端）。蓝图以 JSON 字节数组传输，避免超长 UTF 字符串限制。
 *
 * @param villagerId    被对话的村民实体 id
 * @param blueprintJson 经 {@link dev.breezes.settlements.application.ai.modeldialogue.parse.BlueprintParser} 可解析的蓝图 JSON
 */
public record ServerBoundModelApproveBuildPacket(int villagerId, @Nonnull String blueprintJson) implements ServerBoundPacket {

    public static final Type<ServerBoundModelApproveBuildPacket> ID =
            new Type<>(ResourceLocationUtil.mod("packet_model_approve_build_serverbound"));

    public static final StreamCodec<FriendlyByteBuf, ServerBoundModelApproveBuildPacket> CODEC =
            CustomPacketPayload.codec(ServerBoundModelApproveBuildPacket::write, ServerBoundModelApproveBuildPacket::decode);

    private static ServerBoundModelApproveBuildPacket decode(@Nonnull FriendlyByteBuf buffer) {
        int villagerId = buffer.readInt();
        String blueprintJson = new String(buffer.readByteArray(), StandardCharsets.UTF_8);
        return new ServerBoundModelApproveBuildPacket(villagerId, blueprintJson);
    }

    private void write(@Nonnull FriendlyByteBuf buffer) {
        buffer.writeInt(this.villagerId);
        buffer.writeByteArray(this.blueprintJson.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
