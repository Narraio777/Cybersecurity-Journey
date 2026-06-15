package com.hypixelclient.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ChangelogScreen extends Screen {

    private static final int PANEL_W   = 420;
    private static final int PANEL_H   = 280;
    private static final int LINE_H    = 11;
    private static final int PADDING   = 10;

    private int scrollOffset = 0;
    private final List<Entry> entries  = new ArrayList<>();

    // --- changelog data -------------------------------------------------------

    static {
        // populated in the instance initialiser below so the static list stays clean
    }

    private record Entry(String text, int color, int indent) {}

    public ChangelogScreen() {
        super(Text.literal("Changelog"));
        build();
    }

    private void build() {
        version("v1.3", "AC Bypass & Humanization");
        bullet("New Humanizer utility — gaussian delay, jitter, chance helpers");
        bullet("AutoClicker: gaussian CPS distribution, stutter after 8-18 clicks, 4% miss");
        bullet("AimAssist: 6% lazy frames, off-centre body jitter, 8% overshoot chance");
        bullet("TriggerBot: gaussian reaction time per target, 5% miss rate");
        bullet("Criticals: 25% skip rate, 0-2 tick pre-hop delay");
        bullet("SprintReset: variable 1-4 tick reset window (was fixed 2)");
        bullet("BunnyHop: 0-2 tick landing delay between jumps");
        bullet("SneakBridge / NinjaBridge: ±2° angle jitter on every placement");

        spacer();

        version("v1.2", "Expanded Modules & Config");
        bullet("ESP (entity glow outline for nearby players)");
        bullet("Zoom (C key, configurable FOV 5°-45°)");
        bullet("NoFire — hides fire overlay");
        bullet("HitColor — red vignette flash on hit");
        bullet("Nametags+ — name / health / distance above players");
        bullet("Reach extension (EntityAttributes)");
        bullet("AntiBot — UUID v3 + regex bot-name filter");
        bullet("AntiAFK — random yaw nudge every ~90 s");
        bullet("AutoGG — sends 'gg' after game-end chat messages");
        bullet("Config: Gson save/load, auto every 10 s, persists all settings");
        bullet("HypixelAPI: stats HUD via Hypixel public API");
        bullet("CustomCrosshair — colour & size settings");

        spacer();

        version("v1.1", "Combat & Movement");
        bullet("Velocity / Anti-Knockback (horizontal + vertical reduction %)");
        bullet("TriggerBot — auto-attack crosshair entity with configurable delay");
        bullet("Criticals — mini-hops for guaranteed crits");
        bullet("SprintReset / W-Tap — drops sprint on hit for extra KB");
        bullet("BunnyHop — auto-jump while moving");
        bullet("Speed / Strafe — velocity multiplier (100-200%)");
        bullet("ReachDisplay HUD — live block-reach readout");
        bullet("Reach module — extends interaction range");
        bullet("ScreenBlurMixin — removes menu background blur");

        spacer();

        version("v1.0", "Core Client");
        bullet("Module system with categories: Combat, Movement, Visual, HUD, Misc, API");
        bullet("AimAssist, AutoClicker with AntiBot integration");
        bullet("AutoBridge, NinjaBridge, SneakBridge — AC-safe (no sneak packets)");
        bullet("SneakBridge KbCancel — dampens knockback mid-bridge");
        bullet("ArmorHUD, PotionHUD, KeystrokeHUD, CoordDisplay");
        bullet("FPS / Ping / CPS / ReachDisplay HUDs");
        bullet("AutoSprint, Keybind system");
        bullet("Module GUI (§b key), dark translucent background (no blur)");
    }

    // --- helpers --------------------------------------------------------------

    private void version(String tag, String title) {
        entries.add(new Entry("§b" + tag + " §7— §f" + title, 0xFFFFFFFF, 0));
    }

    private void bullet(String text) {
        entries.add(new Entry("§7· §r" + text, 0xFFCCCCCC, 10));
    }

    private void spacer() {
        entries.add(new Entry("", 0, 0));
    }

    // --- rendering ------------------------------------------------------------

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, 0x88000000);

        int px = (this.width  - PANEL_W) / 2;
        int py = (this.height - PANEL_H) / 2;

        ctx.fill(px, py, px + PANEL_W, py + PANEL_H, 0xDD111122);
        ctx.fill(px, py, px + PANEL_W, py + 16, 0xFF222244);

        ctx.drawCenteredTextWithShadow(textRenderer, "§bHypixelClient §7Changelog", this.width / 2, py + 4, 0xFFFFFF);

        // "Back" button
        ctx.fill(px + PANEL_W - 52, py + 2, px + PANEL_W - 2, py + 14, 0xFF333355);
        ctx.drawCenteredTextWithShadow(textRenderer, "§7Back", px + PANEL_W - 27, py + 4, 0xFFCCCCCC);

        // clipping region for the list
        int listTop    = py + 18;
        int listBottom = py + PANEL_H - 4;
        int listHeight = listBottom - listTop;
        int contentH   = entries.size() * LINE_H;
        int maxScroll  = Math.max(0, contentH - listHeight);
        scrollOffset   = Math.max(0, Math.min(scrollOffset, maxScroll));

        int y = listTop - scrollOffset;
        for (Entry e : entries) {
            if (y + LINE_H > listTop && y < listBottom && !e.text().isEmpty()) {
                ctx.drawText(textRenderer, e.text(), px + PADDING + e.indent(), y + 1, e.color(), false);
            }
            y += LINE_H;
        }

        // Scrollbar
        if (contentH > listHeight) {
            int barH  = Math.max(20, listHeight * listHeight / contentH);
            int barY  = listTop + scrollOffset * (listHeight - barH) / maxScroll;
            int barX  = px + PANEL_W - 5;
            ctx.fill(barX, listTop, barX + 3, listBottom, 0xFF222233);
            ctx.fill(barX, barY, barX + 3, barY + barH, 0xFF6666AA);
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int px = (this.width  - PANEL_W) / 2;
        int py = (this.height - PANEL_H) / 2;
        // Back button
        if (mouseX >= px + PANEL_W - 52 && mouseX <= px + PANEL_W - 2
                && mouseY >= py + 2 && mouseY <= py + 14) {
            if (this.client != null) this.client.setScreen(new ModuleScreen());
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollOffset -= (int)(verticalAmount * LINE_H * 2);
        return true;
    }

    @Override
    public boolean shouldPause() { return false; }
}
