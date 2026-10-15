package com.example.trampleguard;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.level.BlockEvent;

/**
 * 核心逻辑：拦截"耕地被踩踏"事件。
 * <p>
 * 原版 {@code FarmBlock} 在实体落地时会调用 {@code turnToDirt(...)} 把耕地变成泥土，
 * 耕地上的作物因此被破坏。NeoForge 在真正变泥土之前会先抛出
 * {@link BlockEvent.FarmlandTrampleEvent}，只要把这个事件取消掉，
 * 耕地就不会变泥土，作物也就不会被破坏。
 */
public final class TrampleHandler {

    private TrampleHandler() {
    }

    /**
     * 事件回调。方法签名必须与事件类型严格对应，通过
     * {@code NeoForge.EVENT_BUS.addListener(TrampleHandler::onFarmlandTrample)} 注册。
     * <p>
     * 这个事件只在服务端逻辑中触发，所以单人游戏里由内置服务端处理，客户端不需要额外判断。
     *
     * @param event 耕地踩踏事件（可取消）
     */
    public static void onFarmlandTrample(BlockEvent.FarmlandTrampleEvent event) {
        // 0) 总开关
        if (!Config.ENABLED.get()) {
            return;
        }

        Entity entity = event.getEntity();

        // 1) 按"谁踩的"决定是否保护
        if (entity instanceof Player) {
            if (!Config.PROTECT_FROM_PLAYERS.get()) {
                return; // 不保护玩家，交给原版处理
            }
        } else if (!Config.PROTECT_FROM_MOBS.get()) {
            return; // 不保护生物，交给原版处理
        }

        // 2) 如果配置要求"只在有作物时保护"，就先检查耕地上方
        if (Config.ONLY_WHEN_CROP_PLANTED.get() && !hasCropAbove(event)) {
            return;
        }

        // 3) 取消事件 —— 耕地不会变成泥土，作物保住
        event.setCanceled(true);

        if (Config.LOG_TRAMPLE_PREVENTED.get()) {
            TrampleGuard.LOGGER.info("[Trample Guard] 已阻止 {} 在 {} 踩踏耕地",
                    entity.getName().getString(), event.getPos().toShortString());
        }
    }

    /**
     * 判断耕地正上方是否长着植物。
     * <p>
     * 依次检查四类，任何一类命中即视为"有作物"：
     * <ol>
     *     <li>{@code minecraft:crops} 方块标签 —— 覆盖原版作物，以及那些
     *         "只把自己加进标签、没有继承 {@link CropBlock}" 的模组作物；</li>
     *     <li>{@link CropBlock} —— 原版小麦/胡萝卜/马铃薯/甜菜根，以及绝大多数模组作物；</li>
     *     <li>{@link StemBlock} / {@link AttachedStemBlock} —— 南瓜、西瓜的茎。</li>
     * </ol>
     *
     * @param event 踩踏事件，用于取世界与坐标
     * @return 上方长着作物返回 true
     */
    private static boolean hasCropAbove(BlockEvent.FarmlandTrampleEvent event) {
        BlockPos cropPos = event.getPos().above();
        BlockState above = event.getLevel().getBlockState(cropPos);

        return above.is(BlockTags.CROPS)
                || above.getBlock() instanceof CropBlock
                || above.getBlock() instanceof StemBlock
                || above.getBlock() instanceof AttachedStemBlock;
    }
}
