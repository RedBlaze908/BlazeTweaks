# BlazeTweaks (Forge 1.12.2)

A client-side Minecraft Forge 1.12.2 mod that adds several useful features for technical Minecraft players.<br>
The mod was created primarily to add features that I found useful in the game, but it’s now available to everyone!

![Downloads](https://img.shields.io/github/downloads/RedBlaze908/BlazeTweaks/total?style=for-the-badge&label=Downloads)

![Preview](https://github.com/RedBlaze908/BlazeTweaks/blob/main/src/main/resources/assets/blazetweaks/texture/logo.png)
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
- **Falling Block Info:** It's possible to see more info about the falling blocks. You can find it in the options menu by pressing **`RMENU`**
- **Falling Block Entity Ticking Toggle:** You can deactivate entity ticking only for falling blocks. (Default: **`BACKSLASH`**).
- **Entity Ticking Toggle:** This deactivate all the entity ticking in the world. (Default: **`NONE`**, you can find it in the **`RMENU`**).
- **Ticks Freeze**: You can freeze the ticks with `/instant freeze`, unfreeze with `/instat unfreeze`, and go forward with `/instant step (n)`. This type of freeze, can stop even tnt and falling blocks, in this way you can visualize them better.
- **Lazy Chunk Simulation:** it allows you to simulate a lazy chunk with the command `/lazychunk <xChunk> <yChunk>`, you can turn it off wit `/lazychunk off <xChunk> <yChunk>`, you can see a list of the chunks that you put in lazy with `/lazychunk list` and finally you can use `/lazychunk auto on/off` for turn on or off the system that, when the chunk is outside the render distance, it becomes again a normal chunk. (only singleplayer)
- **ITT Switch:** You can turn on ITT in singleplayer with the command `/instanttile on` for turn it on, `/instanttile off` for turn it off, `/instanttile toggle` for invert the status of the ITT, `/instanttile status` for check if is on or off. (only singleplayer)
- **IF Switch:** Tou can turn on Instant Fall in singleplayer with the command `/instantfall on` for turn it on, `/instantfall off` for turn it off, `/instantfall toggle` for invert the status of the IF, `/instantfall status` for check if is on or off. (only singleplayer)
- **Autosave Detector Overlay:** You can find a button in the custom menu (button for open the menu: `RMENU`) then an overlay will appear near to the hotbar (only singleplayer).
- **Entity ID Counter:** it allows you to see the ID counter of the session with `/eid get`, you can change the value with `/eid set n`, you can increase it with `/eid increment` or decrese it with `/eid decrement`, you can also make a collision and have you entity with the same ID with `/eid collide n`.
- **Client-Side Only:** Works entirely on the client, making it safe for multiplayer servers.

---

## 🎮 How to Use

I made a full video on how to use this mod! Check my youtube channel: RedBlaze908

This mod require: **mixinbooter** version: 11.17

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
![Preview](https://github-repo-readme-stats.vercel.app/api?username=RedBlaze908&repo=BlazeTweaks&theme=dark)
