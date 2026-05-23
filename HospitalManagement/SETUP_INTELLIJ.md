# How to Open & Run in IntelliJ IDEA

## Step 1 — Import as Maven Project

1. Open IntelliJ IDEA
2. **File → Open** → select the `HospitalManagement` folder (the one containing `pom.xml`)
3. IntelliJ will detect it as a Maven project and show a popup — click **"Load Maven Project"**
4. Wait for Maven to finish downloading dependencies (bottom status bar)

## Step 2 — Verify JavaFX dependencies are downloaded

Open the **Maven** panel (View → Tool Windows → Maven).
Expand: `hospital-management → Dependencies`
You should see entries like:
```
javafx-controls-21.0.2-win.jar   ← (win / mac / linux depending on your OS)
javafx-graphics-21.0.2-win.jar
javafx-base-21.0.2-win.jar
```
If you see JARs **without** the OS suffix (e.g. just `javafx-controls-21.0.2.jar`),
click the **Reload All Maven Projects** button (circular arrows icon in Maven panel).

## Step 3 — Set Project SDK

1. **File → Project Structure → Project**
2. Set **SDK** to Java 17 or Java 21
3. Set **Language level** to 17 (or 21)
4. Click **Apply → OK**

## Step 4 — Run the app

**Option A — Maven (recommended):**
- In the Maven panel expand: `Plugins → javafx → javafx:run`
- Double-click `javafx:run`

**Option B — Run configuration:**
- Open `MainApp.java`
- Click the green ▶ next to `public static void main`
- If it fails with "module not found", use Option A instead

## Troubleshooting

### "package javafx.* does not exist" after reload
The OS classifier wasn't detected. Fix it manually:
1. Open `pom.xml`
2. Change `${javafx.platform}` on each dependency to your OS literal:
   - Windows → `win`
   - macOS (Intel) → `mac`
   - macOS (Apple Silicon) → `mac-aarch64`  
   - Linux → `linux`

Example:
```xml
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>21.0.2</version>
    <classifier>win</classifier>   <!-- ← hardcode your OS here -->
</dependency>
```
Do the same for javafx-fxml, javafx-base, javafx-graphics, javafx-swing.
Then **Reload Maven** again.

### "Error: JavaFX runtime components are missing"
Use the Maven `javafx:run` goal (Option A above) — it adds the right `--module-path` automatically.

### IntelliJ shows red underlines even though Maven compiled fine
- **File → Invalidate Caches → Invalidate and Restart**
- After restart, re-import Maven project
