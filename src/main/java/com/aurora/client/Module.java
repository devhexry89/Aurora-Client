package com.aurora.client;

import net.minecraft.client.Minecraft;

public final class Module {
    public enum Category { COMBAT, RENDER, MOVEMENT, WORLD, MISC, CLIENT }
    public final String id, title, description;
    public final Category category;
    public boolean enabled;
    public final boolean implemented;
    public Module(String id, String title, String description, Category category, boolean implemented) {
        this.id=id; this.title=title; this.description=description; this.category=category; this.implemented=implemented;
    }
    public void toggle() { if (implemented) { enabled=!enabled; AuroraClient.save(); } }
    public void tick(Minecraft client) {
        if (!enabled || client.player == null) return;
        if (id.equals("sprint") && client.gui.screen() == null && client.player.input != null
            && client.player.input.hasForwardImpulse() && !client.player.isCrouching()) client.player.setSprinting(true);
    }
}
