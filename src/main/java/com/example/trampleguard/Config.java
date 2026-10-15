package com.example.trampleguard;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 配置文件定义。
 * <p>
 * 生成的配置文件位于：
 * <ul>
 *     <li>开发环境：{@code <项目目录>/run/config/trampleguard-common.toml}</li>
 *     <li>正式游戏：{@code .minecraft/config/trampleguard-common.toml}</li>
 * </ul>
 * 也可以在游戏内的「Mods → Trample Guard → Config」界面里直接改（改完即时生效）。
 */
public final class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    /** 总开关。关掉后完全恢复原版行为，适合临时排查"是不是本 mod 造成的"。 */
    public static final ModConfigSpec.BooleanValue ENABLED = BUILDER
            .comment("Master switch for the whole mod. / 本 mod 总开关",
                     "true  = 保护生效（默认）",
                     "false = 完全关闭，恢复原版踩踏行为")
            .define("enabled", true);

    /** 非玩家生物（僵尸、村民、牛羊、苦力怕等）踩踏时是否保护。 */
    public static final ModConfigSpec.BooleanValue PROTECT_FROM_MOBS = BUILDER
            .comment("Prevent non-player mobs from trampling farmland. / 是否阻止生物踩踏耕地",
                     "true  = 保护，耕地不会被踩成泥土（默认）",
                     "false = 保持原版行为，生物会把耕地踩坏")
            .define("protectFromMobs", true);

    /** 玩家踩踏时是否保护。 */
    public static final ModConfigSpec.BooleanValue PROTECT_FROM_PLAYERS = BUILDER
            .comment("Prevent players from trampling farmland. / 是否阻止玩家踩踏耕地",
                     "true  = 保护，玩家跳跃落地也不会踩坏作物（默认）",
                     "false = 保持原版行为，玩家可以踩坏耕地")
            .define("protectFromPlayers", true);

    /** 是否只保护"上面种着作物"的耕地。 */
    public static final ModConfigSpec.BooleanValue ONLY_WHEN_CROP_PLANTED = BUILDER
            .comment("Only protect farmland that actually has a crop growing on it. / 仅在耕地上方有作物时才保护",
                     "true  = 只有长了作物的耕地免疫踩踏，空耕地照旧会被踩成泥土（默认，最贴近原版手感）",
                     "false = 任何耕地都免疫踩踏")
            .define("onlyWhenCropPlanted", true);

    /** 每次成功阻止踩踏时是否输出一行日志。 */
    public static final ModConfigSpec.BooleanValue LOG_TRAMPLE_PREVENTED = BUILDER
            .comment("Log one line each time a trample is prevented. / 每次阻止踩踏时输出日志",
                     "true  = 输出（排查问题时打开，正常游戏会刷屏）",
                     "false = 不输出（默认）")
            .define("logTramplePrevented", false);

    /** 由 BUILDER 构建出的、要交给 NeoForge 注册的配置规格。 */
    public static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {
    }
}
