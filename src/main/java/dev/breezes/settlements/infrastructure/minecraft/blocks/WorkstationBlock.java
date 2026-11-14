package dev.breezes.settlements.infrastructure.minecraft.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 通用「工作站方块」基类：三个机械动力新增职业各自的工作站（职业方块）。
 * <p>
 * 该类本身只是一个普通方块，真正的差异在 {@code BlockRegistry} 里通过不同的
 * {@link BlockBehaviour.Properties}（颜色 / 材质 / 音效）区分。每个实例都会被注册成一个
 * POI 类型（见 {@code PoiTypeRegistry}），从而让对应职业的村民认领并使用。
 * <p>
 * 后续可为不同职业的工作站加专属交互（例如工厂工人操作台显示进度），届时在此子类化即可。
 */
public class WorkstationBlock extends Block {

    public WorkstationBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }
}
