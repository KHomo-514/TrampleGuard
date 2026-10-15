# Trample Guard —— 作物踩踏保护（Minecraft 1.21.1 / NeoForge 21.1.259）

> 生物（以及玩家）踩踏耕地时，耕地不会被踩成泥土，因此**上面的作物永远不会被破坏**。

| 项目 | 值 |
|---|---|
| Minecraft | 1.21.1 |
| 加载器 | NeoForge **21.1.259** |
| Java | **21**（必须！见下文"JDK 版本"一节） |
| Gradle | 8.10.2 |
| mod id | `trampleguard` |
| 构建插件 | ModDevGradle (`net.neoforged.moddev`) |

---

## 一、原理（为什么这样就够了）

原版里"作物被踩坏"其实分两步，关键在第 1 步：

1. 实体落到耕地上时，`FarmBlock` 调用 `turnToDirt(...)`，**把耕地变成泥土**；
2. 泥土上不能长作物，于是作物方块被移除、掉落销毁。

所以只要阻止第 1 步，作物就永远安全。NeoForge 恰好在真正变泥土之前抛出了可取消事件：

```
net.neoforged.neoforge.event.level.BlockEvent.FarmlandTrampleEvent
```

本 mod 的全部核心逻辑就是一句话：

```java
event.setCanceled(true);
```

---

## 二、工程结构

```
trampleguard/
├── build.gradle                       # ModDevGradle 构建脚本
├── settings.gradle                    # 插件仓库配置
├── gradle.properties                  # ★ 版本号都在这里
├── gradlew / gradlew.bat              # Gradle Wrapper 启动脚本
├── gradle/wrapper/                    # wrapper 的 jar 与 properties
├── LICENSE                            # MIT 许可证
├── .gitignore
├── README.md
└── src/main/
    ├── java/com/example/trampleguard/
    │   ├── TrampleGuard.java          # mod 主类，注册配置 + 事件
    │   ├── TrampleHandler.java        # ★ 核心逻辑：取消踩踏事件
    │   └── Config.java                # 五个配置开关
    └── resources/
        ├── META-INF/neoforge.mods.toml # mod 元信息（1.21.1 用这个名字，不是 mods.toml）
        ├── icon.png                    # mods 列表里显示的图标
        └── pack.mcmeta
```

---

## 三、在 IDEA 里跑起来

### 方式 A（★ 推荐）：直接用本工程

本工程**自带完整的 Gradle Wrapper**（`gradlew`、`gradlew.bat`、`gradle/wrapper/gradle-wrapper.jar`），
可以独立构建，不需要另外下载官方 MDK。

1. 把 `trampleguard` 文件夹放到**无中文、无空格**的路径下。
2. IDEA 里 `File → Open`，选中 `trampleguard` 目录（含 `build.gradle` 那一层）。
3. 按第四节把 **Gradle JVM** 设成 JDK 21，然后点 Gradle 面板的刷新 🔄。
4. 同步成功后即可运行（见第五节）。

命令行也可以直接构建（前提：`JAVA_HOME` 指向 JDK 21）：

```powershell
.\gradlew.bat build          # 产物在 build/libs/
.\gradlew.bat runClient      # 直接启动客户端
```

### 方式 B：用官方 MDK 打底

如果本工程的 `net.neoforged.moddev` 插件版本 `2.0.78` 解析失败（版本号会随时间更新），
可以拿官方 MDK 打底：

