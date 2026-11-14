package dev.breezes.settlements.bootstrap.event;

import dev.breezes.settlements.bootstrap.registry.items.ItemRegistry;
import dev.breezes.settlements.presentation.ui.keybindings.SettlementsKeyMappings;
import dev.breezes.settlements.presentation.ui.modelchat.ModelChatScreen;
import dev.breezes.settlements.shared.util.VillagerRaycastUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * 客户端：打开「村民对话」界面的触发逻辑。
 * <p>
 * 两种方式：
 * <ol>
 *   <li>对着村民按 {@link SettlementsKeyMappings#OPEN_MODEL_CHAT} 键（在控制设置里绑定）；</li>
 *   <li>手持「对话手册」对着村民按下使用键。</li>
 * </ol>
 * 二者都通过射线检测最近的村民，然后打开 {@link ModelChatScreen}。
 */
@EventBusSubscriber(Dist.CLIENT)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ModelDialogueClientEvents {

    private static boolean useKeyWasDown = false;

    @SubscribeEvent
    public static void onClientTick(@Nonnull ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null || minecraft.screen != null) {
            return;
        }

        // 方式一：快捷键
        while (SettlementsKeyMappings.OPEN_MODEL_CHAT.consumeClick()) {
            openForLookedAtVillager(minecraft);
            break;
        }

        // 方式二：手持对话手册对着村民按使用键（边沿触发）
        boolean useDown = minecraft.options.keyUse.isDown();
        boolean edge = useDown && !useKeyWasDown;
        useKeyWasDown = useDown;
        if (edge && minecraft.player.getMainHandItem().is(ItemRegistry.DIALOGUE_MANUAL.get())) {
            openForLookedAtVillager(minecraft);
        }
    }

    private static void openForLookedAtVillager(@Nonnull Minecraft minecraft) {
        Optional<EntityHitResult> hit = VillagerRaycastUtil.raycastVillagerTarget(minecraft.player, 15.0);
        if (hit.isEmpty()) {
            return;
        }
        int villagerId = hit.get().getEntity().getId();
        minecraft.setScreen(new ModelChatScreen(villagerId));
    }
}
