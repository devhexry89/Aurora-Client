
package com.aurora.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.CameraType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class AuroraClient implements ClientModInitializer {

    public static final List<Module> MODULES = new ArrayList<>();

    private static final Path CONFIG =
        FabricLoader.getInstance().getConfigDir()
            .resolve("aurora-client.properties");

    private static KeyMapping menu;

    private static boolean priorAutoJump;
    private static boolean controllingJump;
    private static boolean controllingCamera;
    private static boolean controllingFov;
    private static boolean controllingBob;

    private static CameraType priorCamera;
    private static int priorFov;
    private static boolean priorBob;

    public static boolean notices = true;

    static {
        add("sprint", "Sprint Assist",
            "Automatically sprint while walking forward",
            Module.Category.MOVEMENT, true);

        add("autojump", "Auto Jump",
            "Automatically jump over small obstacles",
            Module.Category.MOVEMENT, true);

        add("thirdperson", "Third Person",
            "Keep third-person camera enabled",
            Module.Category.RENDER, true);

        add("zoom", "Zoom FOV",
            "Reduce field of view while enabled",
            Module.Category.RENDER, true);

        add("nobob", "No Bob",
            "Turn off view bobbing",
            Module.Category.RENDER, true);

        add("radar", "Player Radar",
            "List nearby loaded players inside Aurora's menu",
            Module.Category.RENDER, true);

        add("coordshud", "Coordinates HUD",
            "Show XYZ coordinates on screen",
            Module.Category.RENDER, true);

        add("fpshud", "FPS HUD",
            "Show the current frame rate",
            Module.Category.RENDER, true);

        add("compasshud", "Compass HUD",
            "Show facing direction and heading",
            Module.Category.RENDER, true);

        add("playerhud", "Nearby Players HUD",
            "Show distance to nearby loaded players",
            Module.Category.RENDER, true);

        add("brandhud", "Aurora Watermark",
            "Show Aurora branding in the HUD",
            Module.Category.CLIENT, true);

        add("notifications", "Toggle Notices",
            "Display module state changes in chat",
            Module.Category.CLIENT, true);
    }

    private static void add(
        String id,
        String title,
        String desc,
        Module.Category category,
        boolean implemented
    ) {
        MODULES.add(
            new Module(id, title, desc, category, implemented)
        );
    }

    public static Module module(String id) {
        return MODULES.stream()
            .filter(m -> m.id.equals(id))
            .findFirst()
            .orElseThrow();
    }

    @Override
    public void onInitializeClient() {
        load();

        KeyMapping.Category category =
            KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(
                    "auroraclient", "main"
                )
            );

        // Only register the real GUI keybinding.
        // Do not register unused module bindings with
        // invalid or unassigned SDL keyboard codes.

        menu = KeyMappingHelper.registerKeyMapping(
            new KeyMapping(
                "key.auroraclient.menu",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_RSHIFT,
                category
            )
        );

        HudElementRegistry.addLast(
            Identifier.fromNamespaceAndPath(
                "auroraclient", "overlay"
            ),
            (graphics, tickDelta) ->
                AuroraHud.render(graphics)
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (menu != null) {
                while (menu.consumeClick()) {
                    if (client.gui.screen() instanceof AuroraScreen) {
                        client.gui.setScreen(null);
                    } else {
                        client.gui.setScreen(new AuroraScreen());
                    }
                }
            }

            // Module functionality remains active,
            // but unused module keybindings are not registered.
            for (Module m : MODULES) {
                if (m.implemented) {
                    m.tick(client);
                }
            }

            applyOptions(client);
        });
    }

    public static void toggle(Module m) {
        if (!m.implemented) {
            return;
        }

        m.toggle();

        if (
            module("notifications").enabled
            && !m.id.equals("notifications")
        ) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player != null) {
                mc.player.sendSystemMessage(
                    Component.literal(
                        "[Aurora] " + m.title
                        + (m.enabled ? " ON" : " OFF")
                    )
                );
            }
        }
    }

    private static void applyOptions(Minecraft mc) {
        if (mc.options == null) {
            return;
        }

        boolean jump = module("autojump").enabled;

        if (jump && !controllingJump) {
            priorAutoJump = mc.options.autoJump().get();
            controllingJump = true;
        }

        if (jump) {
            mc.options.autoJump().set(true);
        } else if (controllingJump) {
            mc.options.autoJump().set(priorAutoJump);
            controllingJump = false;
        }

        boolean third = module("thirdperson").enabled;

        if (third && !controllingCamera) {
            priorCamera = mc.options.getCameraType();
            controllingCamera = true;
        }

        if (third) {
            mc.options.setCameraType(
                CameraType.THIRD_PERSON_BACK
            );
        } else if (controllingCamera) {
            mc.options.setCameraType(priorCamera);
            controllingCamera = false;
        }

        boolean zoom = module("zoom").enabled;

        if (zoom && !controllingFov) {
            priorFov = mc.options.fov().get();
            controllingFov = true;
        }

        if (zoom) {
            mc.options.fov().set(30);
        } else if (controllingFov) {
            mc.options.fov().set(priorFov);
            controllingFov = false;
        }

        boolean nobob = module("nobob").enabled;

        if (nobob && !controllingBob) {
            priorBob = mc.options.bobView().get();
            controllingBob = true;
        }

        if (nobob) {
            mc.options.bobView().set(false);
        } else if (controllingBob) {
            mc.options.bobView().set(priorBob);
            controllingBob = false;
        }
    }

    private static void load() {
        if (!Files.exists(CONFIG)) {
            return;
        }

        Properties properties = new Properties();

        try (InputStream input = Files.newInputStream(CONFIG)) {
            properties.load(input);

            for (Module m : MODULES) {
                if (m.implemented) {
                    m.enabled = Boolean.parseBoolean(
                        properties.getProperty(m.id, "false")
                    );
                }
            }
        } catch (IOException ex) {
            System.err.println(
                "[Aurora] Config read failed: " + ex
            );
        }
    }

    public static void save() {
        Properties properties = new Properties();

        for (Module m : MODULES) {
            if (m.implemented) {
                properties.setProperty(
                    m.id,
                    Boolean.toString(m.enabled)
                );
            }
        }

        try {
            Files.createDirectories(CONFIG.getParent());

            try (
                OutputStream output =
                    Files.newOutputStream(CONFIG)
            ) {
                properties.store(
                    output,
                    "Aurora Client 26.3"
                );
            }
        } catch (IOException ex) {
            System.err.println(
                "[Aurora] Config save failed: " + ex
            );
        }
    }
}

