package com.hardhorror.forge;

import com.hardhorror.HardHorrorMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Forge entrypoint for Minecraft 1.20.1 wiring.
 *
 * <p>This class is intentionally placed in a dedicated package so the
 * lightweight local quick-build (without Forge dependencies) keeps working.</p>
 */
@Mod(HardHorrorMod.MOD_ID)
public final class HardHorrorForgeEntrypoint {
    private final HardHorrorMod core = new HardHorrorMod();

    public HardHorrorForgeEntrypoint() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(ForgeEventBridge::onCommonSetup);
        MinecraftForge.EVENT_BUS.register(new ForgeEventBridge(core));
    }
}
