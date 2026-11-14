package dev.breezes.settlements.di.modules.server;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import dev.breezes.settlements.infrastructure.network.core.ServerSidePacketHandler;
import dev.breezes.settlements.infrastructure.network.features.ballista.handler.ServerBoundBallistaAimPacketHandler;
import dev.breezes.settlements.infrastructure.network.features.ballista.packet.ServerBoundBallistaAimPacket;
import dev.breezes.settlements.infrastructure.network.features.modelchat.handler.ServerBoundModelApproveBuildPacketHandler;
import dev.breezes.settlements.infrastructure.network.features.modelchat.handler.ServerBoundModelChatPacketHandler;
import dev.breezes.settlements.infrastructure.network.features.modelchat.packet.ServerBoundModelApproveBuildPacket;
import dev.breezes.settlements.infrastructure.network.features.modelchat.packet.ServerBoundModelChatPacket;
import dev.breezes.settlements.infrastructure.network.features.ui.sync.handler.ServerBoundCloseUiPacketHandler;
import dev.breezes.settlements.infrastructure.network.features.ui.sync.handler.ServerBoundHeartbeatUiPacketHandler;
import dev.breezes.settlements.infrastructure.network.features.ui.sync.handler.ServerBoundOpenUiPacketHandler;
import dev.breezes.settlements.infrastructure.network.features.ui.sync.packet.ServerBoundCloseUiPacket;
import dev.breezes.settlements.infrastructure.network.features.ui.sync.packet.ServerBoundHeartbeatUiPacket;
import dev.breezes.settlements.infrastructure.network.features.ui.sync.packet.ServerBoundOpenUiPacket;

@Module
public abstract class ServerNetworkModule {

    @Binds
    @IntoMap
    @ClassKey(ServerBoundOpenUiPacket.class)
    abstract ServerSidePacketHandler<?> openUi(ServerBoundOpenUiPacketHandler impl);

    @Binds
    @IntoMap
    @ClassKey(ServerBoundCloseUiPacket.class)
    abstract ServerSidePacketHandler<?> closeUi(ServerBoundCloseUiPacketHandler impl);

    @Binds
    @IntoMap
    @ClassKey(ServerBoundHeartbeatUiPacket.class)
    abstract ServerSidePacketHandler<?> heartbeatUi(ServerBoundHeartbeatUiPacketHandler impl);

    @Binds
    @IntoMap
    @ClassKey(ServerBoundBallistaAimPacket.class)
    abstract ServerSidePacketHandler<?> ballistaAim(ServerBoundBallistaAimPacketHandler impl);

    // 模型对话建造
    @Binds
    @IntoMap
    @ClassKey(ServerBoundModelChatPacket.class)
    abstract ServerSidePacketHandler<?> modelChat(ServerBoundModelChatPacketHandler impl);

    @Binds
    @IntoMap
    @ClassKey(ServerBoundModelApproveBuildPacket.class)
    abstract ServerSidePacketHandler<?> modelApproveBuild(ServerBoundModelApproveBuildPacketHandler impl);

}
