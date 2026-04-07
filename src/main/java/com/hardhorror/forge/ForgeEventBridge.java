package com.hardhorror.forge;

import com.hardhorror.FearSystem;
import com.hardhorror.HardHorrorMod;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Bridge from Forge events to core mod systems.
 */
public final class ForgeEventBridge {
    private static final long HUD_UPDATE_TICKS = 20L;
    private static final long REWRITE_MESSAGE_COOLDOWN_TICKS = 200L;

    private final HardHorrorMod core;
    private final Map<UUID, Long> lastHudTick = new HashMap<>();
    private final Map<UUID, Long> lastRewriteTick = new HashMap<>();

    public ForgeEventBridge(HardHorrorMod core) {
        this.core = core;
    }

    public static void onCommonSetup(FMLCommonSetupEvent event) {
        // Reserved for future packet registration, networking, etc.
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("psychhorror")
                        .then(Commands.literal("fear_get")
                                .executes(ctx -> {
                                    int fear = core.fearSystem().getFear();
                                    ctx.getSource().sendSuccess(() -> Component.literal("Fear=" + fear + " level=" + core.fearSystem().level()), false);
                                    return Command.SINGLE_SUCCESS;
                                }))
                        .then(Commands.literal("fear_set")
                                .then(Commands.argument("value", IntegerArgumentType.integer(0, 100))
                                        .executes(ctx -> {
                                            int value = IntegerArgumentType.getInteger(ctx, "value");
                                            core.fearSystem().setFear(value);
                                            ctx.getSource().sendSuccess(() -> Component.literal("Fear set to " + value), false);
                                            return Command.SINGLE_SUCCESS;
                                        })))
                        .then(Commands.literal("fear_add")
                                .then(Commands.argument("value", IntegerArgumentType.integer(1, 100))
                                        .executes(ctx -> {
                                            int value = IntegerArgumentType.getInteger(ctx, "value");
                                            core.fearSystem().addFear(value);
                                            ctx.getSource().sendSuccess(() -> Component.literal("Fear now " + core.fearSystem().getFear()), false);
                                            return Command.SINGLE_SUCCESS;
                                        })))
                        .then(Commands.literal("screamer_test")
                                .executes(ctx -> {
                                    core.encounterManager().triggerTestScreamer();
                                    ctx.getSource().sendSuccess(() -> Component.literal("Screamer test triggered."), false);
                                    return Command.SINGLE_SUCCESS;
                                }))
                        .then(Commands.literal("watcher_test")
                                .executes(ctx -> {
                                    core.encounterManager().forceSpawnWatcher();
                                    var watcher = core.encounterManager().watcher();
                                    ctx.getSource().sendSuccess(() -> Component.literal("Watcher forced at distance " + watcher.distanceBlocks()), false);
                                    return Command.SINGLE_SUCCESS;
                                }))
        );
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!(event.player instanceof ServerPlayer player)) {
            return;
        }

        if (event.phase != TickEvent.Phase.END || player.level().isClientSide()) {
            return;
        }

        if (player.getLightLevelDependentMagicValue() < 0.3f) {
            core.fearSystem().addFear(1);
        }
        if (player.getY() < 40) {
            core.fearSystem().addFear(2);
        }

        long gameTime = player.level().getGameTime();
        UUID uuid = player.getUUID();

        // HUD in actionbar: updates once per second.
        if (gameTime - lastHudTick.getOrDefault(uuid, Long.MIN_VALUE) >= HUD_UPDATE_TICKS) {
            lastHudTick.put(uuid, gameTime);
            String hud = "Fear: " + core.fearSystem().getFear() + " / 100  [" + core.fearSystem().level() + "]";
            player.displayClientMessage(Component.literal(hud), true);
        }

        FearSystem.FearLevel level = core.fearSystem().level();
        if (level == FearSystem.FearLevel.CRITICAL
                && gameTime - lastRewriteTick.getOrDefault(uuid, Long.MIN_VALUE) >= REWRITE_MESSAGE_COOLDOWN_TICKS) {
            lastRewriteTick.put(uuid, gameTime);
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
