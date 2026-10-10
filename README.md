# Aurora Client 0.3 — Minecraft 26.3 Fabric (test source)

Original client-side source. **Uncompiled / not yet in-game tested.** This is not a complete Meteor replacement.

## Features implemented in source
- Right Shift opens a six-category GUI (Combat / Render / Movement / World / Misc / Client)
- Persistent module enable/disable settings in `config/aurora-client.properties`
- Per-module keybinds in Minecraft Settings > Controls
- Sprint Assist, Auto Jump, Third Person, Zoom FOV, No Bob
- Player Radar (inside GUI; loaded players only)
- On-screen HUD toggles: coordinates, FPS, compass, nearby loaded players, Aurora watermark
- Module toggle notifications

**Not included yet:** X-ray, 3D ESP, tracers, KillAura, flight, automation. No fake toggles for those features.

## Build on GitHub
1. Upload the contents of this directory to the **root** of a GitHub repository (not nested in a directory).
2. On GitHub select Actions > Build Aurora Client > Run workflow.
3. Review the compile log. If it succeeds, download the artifact and unzip it.
4. Put the `aurora-client-0.3.0.jar` in your **local client** Minecraft `mods` folder, alongside Fabric API compatible with 26.3.
5. Launch Minecraft 26.3 with Fabric Loader and Java 25, press Right Shift.

If compilation fails, share the **first error in the Compile step**, not the entire stack trace. Build APIs change frequently.

## Safety / scope
Only reads client-known players for radar/HUD. Never install this client mod on BisectHosting; server-side installs are not required. Multiplayer servers may disallow some client features.


IMPORTANT: See UPLOAD_TO_GITHUB.md. This source is not yet compile verified.


## v0.3.1 compile fixes

Updated to the Fabric 26.3 keymapping API (`KeyMappingHelper`), Minecraft `Identifier`, SDL keyboard input (`KEYBOARD`), and removed a direct dependency on the missing `DeltaTracker` HUD type. This source ZIP has not been compiled here; GitHub Actions must verify it.
