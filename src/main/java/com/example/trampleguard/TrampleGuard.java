package com.example.trampleguard;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

/**
 * Trample Guard —— 作物踩踏保护
 * <p>
 * 对应 Minecraft 1.21.1 + NeoForge 21.1.x。
 * <p>
 * 原版逻辑：任何生物（或玩家）从一定高度落到耕地上时，耕地会被踩成泥土，
 * 上面的作物随之掉落销毁。本 mod 通过监听 NeoForge 的
 * {@link net.neoforged.neoforge.event.level.BlockEvent.FarmlandTrampleEvent}
 * 并取消该事件，让耕地保持耕地，作物自然就保住了。
 *
 * @author YourName
 */
@Mod(TrampleGuard.MOD_ID)
public class TrampleGuard {

    /** mod id，必须与 gradle.properties 里的 mod_id 以及 neoforge.mods.toml 保持一致。 */
    public static final String MOD_ID = "trampleguard";

    public static final Logger LOGGER = LogUtils.getLogger();

    /**
     * NeoForge 的 mod 主类构造函数。
     * <p>
     * 1.21.1 的 NeoForge 支持在构造函数里直接注入 {@link IEventBus}（mod 事件总线）
     * 和 {@link ModContainer}（本 mod 的容器对象）。
     *
     * @param modEventBus  mod 生命周期事件总线（注册物品、配置等）
     * @param modContainer 本 mod 的容器，用来注册配置文件
     */
    public TrampleGuard(IEventBus modEventBus, ModContainer modContainer) {
        // 注册通用配置：生成 config/trampleguard-common.toml
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        // 注册到游戏事件总线（NeoForge.EVENT_BUS），监听"耕地被踩踏"事件
        NeoForge.EVENT_BUS.addListener(TrampleHandler::onFarmlandTrample);

        LOGGER.info("[Trample Guard] 已加载：耕地/作物踩踏保护生效。");
    }
}
