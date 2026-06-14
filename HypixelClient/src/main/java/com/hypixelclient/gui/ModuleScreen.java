package com.hypixelclient.gui;

import com.hypixelclient.HypixelClient;
import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import java.util.List;

public class ModuleScreen extends Screen {
    private static final int PANEL_WIDTH = 140;
    private static final int BUTTON_HEIGHT = 20;
    private static final int PADDING = 4;

    public ModuleScreen() {
        super(Text.literal("HypixelClient"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, "HypixelClient Modules", width / 2, 8, 0xFFFFFF);

        Category[] categories = Category.values();
        int totalWidth = categories.length * (PANEL_WIDTH + PADDING);
        int startX = (width - totalWidth) / 2;
        int startY = 25;

        for (int c = 0; c < categories.length; c++) {
            Category cat = categories[c];
            int panelX = startX + c * (PANEL_WIDTH + PADDING);
            List<Module> mods = HypixelClient.getInstance().getModuleManager().getByCategory(cat);

            context.fill(panelX, startY, panelX + PANEL_WIDTH, startY + 14, 0xFF333355);
            context.drawCenteredTextWithShadow(textRenderer, cat.name(), panelX + PANEL_WIDTH / 2, startY + 3, 0xFFAAAAFF);

            for (int i = 0; i < mods.size(); i++) {
                Module mod = mods.get(i);
                int btnY = startY + 14 + i * (BUTTON_HEIGHT + 2);
                boolean hovered = mouseX >= panelX && mouseX <= panelX + PANEL_WIDTH
                               && mouseY >= btnY && mouseY <= btnY + BUTTON_HEIGHT;
                int bg = mod.isEnabled() ? 0xFF005500 : (hovered ? 0xFF444444 : 0xFF222222);
                context.fill(panelX, btnY, panelX + PANEL_WIDTH, btnY + BUTTON_HEIGHT, bg);
                context.drawText(textRenderer, mod.getName(), panelX + 4, btnY + 6, 0xFFFFFFFF, false);
                String state = mod.isEnabled() ? "ON" : "OFF";
                int stateColor = mod.isEnabled() ? 0xFF00FF00 : 0xFFFF4444;
                context.drawText(textRenderer, state, panelX + PANEL_WIDTH - 4 - textRenderer.getWidth(state), btnY + 6, stateColor, false);
            }
        }
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Category[] categories = Category.values();
        int totalWidth = categories.length * (PANEL_WIDTH + PADDING);
        int startX = (width - totalWidth) / 2;
        int startY = 25;

        for (int c = 0; c < categories.length; c++) {
            Category cat = categories[c];
            int panelX = startX + c * (PANEL_WIDTH + PADDING);
            List<Module> mods = HypixelClient.getInstance().getModuleManager().getByCategory(cat);

            for (int i = 0; i < mods.size(); i++) {
                int btnY = startY + 14 + i * (BUTTON_HEIGHT + 2);
                if (mouseX >= panelX && mouseX <= panelX + PANEL_WIDTH
                 && mouseY >= btnY && mouseY <= btnY + BUTTON_HEIGHT) {
                    mods.get(i).toggle();
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() { return false; }
}
