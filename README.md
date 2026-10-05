# BlazeTweaks (Forge 1.12.2)

A client-side Minecraft Forge 1.12.2 mod that adds several useful features for technical Minecraft players.<br>
The mod was created primarily to add features that I found useful in the game, but it’s now available to everyone!

![Preview](https://github.com/RedBlaze908/PistonOrderOverlay/blob/main/screenshot/pistorder.png)
---

## 🌟 Features

- **Push Order Overlay:** Displays the exact sequence in which blocks are processed and moved during piston activation.
- **Piston Status Inspection:** Shows whether an operation is a `PUSH`, `RETRACT`, or blocked (`X`).
- **Toggle Keybind:** Turn the overlay on or off anytime using a customizable keybind (Default: **`O`**).
- **Rejoin Button:** It allows you to rejoin a world (saving it) or rejoin a multiplayer server automatically.
- **Enchantment Predictor:** It helps you on finding which enchantments you will get (works perfectly in singleplayer, not really in muliplayer)
- **Backup Save System:** It makes backup with the command `/backup save` and you can reload the backup with `/backup restore (filename)`, it automatically close your world and put the backup in the world folder (so it replace it)
- **RNG Seed:** It find the first rng seed (singleplayer) `/rngseed`
- **RNG Weather Predictor:** It tells you when the next thunder will come (singleplayer) `/rngweather`
- **RNG Lightning Finder:** It find only where the next lightining it's going to stike, it doesn't find the region where there is the RNG. `/lightningfinder <count> [radius] [centerChunkX centerChunkZ]`
- **Shulkerbox Tooltip:** It make a small tooltip on the shulker item where it tells you the amount of redstone signal that it gives to the comparator and how much, in percentage, are fill.
- **Dungeon Bounding Box:** It shows you where you can find spawner that are not been generated normally by the game. You can find it in the options menu by pressing **`RMENU`**
- **Client-Side Only:** Works entirely on the client, making it safe for multiplayer servers.

---

## 🎮 How to Use

I made a full video on how to use this mod! Check my youtube channel: RedBlaze908

---

## 📜 Credits & License

- **Piston Order Mod Creator:** [Fallen-Breath](https://github.com/Fallen-Breath) (*Pistorder*) I took inspiration from him for the piston overlay.
- **Forge 1.12.2 Mod Creator:** Created by **RedBlaze908**

This project is open-source and released under the **MIT License**.

---

## 🛠️ Building from Source

To compile the mod yourself, clone the repository and run:

```cmd
gradlew.bat clean build

gradlew.bat runClient
```
![Preview](https://github-repo-readme-stats.vercel.app/api?username=RedBlaze908&repo=PistonOrderOverlay&theme=dark)
