# 🎮 John Wick's Adventure

[![Java](https://img.shields.io/badge/Java-8%2B-orange.svg)](https://www.oracle.com/java/)
[![libGDX](https://img.shields.io/badge/libGDX-1.12.1-red.svg)](https://libgdx.com/)
[![Gradle](https://img.shields.io/badge/Gradle-8.7-blue.svg)](https://gradle.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

An action-packed 2D side-scrolling platformer developed in **Java** using the **libGDX** framework. Features multiple themed levels, physics-driven platforming, dual-projectile combat mechanics, animated enemy states, sound design, and a multi-phase boss fight.

---

## 🔗 Quick Links
- **Play / Download Game:** [https://huzaifa-dev78.itch.io/john-wick-adventure](https://huzaifa-dev78.itch.io/john-wick-adventure)
- **Source Code:** [https://github.com/huzaifa-dev78/John-Wick-Adventure](https://github.com/huzaifa-dev78/John-Wick-Adventure)
- **Developer:** [Huzaifa (@huzaifa-dev78)](https://github.com/huzaifa-dev78)

---

## 🕹️ Gameplay & Features

### 1. Multi-Level Campaign
- **Main Menu:** Cinematic menu with interactive hover buttons, keyboard navigation (`1`, `2`, `3`, `ENTER`, `ESC`), and quick-launch options.
- **Level 1 (Desert Run):** Desert-themed introductory stage featuring platform navigation, coin collection, hazardous thorn pits, patrolling desert enemies, and fire-bullet combat.
- **Level 2 (Ice Run):** Slippery ice caverns introducing the **Ice Bullet** mechanic — freeze patrolling enemies in their tracks to traverse safely.
- **Level 3 (Final Boss):** The climactic showdown against the Boss enemy, featuring multi-tiered floating platforms, dual-bullet switching, boss fire attacks, dynamic health bars, and an ending victory screen.

### 2. Core Mechanics
- **Physics Engine:** Frame-rate independent horizontal momentum, variable jumping velocity, gravity simulation, and precise AABB box collision detection.
- **Combat Systems:**
  - 🔥 **Fire Bullets:** Deals direct lethal damage and triggers a 3x3 burning sprite-sheet death animation.
  - ❄️ **Ice Bullets:** Temporarily freezes enemies into ice blocks with timer-based thaw mechanics.
- **Audio & SFX:** Dynamic background music tracks, sound effects for jumping, shooting, coin pickups, enemy roar, and victory jingles.

---

## ⌨️ Controls & Cross-Platform Playability

| Action | Desktop (PC / Mac) | Mobile / Android / Touchscreens |
| :--- | :--- | :--- |
| **Move Left / Right** | `A` / `D` or `LEFT` / `RIGHT` | On-Screen **`<`** / **`>`** Buttons |
| **Jump** | `W` / `SPACE` / `UP` | On-Screen **`JUMP`** Button |
| **Shoot Fire Bullet** | `K` (Level 1, Level 3) | On-Screen **`FIRE`** Button |
| **Shoot Ice Bullet** | `K` (Level 2) / `L` or `I` (Level 3) | On-Screen **`ICE`** Button |
| **Menu / Pause / Exit** | `ESC` / Top Bar Button | Top Bar Exit Button / Modal Buttons |
| **Quick Level Select** | `1`, `2`, `3` *(Menu)* | Tap Level Buttons on Touchscreen |

---

## 🛠️ Tech Stack & Architecture

- **Language:** Java (JDK 8 / 11 / 17 / 21 compatible)
- **Game Engine:** libGDX 1.12.1 (LWJGL3 Desktop Backend)
- **Build Automation:** Gradle 8.7 (Multi-project build with Gradle Wrapper)
- **Design Patterns & Architecture:**
  - **Screen-Based State Machine:** Implements libGDX `Game` and `Screen` interfaces for smooth transitions between menu and gameplay states.
  - **Separation of Concerns:**
    - `core/`: Platform-agnostic game logic, cameras, asset management, and screens.
    - `desktop/`: Platform launcher, display mode (1280x720, VSync, 60 FPS cap), and window lifecycle.
    - `assets/`: Sound effects, music tracks, textures, and sprites.

---

## 🚀 How to Run Locally

### Prerequisites
- Java JDK 8 or newer installed (`java -version`)
- No separate Gradle installation required (Gradle Wrapper included)

### 1. Run from Source
**On Windows:**
```bash
gradlew.bat desktop:run
```

**On macOS / Linux:**
```bash
./gradlew desktop:run
```

---

## 🌐 Play in Browser & Deploy on itch.io (No Download Needed!)

The game can be played directly in any web browser on **Android**, **Windows**, **macOS**, and **Linux** without downloading any files or apps.

### 1. Build the itch.io Playable Package
Run the automated packaging task:

```bash
# On Windows
gradlew.bat :teavm:packageItchZip

# On macOS / Linux
./gradlew :teavm:packageItchZip
```

The output file is generated at:
```
dist/JohnWicksAdventure-HTML5.zip
```

### 2. Upload to itch.io
1. Go to your itch.io dashboard and edit your game project (e.g. `https://itch.io/game/edit/...`).
2. Under **Kind of project**, select: **HTML (You have a ZIP or HTML file that will play directly in the browser)**.
3. In the **Uploads** section, click **Upload files** and select `dist/JohnWicksAdventure-HTML5.zip`.
4. Once uploaded, check the box: **"This file will be played in the browser"**.
5. Under **Embed options**:
   - Set **Viewports dimensions**: `960` x `640` (the game will automatically scale to fit any screen).
   - Check **Mobile friendly** (orientation: Landscape).
   - Check **Automatically start on page load** and **Fullscreen button**.
6. Click **Save & View Page** — your game is now live and 100% playable in-browser on Android, Windows, and Mac!

---

## 📦 Build Standalone Executable (Optional Desktop FAT JAR)

To generate a single, self-contained **Fat JAR** containing all code, dependencies, and bundled assets:

```bash
gradlew.bat desktop:dist
```

The output file will be generated at:
```
desktop/build/libs/JohnWicksAdventure.jar
```

Anyone with Java installed can run the game directly by double-clicking the file or via terminal:
```bash
java -jar desktop/build/libs/JohnWicksAdventure.jar
```

---

## 👤 Author
- **Huzaifa** — [@huzaifa-dev78](https://github.com/huzaifa-dev78)
- Co-developed with Muhammad Rehan — [@Muhammad-Rehan20](https://github.com/Muhammad-Rehan20)
