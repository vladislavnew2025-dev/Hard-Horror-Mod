package com.hardhorror.forge;

import com.hardhorror.EndingController;
import com.hardhorror.FearSystem;
import com.hardhorror.HardHorrorMod;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Block;
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
    private static final long ANOMALY_COOLDOWN_TICKS = 120L;
    private static final long AMBIENT_COOLDOWN_TICKS = 160L;
    private static final long ENDING_STEP_COOLDOWN_TICKS = 220L;

    private final HardHorrorMod core;
    private final Map<UUID, Long> lastHudTick = new HashMap<>();
    private final Map<UUID, Long> lastAnomalyTick = new HashMap<>();
    private final Map<UUID, Long> lastAmbientTick = new HashMap<>();
    private final Map<UUID, Long> lastEndingTick = new HashMap<>();
    private final Map<UUID, EndingController.EndingState> endingStates = new HashMap<>();
    private final Random random = new Random();

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
                                    if (ctx.getSource().getEntity() instanceof ServerPlayer player) {
                                        player.sendSystemMessage(Component.literal("Watcher appears at distance " + watcher.distanceBlocks()));
                                    }
                                    ctx.getSource().sendSuccess(() -> Component.literal("Watcher forced at distance " + watcher.distanceBlocks()), false);
                                    return Command.SINGLE_SUCCESS;
                                }))
                        .then(Commands.literal("anomaly_test")
                                .executes(ctx -> {
                                    if (ctx.getSource().getEntity() instanceof ServerPlayer player) {
                                        triggerAnomaly(player, true);
                                        ctx.getSource().sendSuccess(() -> Component.literal("Anomaly test triggered."), false);
                                    }
                                    return Command.SINGLE_SUCCESS;
                                }))
                        .then(Commands.literal("plan_test")
                                .executes(ctx -> {
                                    if (ctx.getSource().getEntity() instanceof ServerPlayer player) {
                                        core.fearSystem().setFear(100);
                                        triggerAnomaly(player, true);
                                        triggerAmbient(player, true);
                                        stepEnding(player, true);
                                        ctx.getSource().sendSuccess(() -> Component.literal("Plan test: fear=100 + anomaly + ending step."), false);
                                    }
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

        if (gameTime - lastHudTick.getOrDefault(uuid, Long.MIN_VALUE) >= HUD_UPDATE_TICKS) {
            lastHudTick.put(uuid, gameTime);
            String hud = "Fear: " + core.fearSystem().getFear() + " / 100  [" + core.fearSystem().level() + "]";
            player.displayClientMessage(Component.literal(hud), true);
        }

        FearSystem.FearLevel level = core.fearSystem().level();

        if ((level == FearSystem.FearLevel.HIGH || level == FearSystem.FearLevel.CRITICAL)
                && gameTime - lastAnomalyTick.getOrDefault(uuid, Long.MIN_VALUE) >= ANOMALY_COOLDOWN_TICKS) {
            double chance = (level == FearSystem.FearLevel.CRITICAL) ? 0.45d : 0.22d;
            if (random.nextDouble() <= chance) {
                triggerAnomaly(player, false);
                lastAnomalyTick.put(uuid, gameTime);
            }
        }

        if (level == FearSystem.FearLevel.MEDIUM || level == FearSystem.FearLevel.HIGH || level == FearSystem.FearLevel.CRITICAL) {
            if (gameTime - lastAmbientTick.getOrDefault(uuid, Long.MIN_VALUE) >= AMBIENT_COOLDOWN_TICKS) {
                if (random.nextDouble() <= 0.25d) {
                    triggerAmbient(player, false);
                    lastAmbientTick.put(uuid, gameTime);
                }
            }
        }

        if (level == FearSystem.FearLevel.CRITICAL
                && gameTime - lastEndingTick.getOrDefault(uuid, Long.MIN_VALUE) >= ENDING_STEP_COOLDOWN_TICKS) {
            stepEnding(player, false);
            lastEndingTick.put(uuid, gameTime);
        }
    }

    private void triggerAmbient(ServerPlayer player, boolean forced) {
        BlockPos pos = player.blockPosition().offset(random.nextInt(9) - 4, 0, random.nextInt(9) - 4);
        var ambientSound = switch (random.nextInt(3)) {
            case 0 -> SoundEvents.AMBIENT_CAVE;
            case 1 -> SoundEvents.SOUL_SAND_STEP;
            default -> SoundEvents.WARDEN_NEARBY_CLOSER;
        };
        player.level().playSound(null, pos, ambientSound, SoundSource.AMBIENT, forced ? 1.5f : 1.0f, 0.7f + random.nextFloat() * 0.5f);
    }

    private void triggerAnomaly(ServerPlayer player, boolean forced) {
        BlockPos pos = player.blockPosition().offset(random.nextInt(7) - 3, 0, random.nextInt(7) - 3);

        var sound = switch (random.nextInt(4)) {
            case 0 -> SoundEvents.AMETHYST_BLOCK_CHIME;
            case 1 -> SoundEvents.STONE_BREAK;
            case 2 -> SoundEvents.CHAIN_BREAK;
            default -> SoundEvents.SCULK_SENSOR_CLICKING;
        };
        player.level().playSound(null, pos, sound, SoundSource.AMBIENT, 1.1f, 0.8f + random.nextFloat() * 0.4f);

        // Fake block-break visual/audio event (world distortion) without editing real blocks.
        var state = player.level().getBlockState(pos);
        player.level().levelEvent(2001, pos, Block.getId(state));

        int duration = forced ? 80 : 40;
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, duration, 0, false, false, true));
        player.displayClientMessage(Component.literal("Мир искажается..."), true);
    }

    private void stepEnding(ServerPlayer player, boolean forced) {
        UUID uuid = player.getUUID();
        EndingController.EndingState current = endingStates.getOrDefault(uuid, EndingController.EndingState.DORMANT);
        EndingController.EndingState next = core.endingController().nextState(core.fearSystem(), current);
        endingStates.put(uuid, next);

        if (next != current || forced) {
            switch (next) {
                case OUT_OF_BODY_CAMERA -> player.sendSystemMessage(Component.literal("...кто это?"));
                case WORLD_REWRITE -> player.sendSystemMessage(Component.literal("перезапись..."));
                case COMPLETE -> player.sendSystemMessage(Component.literal("мир принят."));
                default -> {
                }
            }
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
            player.level().playSound(null, player.blockPosition(), SoundEvents.GHAST_SCREAM, SoundSource.MASTER, 1.8f, 0.8f);
        }
    }
}
