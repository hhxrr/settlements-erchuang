package dev.breezes.settlements.infrastructure.network.features.modelchat.packet;

import dev.breezes.settlements.infrastructure.network.core.ServerBoundPacket;
import dev.breezes.settlements.shared.util.ResourceLocationUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import javax.annotation.Nonnull;

/**
 * 玩家在对话界面发送的一句话（客户端 → 服务端）。
 *
 * @param villagerId 被对话的村民实体 id
 * @param message    玩家输入
 * @param turnId     本轮序号（用于客户端将回复对应到正确的对话）
 */
public record ServerBoundModelChatPacket(int villagerId, @Nonnull String message, int turnId) implements ServerBoundPacket {

    public static final Type<ServerBoundModelChatPacket> ID =
            new Type<>(ResourceLocationUtil.mod("packet_model_chat_serverbound"));

    public static final StreamCodec<FriendlyByteBuf, ServerBoundModelChatPacket> CODEC =
            CustomPacketPayload.codec(ServerBoundModelChatPacket::write, ServerBoundModelChatPacket::decode);

    private static ServerBoundModelChatPacket decode(@Nonnull FriendlyByteBuf buffer) {
        return new ServerBoundModelChatPacket(buffer.readInt(), buffer.readUtf(), buffer.readInt());
    }

    private void write(@Nonnull FriendlyByteBuf buffer) {
        buffer.writeInt(this.villagerId);
        buffer.writeUtf(this.message);
        buffer.writeInt(this.turnId);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
