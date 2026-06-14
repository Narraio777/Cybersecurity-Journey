package com.hypixelclient.gui;

import com.hypixelclient.HypixelClient;
import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import com.hypixelclient.module.Setting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import java.util.List;

public class ModuleScreen extends Screen {
    private static final int PANEL_WIDTH = 150;
    private static final int BTN_HEIGHT  = 18;
    private static final int SET_HEIGHT  = 14;
    private static final int PADDING     = 4;

    public ModuleScreen() {
        super(Text.literal("HypixelClient"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, "§bHypixelClient §7Modules", width / 2, 6, 0xFFFFFF);

        Category[] categories = Category.values();
        int startX = (width - categories.length * (PANEL_WIDTH + PADDING)) / 2;
        int startY = 20;

        for (int c = 0; c < categories.length; c++) {
            Category cat = categories[c];
            int px = startX + c * (PANEL_WIDTH + PADDING);
            List<Module> mods = HypixelClient.getInstance().getModuleManager().getByCategory(cat);

            context.fill(px, startY, px + PANEL_WIDTH, startY + 13, 0xFF222244);
            context.drawCenteredTextWithShadow(textRenderer, cat.name(), px + PANEL_WIDTH / 2, startY + 3, 0xFFAAAAFF);

            int rowY = startY + 13;
            for (Module mod : mods) {
                boolean hovered = mouseX >= px && mouseX <= px + PANEL_WIDTH && mouseY >= rowY && mouseY <= rowY + BTN_HEIGHT;
                int bg = mod.isEnabled() ? 0xFF005500 : (hovered ? 0xFF444444 : 0xFF1A1A1A);
                context.fill(px, rowY, px + PANEL_WIDTH, rowY + BTN_HEIGHT, bg);
                context.drawText(textRenderer, mod.getName(), px + 4, rowY + 5, 0xFFFFFFFF, false);
                String state = mod.isEnabled() ? "§aON" : "§cOFF";
                context.drawText(textRenderer, state, px + PANEL_WIDTH - 22, rowY + 5, 0xFFFFFFFF, false);
                rowY += BTN_HEIGHT + 1;

                if (mod.isEnabled()) {
                    for (Setting s : mod.getSettings()) {
                        context.fill(px, rowY, px + PANEL_WIDTH, rowY + SET_HEIGHT, 0xFF111111);
                        context.fill(px + 1, rowY + 1, px + 10, rowY + SET_HEIGHT - 1, 0xFF333366);
                        context.drawText(textRenderer, "<", px + 3, rowY + 3, 0xFFCCCCCC, false);
                        context.fill(px + PANEL_WIDTH - 10, rowY + 1, px + PANEL_WIDTH - 1, rowY + SET_HEIGHT - 1, 0xFF333366);
                        context.drawText(textRenderer, ">", px + PANEL_WIDTH - 8, rowY + 3, 0xFFCCCCCC, false);
                        String label = s.getName() + ": " + s.getDisplayValue();
                        context.drawCenteredTextWithShadow(textRenderer, label, px + PANEL_WIDTH / 2, rowY + 3, 0xFFDDDDDD);
                        rowY += SET_HEIGHT + 1;
                    }
                }
            }
        }
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Category[] categories = Category.values();
        int startX = (width - categories.length * (PANEL_WIDTH + PADDING)) / 2;
        int startY = 20;

        for (int c = 0; c < categories.length; c++) {
            Category cat = categories[c];
            int px = startX + c * (PANEL_WIDTH + PADDING);
            List<Module> mods = HypixelClient.getInstance().getModuleManager().getByCategory(cat);
            int rowY = startY + 13;

            for (Module mod : mods) {
                if (mouseX >= px && mouseX <= px + PANEL_WIDTH && mouseY >= rowY && mouseY <= rowY + BTN_HEIGHT) {
                    mod.toggle();
                    return true;
                }
                rowY += BTN_HEIGHT + 1;

                if (mod.isEnabled()) {
                    for (Setting s : mod.getSettings()) {
                        if (mouseX >= px + 1 && mouseX <= px + 10 && mouseY >= rowY + 1 && mouseY <= rowY + SET_HEIGHT - 1) {
                            s.decrement();
                            return true;
                        }
                        if (mouseX >= px + PANEL_WIDTH - 10 && mouseX <= px + PANEL_WIDTH - 1 && mouseY >= rowY + 1 && mouseY <= rowY + SET_HEIGHT - 1) {
                            s.increment();
                            return true;
                        }
                        rowY += SET_HEIGHT + 1;
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() { return false; }
}