1. 打开 <https://github.com/NeoForgeMDKs/MDK-1.21.1-ModDevGradle> ，点 **Code → Download ZIP**，解压到一个**路径不含中文和空格**的目录，例如 `D:\mcmods\mdk`。
2. 用 IDEA 打开这个 MDK 目录（`File → Open`，选中含 `build.gradle` 的那一层）。
3. 等 IDEA 自动同步完（右下角进度条走完，Gradle 面板能展开）。
4. 把本工程 `src/main/java/com/example/trampleguard/` 整个 **复制**到 MDK 的 `src/main/java/` 下。
5. 用本工程的 `src/main/resources/` 下的三个文件（`META-INF/neoforge.mods.toml`、`pack.mcmeta`、`icon.png`）**替换** MDK 里的同名文件。
6. 打开 MDK 的 `gradle.properties`，把这几项改成：

   ```properties
   minecraft_version=1.21.1
   neo_version=21.1.259
   mod_id=trampleguard
   mod_name=Trample Guard
   mod_group_id=com.example.trampleguard
   mod_version=1.0.0
   mod_authors=你的名字
   mod_description=Prevents mobs and players from trampling farmland.
   ```

7. 点 Gradle 面板的**刷新** 🔄，同步成功后即可运行（见第五节）。

---

## 四、IDEA 里必须确认的三处设置（★ 最容易踩的坑）

你之前那几张截图报的 `Unsupported class file major version 70`，根因是**用了 JDK 26**。

**Minecraft 1.21.1 + NeoForge 21.1.x 必须用 Java 21。**

### 1. Project SDK

`File → Project Structure…`（`Ctrl+Alt+Shift+S`）→ 左侧 **Project**：

- **SDK**：选 **21**（没有就 `Add SDK → Download JDK…`，Version 选 `21`，Vendor 选 `Eclipse Temurin`）
- **Language level**：`SDK default` 或 `21`

### 2. Gradle JVM

`File → Settings`（`Ctrl+Alt+S`）→ `Build, Execution, Deployment → Build Tools → Gradle`：

- **Gradle JVM**：选 **21**（**不要**留成 JDK 26！）
- **Use Gradle from**：`'gradle-wrapper.properties' file`
- 确认 **Offline mode** 没被勾上

改完 `Apply → OK`，回到右侧 Gradle 面板点刷新 🔄。

> 也可以在项目根目录的 `gradle.properties` 里硬指定 JDK（Gradle 6.7+ 支持）：
> ```properties
> org.gradle.java.home=C:\\Program Files\\Eclipse Adoptium\\jdk-21.0.x-hotspot
> ```
> 注意反斜杠要写双份。

### 3. ★ 关闭 Gradle 委托构建（否则运行时会报 `Read timed out`）

同样在 `Settings → Build, Execution, Deployment → Build Tools → Gradle`，**面板最上方**有两个下拉框：

| 选项 | 改成 |
|---|---|
| **Build and run using:** | **`IntelliJ IDEA`** |
| **Run tests using:** | **`IntelliJ IDEA`** |

两个默认值都是 `Gradle`。保持默认的话，你每次点运行，IDEA 都会先找 Gradle 守护进程做一次构建；
守护进程一旦被卡住（比如上一次的服务端没关），就会报：

```
java.net.SocketTimeoutException: Read timed out
```

构建没跑成，游戏就会拿着一份**过期的运行参数**启动，进而报出各种莫名其妙的错
（例如 `there is no mod with that name in the game`）。**改成 IntelliJ IDEA 后这类问题全部消失，启动也更快。**

---

## 五、运行与测试

### 启动客户端

在 IDEA 右侧 **Gradle** 面板里：

```
Tasks → neoforge → runClient     （双击）
```

或者直接在 **Run/Debug Configurations** 里选 `runClient` 点绿色三角 ▶。

首次启动会下载 Minecraft 资源，比较慢（国内网络可能要点耐心）。

### 验证作物是否真的被保护

进入一个创造模式世界，然后：

```mcfunction
# 1. 搭一块耕地并种上小麦
/setblock ~ ~ ~ farmland
/setblock ~ ~1 ~ wheat[age=7]
```

```mcfunction
# 2. 跳起来落到耕地上（玩家测试，protectFromPlayers=true）
#    跳跃落下高度约 1.25 格，超过原版 0.5 格的踩踏门槛。
```

**预期结果**：耕地仍然是 `farmland`，小麦仍然在原位（不会被弹掉、不会消失）。

再测生物：

