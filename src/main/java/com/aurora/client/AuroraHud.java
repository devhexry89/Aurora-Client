package com.aurora.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.DeltaTracker;

/** Client-only overlay; never transmits any player information. */
public final class AuroraHud {
    private AuroraHud() { }
    private static boolean on(String id) { return AuroraClient.module(id).enabled; }

    public static void render(GuiGraphicsExtractor graphics, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.gui.hud.isHidden()) return;
        int y = 6;
        if (on("brandhud")) {
            graphics.text(mc.font, "AURORA CLIENT  |  26.3", 6, y, 0xFF9BA7FF, true);
            y += 13;
        }
        if (on("coordshud")) {
            String coords = String.format("XYZ  %.0f / %.0f / %.0f", mc.player.getX(), mc.player.getY(), mc.player.getZ());
            graphics.text(mc.font, coords, 6, y, 0xFFE1E8FF, true);
            y += 12;
        }
        if (on("fpshud")) {
            graphics.text(mc.font, "FPS  " + mc.getFps(), 6, y, 0xFFACFFCB, true);
            y += 12;
        }
        if (on("compasshud")) {
            float heading = Math.floorMod((int)Math.floor(mc.player.getYRot()), 360);
            int quadrant = Math.floorMod(Math.round(heading / 90f), 4);
            String direction = switch (quadrant) { case 0 -> "South"; case 1 -> "West"; case 2 -> "North"; default -> "East"; };
            graphics.text(mc.font, "Facing  " + direction + " (" + (int)heading + "°)", 6, y, 0xFFFFDC9D, true);
            y += 12;
        }
        if (on("playerhud")) {
            graphics.text(mc.font, "LOADED PLAYERS", 6, y, 0xFFB3C3FF, true);
            y += 12;
            int displayed = 0;
            for (Player other : mc.level.players()) {
                if (other == mc.player || displayed >= 6) continue;
                int distance = (int) mc.player.distanceTo(other);
                graphics.text(mc.font, other.getName().getString() + "  " + distance + "m", 6, y, 0xFFDDE8FF, true);
                displayed++;
                y += 12;
            }
            if (displayed == 0) graphics.text(mc.font, "No nearby players loaded", 6, y, 0xFF999999, false);
        }
    }
}
