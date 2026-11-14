package dev.breezes.settlements.compat.create;

import net.neoforged.fml.ModList;

/**
 * 机械动力（Create）集成的总入口。
 * <p>
 * 所有对 Create 的调用都应先经过这里，保证在 Create 未安装时优雅降级，
 * 不会抛出 {@link NoClassDefFoundError} 导致整个 mod 崩溃。
 */
public final class CreateCompat {

    public static final String MOD_ID = "create";

    private CreateCompat() {
    }

    /**
     * @return Create 是否已加载（即玩家整合包里装了机械动力）
     */
    public static boolean isLoaded() {
        try {
            return ModList.get().isLoaded(MOD_ID);
        } catch (Throwable t) {
            return false;
        }
    }
}
