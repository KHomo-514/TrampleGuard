# Trample Guard

<img src="src/main/resources/icon.png" alt="Trample Guard" width="96">

防止生物和玩家踩踏耕地导致作物被破坏。

**Minecraft 1.21.1 ｜ NeoForge 21.1.259+ ｜ 需要 Java 21**

## 原理

原版中实体落到耕地上会把它踩成泥土，作物随之掉落销毁。
本 mod 取消 NeoForge 的 `FarmlandTrampleEvent`，耕地保持不变，作物自然安全。

> 核心逻辑就一行：`event.setCanceled(true);`

## 安装

1. 装好 **NeoForge 21.1.x**（对应 Minecraft 1.21.1）
2. 把 `trampleguard-1.0.0.jar` 放进 `mods/` 文件夹
3. **客户端和服务端都要装**

## 配置

文件位置：`config/trampleguard-common.toml`
也可以在游戏内 `Mods → Trample Guard → Config` 修改（即时生效）。

| 选项 | 默认 | 说明 |
|---|---|---|
| `enabled` | `true` | 总开关 |
| `protectFromMobs` | `true` | 保护耕地不被生物踩坏 |
| `protectFromPlayers` | `true` | 保护耕地不被玩家踩坏 |
| `onlyWhenCropPlanted` | `true` | 只保护种着作物的耕地 |
| `logTramplePrevented` | `false` | 每次拦截时输出一行日志 |

> `onlyWhenCropPlanted = true` 时，**没种作物的空耕地仍会被踩成泥土**，这是有意设计的，最贴近原版手感。
> 想让所有耕地都免疫，改成 `false`。

## 从源码构建

需要 **JDK 21**。

```bash
./gradlew build        # 产物：build/libs/trampleguard-1.0.0.jar
./gradlew runClient    # 启动开发客户端
```

用 IDEA 打开：`File → Open` 选项目目录，再把
`Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle JVM` 设为 **21**。

构建或运行遇到问题，见 [DEVELOPMENT.md](DEVELOPMENT.md)。

## 验证是否生效

```mcfunction
/setblock ~ ~ ~ farmland
/setblock ~ ~1 ~ wheat[age=7]
```

原地跳一下：耕地仍是耕地、小麦还在 = 生效。

## 许可

[MIT](LICENSE)