```mcfunction
# 从 10 格高处召唤一只僵尸，让它砸在耕地上
/summon zombie ~ ~10 ~
# 或
/summon cow ~ ~10 ~
```

**预期结果**：耕地没变成泥土，作物还在。

**反向验证**（确认真的是本 mod 在起作用）：把配置文件里的 `protectFromPlayers` 改成 `false`，
重启游戏后再跳一次 → 耕地应该会被踩成泥土。

用指令确认方块类型：

```mcfunction
/execute if block ~ ~ ~ farmland run say 耕地保住了 ✅
```

### 启动服务端

```
Gradle → Tasks → neoforge → runServer
```

---

## 六、打包发布

```
Gradle → Tasks → build → build      （双击）
```

产物在：

```
build/libs/trampleguard-1.0.0.jar
```

把**这个 jar**（不要带 `-sources` 的那个）丢进 `.minecraft/mods/` 即可。
服务端和客户端都要装（`side="BOTH"`）。

---

## 七、配置说明

配置文件：`config/trampleguard-common.toml`
（开发环境在 `run/config/` 下；正式游戏在 `.minecraft/config/` 下）

```toml
# 总开关（false = 完全关闭本 mod，恢复原版踩踏行为）
enabled = true

# 生物（僵尸、村民、牛羊……）踩踏耕地是否保护
protectFromMobs = true

# 玩家踩踏耕地是否保护（true = 你自己跳也不会踩坏作物）
protectFromPlayers = true

# 是否只在耕地上方种着作物时才保护
# true  = 空耕地照旧会被踩成泥土，只有长了作物的耕地免疫（默认，最贴近原版手感）
# false = 所有耕地都免疫踩踏
onlyWhenCropPlanted = true

# 每次成功阻止踩踏时是否输出一行日志（排查问题时打开，正常游戏会刷屏）
logTramplePrevented = false
```

也可以在游戏内 `Mods → Trample Guard → Config` 里改。

---

## 八、常见问题

### Q1. 报 `Unsupported class file major version 70`

JDK 太新。`70` 对应 Java 26。按第四节把 **Project SDK** 和 **Gradle JVM** 都换成 **21**。
（对照表：61=Java17，65=Java21，66=Java22，69=Java25，70=Java26）

### Q2. 报 `Connect timed out` 或 `不兼容的 Gradle JVM`

多半还是 JDK 问题导致 Gradle daemon 起不来。先按 Q1 换成 JDK 21，再同步。
如果确实是网络问题（下载 Gradle 发行版/依赖超时），可以换镜像：

`gradle/wrapper/gradle-wrapper.properties`：

```properties
distributionUrl=https\://mirrors.cloud.tencent.com/gradle/gradle-8.10.2-bin.zip
```

`settings.gradle` 的 `pluginManagement.repositories` 里也可以补阿里云镜像：

```groovy
maven { url = 'https://maven.aliyun.com/repository/gradle-plugin' }
maven { url = 'https://maven.aliyun.com/repository/public' }
```

### Q3. 报 `FarmlandTrampleEvent` 找不到 / `setCanceled` 用不了

说明该版本的事件类名或包路径有差异，在 IDEA 里按 `Ctrl+N` 搜索 `FarmlandTrampleEvent`，按其实际包路径修 `TrampleHandler.java` 的 import 即可。
（NeoForge 21.1.x 的正常路径是 `net.neoforged.neoforge.event.level.BlockEvent.FarmlandTrampleEvent`。）

### Q4. 游戏里没生效 / 作物还是被踩掉

按顺序排查：

1. F3 看左上角是不是 1.21.1 + NeoForge 21.1.259；
2. `Mods` 列表里有没有 `Trample Guard`（没有就是 jar 没装上）；
3. 日志里有没有 `[Trample Guard] 已加载：耕地/作物踩踏保护生效。`；
4. 配置里 `onlyWhenCropPlanted = true` 时，**空耕地仍然会被踩成泥土**，这是设计如此，别误判；
5. 配置里 `enabled` 是不是被改成了 `false`；
6. 把 `logTramplePrevented` 改成 `true`，再踩一次——日志里有输出说明事件确实触发了；
7. 确认装的其他 mod 没有覆盖同一个逻辑。

