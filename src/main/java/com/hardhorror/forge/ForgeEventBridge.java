package com.hardhorror.forge;

import com.hardhorror.FearSystem;
import com.hardhorror.HardHorrorMod;
import java.time.Instant;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Bridge from Forge events to core mod systems.
 */
public final class ForgeEventBridge {
    private final HardHorrorMod core;

    public ForgeEventBridge(HardHorrorMod core) {
        this.core = core;
    }

    public static void onCommonSetup(FMLCommonSetupEvent event) {
        // Reserved for future packet registration, networking, etc.
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!(event.player instanceof ServerPlayer player)) {
            return;
        }

        if (player.level().isClientSide()) {
            return;
        }

        if (player.getLightLevelDependentMagicValue() < 0.3f) {
            core.fearSystem().addFear(1);
        }
        if (player.getY() < 40) {
            core.fearSystem().addFear(2);
        }

        FearSystem.FearLevel level = core.fearSystem().level();
        if (level == FearSystem.FearLevel.CRITICAL) {
            player.sendSystemMessage(Component.literal("перезапись..."));
        }
    }

    @SubscribeEvent
    public void onDoorInteract(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (event.getLevel().isClientSide()) {
            return;
        }

        String blockId = event.getLevel().getBlockState(event.getPos()).getBlock().getDescriptionId();
        if (blockId.contains("door") && core.screamerScheduler().onDoorOpened(Instant.now())) {
            player.sendSystemMessage(Component.literal("..."));
        }
    }
}
