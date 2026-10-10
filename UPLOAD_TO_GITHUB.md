# Upload Aurora Client v0.3.1 to GitHub

The ZIP contains the full Gradle project, with nested Java folders and `.github/workflows/build.yml`. Do not upload the ZIP itself to GitHub.

1. Extract the ZIP locally.
2. Clone your Aurora-Client repository using GitHub Desktop.
3. Copy the **contents** of this extracted folder to the cloned repository root, replacing older project files.
4. Commit and push.
5. Open Actions > Build Aurora Client. The push will trigger the build; you can also run it manually.
6. If the build succeeds, download the AuroraClient-Fabric-26.3 artifact.

If Java compilation fails again, share the first `error:` block from the run.
