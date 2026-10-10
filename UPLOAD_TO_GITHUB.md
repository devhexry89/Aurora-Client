# Upload Aurora Client to GitHub (keep folders intact)

**Important:** GitHub's browser upload can flatten files when you select individual files via the file picker. Do not upload every file in the ZIP as a flat list.

## Recommended: Upload using GitHub Desktop
1. Extract this ZIP somewhere on your PC.
2. In GitHub Desktop, clone your `mcaurora` repository.
3. Open the cloned repository folder. Remove any previously misplaced root-level `AuroraClient.java`, `AuroraHud.java`, `AuroraScreen.java`, `Module.java`, `fabric.mod.json`, `en_us.json`, and `build.yml`.
4. Copy the **contents** of the extracted AuroraClient-GitHub-Ready-26.3 folder into the cloned repository folder, preserving the `src` and `.github` directories.
5. In GitHub Desktop, commit and push the changes.
6. Check GitHub's Code tab: `.github/workflows/build.yml` and `src/main/java/com/aurora/client/AuroraClient.java` must exist.
7. GitHub > Actions > Build Aurora Client > Run workflow.
8. If successful, download the Artifact named AuroraClient-Fabric-26.3, extract it and put the compiled production JAR in your local Minecraft mods folder.

## Notes
This is a source-code TEST project, not a precompiled JAR. The Minecraft 26.3 API compilation is unverified and further fixes may be required. This version does not have working X-Ray, ESP, or combat modules.
