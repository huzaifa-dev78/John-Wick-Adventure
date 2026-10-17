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

## ⌨️ Controls

| Action | Primary Key | Alternate Key |
| :--- | :--- | :--- |
| **Move Left / Right** | `A` / `D` | `LEFT` / `RIGHT` Arrow Keys |
| **Jump** | `W` / `SPACE` | `UP` Arrow Key |
| **Shoot Fire Bullet** | `J` | `X` |
| **Shoot Ice Bullet** | `K` | `C` |
| **Quick Level Select** | `1`, `2`, `3` *(Menu)* | — |
| **Exit Game** | `ESC` | — |

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

## 📦 Build Standalone Executable (For itch.io & Releases)

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
