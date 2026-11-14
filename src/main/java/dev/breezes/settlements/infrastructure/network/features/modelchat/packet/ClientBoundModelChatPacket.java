package dev.breezes.settlements.infrastructure.network.features.modelchat.packet;

import dev.breezes.settlements.infrastructure.network.core.ClientBoundPacket;
import dev.breezes.settlements.shared.util.ResourceLocationUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;

/**
 * 服务端回传给客户端的对话结果（服务端 → 客户端）。
 *
 * @param villagerId    被对话的村民实体 id
 * @param turnId        本轮序号
 * @param replyText     村民（模型）的回复文本
 * @param mapText       可选的 ASCII 地图文本
 * @param blueprintJson 可选的蓝图 JSON（玩家批准后将由村民建造）；无蓝图时为空字符串
 * @param hasBlueprint 是否附带可建造的蓝图
 * @param error         可选的错误信息
 */
public record ClientBoundModelChatPacket(int villagerId,
                                         int turnId,
                                         @Nonnull String replyText,
                                         @Nonnull String mapText,
                                         @Nonnull String blueprintJson,
                                         boolean hasBlueprint,
                                         @Nonnull String error) implements ClientBoundPacket {

    public static final Type<ClientBoundModelChatPacket> ID =
            new Type<>(ResourceLocationUtil.mod("packet_model_chat_clientbound"));

    public static final StreamCodec<FriendlyByteBuf, ClientBoundModelChatPacket> CODEC =
            CustomPacketPayload.codec(ClientBoundModelChatPacket::write, ClientBoundModelChatPacket::decode);

    private static ClientBoundModelChatPacket decode(@Nonnull FriendlyByteBuf buffer) {
        int villagerId = buffer.readInt();
        int turnId = buffer.readInt();
        String replyText = buffer.readUtf();
        String mapText = buffer.readUtf();
        boolean hasBlueprint = buffer.readBoolean();
        String blueprintJson = new String(buffer.readByteArray(), StandardCharsets.UTF_8);
        String error = buffer.readUtf();
        return new ClientBoundModelChatPacket(villagerId, turnId, replyText, mapText, blueprintJson, hasBlueprint, error);
    }

    private void write(@Nonnull FriendlyByteBuf buffer) {
        buffer.writeInt(this.villagerId);
        buffer.writeInt(this.turnId);
        buffer.writeUtf(this.replyText);
        buffer.writeUtf(this.mapText);
        buffer.writeBoolean(this.hasBlueprint);
        buffer.writeByteArray(this.blueprintJson.getBytes(StandardCharsets.UTF_8));
        buffer.writeUtf(this.error);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
