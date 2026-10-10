package com.aurora.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import java.util.List;

public final class AuroraScreen extends Screen {
    private Module.Category tab = Module.Category.RENDER;
    private int page = 0;
    public AuroraScreen() { super(Component.literal("Aurora Client")); }
    @Override protected void init() {
        int left=(width-460)/2, top=(height-274)/2;
        Module.Category[] cats=Module.Category.values();
        for (int i=0;i<cats.length;i++) {
            final Module.Category cat=cats[i];
            addRenderableWidget(Button.builder(Component.literal((tab == cat ? "◆ " : "")+cat.name()), b -> {
                tab=cat;page=0;clearWidgets();init();
            }).bounds(left+12+i*74,top+34,71,19).build());
        }
        List<Module> list=AuroraClient.MODULES.stream().filter(m->m.category==tab).toList();
        int start=page*7;
        for (int i=start;i<Math.min(start+7,list.size());i++) {
            final Module m=list.get(i);
            addRenderableWidget(Button.builder(Component.literal(m.title+(!m.implemented?" [WIP]":(m.enabled?" [ON]":" [OFF]"))), b -> {
                AuroraClient.toggle(m); b.setMessage(Component.literal(m.title+(!m.implemented?" [WIP]":(m.enabled?" [ON]":" [OFF]"))));
            }).bounds(left+15,top+60+(i-start)*25,230,21).build()).active=m.implemented;
        }
        if (start+7<list.size()) addRenderableWidget(Button.builder(Component.literal("Next >"),b->{page++;clearWidgets();init();}).bounds(left+285,top+225,70,19).build());
        if (page>0) addRenderableWidget(Button.builder(Component.literal("< Back"),b->{page--;clearWidgets();init();}).bounds(left+205,top+225,70,19).build());
        addRenderableWidget(Button.builder(Component.literal("Close"),b->onClose()).bounds(left+375,top+225,70,19).build());
    }
    @Override public void extractRenderState(GuiGraphicsExtractor g,int mouseX,int mouseY,float delta) {
        super.extractRenderState(g,mouseX,mouseY,delta);
        int left=(width-460)/2, top=(height-274)/2;
        g.text(font,"AURORA CLIENT  /  26.3",left+15,top+12,0xFF98A8FF,true);
        g.text(font,"ORIGINAL FABRIC CLIENT  •  RSHIFT",left+265,top+12,0xFFAAAAAA,false);
        g.text(font,"Modules",left+15,top+245,0xFF999999,false);
        if (minecraft != null && minecraft.player != null) {
            g.text(font,"X:"+(int)minecraft.player.getX()+" Y:"+(int)minecraft.player.getY()+" Z:"+(int)minecraft.player.getZ(),left+265,top+66,0xFF8DD3FF,false);
            if (AuroraClient.module("radar").enabled && minecraft.level!=null) {
                g.text(font,"LOADED PLAYER RADAR",left+265,top+85,0xFFFFCA77,true);
                int y=top+103;
                for (Player p : minecraft.level.players()) {
                    if (p==minecraft.player || y>top+206) continue;
                    int meters=(int)minecraft.player.distanceTo(p);
                    g.text(font,p.getName().getString()+"  "+meters+"m",left+265,y,0xFFD6E6FF,false);
                    y+=14;
                }
            } else g.text(font,"Enable Player Radar for nearby players",left+265,top+85,0xFF888888,false);
        }
        g.text(font,"WIP modules are intentionally disabled",left+15,top+261,0xFF888888,false);
    }
    @Override public void onClose() { if (minecraft!=null) minecraft.gui.setScreen(null); }
}