### Q5. 报 `Failed to parse template script ... SimpleTemplateScript1.groovy: 1: Unexpected input`

`neoforge.mods.toml` 会被 Groovy 模板引擎处理，**文件里任何"美元符号 + 花括号"的组合都会被当成 Groovy 表达式求值**。
所以不能在注释、描述等地方写那个组合的示例。把那段文字改成"美元符号加花括号"之类的说法即可。

同理，`build.gradle` 里 `expand` 用到的属性值如果本身含有那个组合，也会出问题。

### Q6. 我想改成"所有耕地都不会被踩坏"

配置里把 `onlyWhenCropPlanted` 设为 `false`。

### Q7. 我看不到 `TrampleGuard` 的日志

日志文件在 `run/logs/latest.log`（开发环境）或 `.minecraft/logs/latest.log`。
搜索关键字 `Trample Guard` 或者类名 `TrampleGuard`。

### Q8. 点运行时在「正在构建」阶段报 `java.net.SocketTimeoutException: Read timed out`

IDEA 把构建**委托给了 Gradle**，而 Gradle 守护进程被卡住了（最常见的原因：上一次用 Gradle 任务
启动的服务端没关，`runServer` 永不退出，把 daemon 永久占死）。

解决：按 **第四节第 3 点**把 `Build and run using` / `Run tests using` 都改成 `IntelliJ IDEA`。
另外先确认没有遗留的服务端在跑：

```powershell
netstat -ano | findstr :25565
```

有输出说明还在跑，到 IDEA 的运行窗口点红色停止方块，或在服务端控制台敲 `stop`。

### Q9. 报 `contains mod entrypoint class ... but there is no mod with that name in the game`

**改了 `mod_id` / `rootProject.name` 之后没重新同步**，IDEA 里的运行配置还留着旧 mod id。
按第九节末尾的说明处理：重新同步 + 删掉旧的临时运行配置。

### Q10. 想临时确认"某个现象是不是本 mod 造成的"

把配置里的 `enabled` 改成 `false`（游戏内 `Mods → Trample Guard → Config` 改即时生效），
对比一下行为差异即可，不需要卸载 jar。

---

## 九、改名 / 二次开发

本工程当前的标识：

| 位置 | 值 |
|---|---|
| `mod_id` | `trampleguard` |
| 游戏内显示名 | `Trample Guard` |
| Java 包名 | `com.example.trampleguard` |
| 主类 | `TrampleGuard` |
| 产物 | `build/libs/trampleguard-1.0.0.jar` |
| 配置文件 | `config/trampleguard-common.toml` |

想换成别的名字，需要同步改这几处：

1. `gradle.properties` → `mod_id`、`mod_name`、`mod_group_id`
2. `settings.gradle` → `rootProject.name`
3. `TrampleGuard.java` → `MOD_ID` 常量
4. 包名（目录名 + 三个 java 文件的 `package` 声明）
5. `trampleguard.mods.toml`（实际叫 `neoforge.mods.toml`）和 `build.gradle` 用的是占位符，**不用手改**

IDEA 里可以用 `Refactor → Rename` 自动完成第 4 步。

⚠️ `mod_id` 只能用小写字母、数字、下划线，且必须以字母开头。

> **⚠️ 改名后的必做动作**：`mod_id` 和 `rootProject.name` 一改，IDEA 里**旧的运行配置就全部失效**了
> （参数里还留着旧 mod id，会报 `there is no mod with that name in the game`）。
> 改完必须 `File → Sync All Gradle Projects`，然后在 `Run → Edit Configurations...` 里
> **删掉所有名字带旧项目名的临时配置**，只保留刷新后重新生成的 `Client` / `Server`。

---

## 十、许可

MIT。随便用、随便改。
