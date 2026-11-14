package dev.breezes.settlements.presentation.ui.modelchat;

import dev.breezes.settlements.infrastructure.network.features.modelchat.packet.ClientBoundModelChatPacket;
import dev.breezes.settlements.infrastructure.network.features.modelchat.packet.ServerBoundModelApproveBuildPacket;
import dev.breezes.settlements.infrastructure.network.features.modelchat.packet.ServerBoundModelChatPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * 村民「模型对话建造」客户端聊天界面。
 * <p>
 * 玩家在此与村民自然语言对话；当模型返回可建造蓝图时，界面出现「批准建造」按钮，
 * 点击后蓝图被发往服务端并入队，由村民逐格盖出。
 */
public final class ModelChatScreen extends Screen {

    /** 当前打开的界面实例，供网络处理器投递消息。 */
    @Nullable
    private static volatile ModelChatScreen activeScreen;

    private final int villagerId;
    private int turnId = 0;

    private final List<String> history = new ArrayList<>();
    private EditBox input;
    private Button sendButton;
    private Button approveButton;

    @Nullable
    private String pendingBlueprintJson = null;
    @Nullable
    private String pendingMapText = null;

    public ModelChatScreen(int villagerId) {
        super(Component.translatable("ui.settlements.modelchat.title"));
        this.villagerId = villagerId;
    }

    @Override
    protected void init() {
        this.history.clear();
        this.history.add("（你靠近了村民，可以跟它说想盖点什么，例如：帮我盖座小木屋 / 来个瞭望塔 / 围成一圈矮墙）");

        int panelX = 20;
        int panelW = this.width - 40;
        int inputY = this.height - 36;
        int inputW = panelW - 90;

        this.input = new EditBox(this.font, panelX, inputY, inputW, 20, Component.literal(""));
        this.input.setMaxLength(256);
        this.input.setResponder(s -> {
        });
        this.addRenderableWidget(this.input);

        this.sendButton = Button.builder(Component.translatable("ui.settlements.modelchat.send"),
                        b -> this.send())
                .bounds(panelX + inputW + 6, inputY, 84, 20)
                .build();
        this.addRenderableWidget(this.sendButton);

        this.approveButton = Button.builder(Component.translatable("ui.settlements.modelchat.approve"),
                        b -> this.approve())
                .bounds(panelX, inputY - 26, panelW, 20)
                .build();
        this.approveButton.visible = false;
        this.addRenderableWidget(this.approveButton);

        activeScreen = this;
        this.setFocused(this.input);
    }

    private void send() {
        String text = this.input.getValue().strip();
        if (text.isEmpty()) {
            return;
        }
        this.input.setValue("");
        this.history.add("你：" + text);
        PacketDistributor.sendToServer(new ServerBoundModelChatPacket(this.villagerId, text, this.turnId++));
    }

    private void approve() {
        if (this.pendingBlueprintJson == null) {
            return;
        }
        this.history.add("你：[已批准建造，村民开始动工]");
        PacketDistributor.sendToServer(new ServerBoundModelApproveBuildPacket(this.villagerId, this.pendingBlueprintJson));
        this.pendingBlueprintJson = null;
        this.pendingMapText = null;
        this.approveButton.visible = false;
    }

    /** 由 {@link dev.breezes.settlements.infrastructure.network.features.modelchat.handler.ClientBoundModelChatPacketHandler} 调用。 */
    public static void deliver(@Nullable ClientBoundModelChatPacket packet) {
        ModelChatScreen screen = activeScreen;
        if (screen != null && packet != null) {
            screen.receive(packet);
        }
    }

    private void receive(@Nullable ClientBoundModelChatPacket packet) {
        if (packet == null) {
            return;
        }
        this.history.add("村民：" + packet.replyText());
        if (packet.mapText() != null && !packet.mapText().isBlank()) {
            for (String line : packet.mapText().split("\n")) {
                this.history.add("    " + line);
            }
        }
        if (packet.hasBlueprint() && packet.blueprintJson() != null && !packet.blueprintJson().isBlank()) {
            this.pendingBlueprintJson = packet.blueprintJson();
            this.pendingMapText = packet.mapText();
            this.approveButton.visible = true;
            this.history.add("    [村民递来一张蓝图，点击下方「批准建造」让它动工]");
        }
        if (packet.error() != null && !packet.error().isBlank()) {
            this.history.add("（模型错误：" + packet.error() + "）");
        }
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        int panelX = 20;
        int panelY = 20;
        int panelW = this.width - 40;
        int panelH = this.height - 40;
        graphics.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xC0101010);
        graphics.drawString(this.font, this.title, panelX + 6, panelY + 6, 0xFFFFFF, false);

        // 渲染最近若干行（简单自底向上布局）
        int bottom = this.height - 64;
        int lineH = 12;
        int maxLines = (bottom - (panelY + 24)) / lineH;
        int start = Math.max(0, this.history.size() - maxLines);
        int y = bottom - (this.history.size() - start) * lineH;
        for (int i = start; i < this.history.size(); i++) {
            List<FormattedCharSequence> wrapped = this.font.split(Component.literal(this.history.get(i)), panelW - 16);
            for (FormattedCharSequence seq : wrapped) {
                graphics.drawString(this.font, seq, panelX + 8, y, 0xE0E0E0, false);
                y += lineH;
            }
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.onClose();
            return true;
        }
        if (this.input.isFocused() && this.input.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.input.isFocused() && this.input.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public void onClose() {
        if (activeScreen == this) {
            activeScreen = null;
        }
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
