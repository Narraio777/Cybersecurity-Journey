package com.hypixelclient.module.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hypixelclient.HypixelClient;
import com.hypixelclient.module.Category;
import com.hypixelclient.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class HypixelAPIModule extends Module {
    private String cachedStats = "";
    private String lastUUID = "";
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public HypixelAPIModule() {
        super("StatsDisplay", "Shows Hypixel stats for yourself or targeted player", Category.API);
    }

    @Override
    public void onEnable() {
        cachedStats = "Loading...";
        fetchStats();
    }

    private void fetchStats() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        String apiKey = HypixelClient.getInstance().getConfig().getData().hypixelApiKey;
        if (apiKey.isBlank()) {
            cachedStats = "Set API key in config!";
            return;
        }
        UUID uuid = client.player.getUuid();
        if (uuid.toString().equals(lastUUID)) return;
        lastUUID = uuid.toString();

        CompletableFuture.runAsync(() -> {
            try {
                HttpRequest req = HttpRequest.newBuilder()
                    .uri(new URI("https://api.hypixel.net/player?key=" + apiKey + "&uuid=" + uuid))
                    .GET().build();
                HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
                JsonObject root = JsonParser.parseString(resp.body()).getAsJsonObject();
                if (root.get("success").getAsBoolean()) {
                    JsonObject player = root.getAsJsonObject("player");
                    long networkExp = player.has("networkExp") ? player.get("networkExp").getAsLong() : 0;
                    int level = (int) Math.floor((Math.sqrt(networkExp + 15312.5) - 125.0 / Math.sqrt(2)) / (25 * Math.sqrt(2)));
                    String name = player.get("displayname").getAsString();
                    cachedStats = "§b" + name + " §7| §aLevel " + level;
                } else {
                    cachedStats = "API Error: " + root.get("cause").getAsString();
                }
            } catch (Exception e) {
                cachedStats = "Failed to fetch stats";
            }
        });
    }

    public void render(DrawContext context, MinecraftClient client) {
        if (!isEnabled()) return;
        int x = client.getWindow().getScaledWidth() / 2 - 50;
        int y = 2;
        context.drawText(client.textRenderer, cachedStats, x, y, 0xFFFFFFFF, true);
    }
}
