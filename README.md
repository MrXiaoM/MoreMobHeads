# MoreMobHeads

fork from [JoelGodOfwar 338b93](https://github.com/JoelGodOfwar/MoreMobHeads/tree/338b93b02e535dae2401f191f4726aa46081dfdc)

> This plugin adds all the Minecraft Mob Heads of the MoreMobHeads 
> datapack hosted by Xisumavoid from the Hermitcraft server, and expands 
> those heads to all Mobs in the game with textures available at the time 
> of release, including all Villager professions and types.
>
> All 22 named Tropical Fish have been added.
>
> ALSO DOES PLAYER HEADS

# What we do in the fork

The old code has annoyed the developer(me) in my server. So we decide to drop original author and make our version.

* Use Gradle to build plugin.
* Add MoreMobHeads as a module of the project.
* Use `spigot-api` instead of `spigot`. NMS is not needed in fact.
* Remove ALL languages because they are low-quality. (translated from *Machine*)
* Add Simplified Chinese translation.

# Build

```shell
gradlew :bukkit:build
```
