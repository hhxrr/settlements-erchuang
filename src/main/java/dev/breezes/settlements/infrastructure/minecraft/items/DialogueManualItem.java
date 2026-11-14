package dev.breezes.settlements.infrastructure.minecraft.items;

import net.minecraft.world.item.Item;

import javax.annotation.Nonnull;

/**
 * 对话手册：手持并对着村民按下使用键，即可打开「村民对话」界面（见 {@code ModelDialogueClientEvents}）。
 * 也可在控制设置里把「打开村民对话」键绑定后，直接对着村民按键打开。
 */
public class DialogueManualItem extends Item {

    public DialogueManualItem(@Nonnull Properties properties) {
        super(properties);
    }
}
