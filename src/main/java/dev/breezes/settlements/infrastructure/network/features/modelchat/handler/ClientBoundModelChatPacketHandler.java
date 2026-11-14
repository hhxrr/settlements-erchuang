package dev.breezes.settlements.infrastructure.network.features.modelchat.handler;

import dev.breezes.settlements.infrastructure.network.core.ClientSidePacketHandler;
import dev.breezes.settlements.infrastructure.network.features.modelchat.packet.ClientBoundModelChatPacket;
import dev.breezes.settlements.presentation.ui.modelchat.ModelChatScreen;
import jakarta.inject.Inject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;

/**
 * 把服务端的对话结果投递给当前打开的村民对话界面。
 */
@CustomLog
@AllArgsConstructor(access = AccessLevel.PACKAGE, onConstructor_ = @Inject)
public class ClientBoundModelChatPacketHandler implements ClientSidePacketHandler<ClientBoundModelChatPacket> {

    @Override
    public void runOnClient(@Nonnull IPayloadContext context, @Nonnull ClientBoundModelChatPacket packet) {
        ModelChatScreen.deliver(packet);
    }
}
