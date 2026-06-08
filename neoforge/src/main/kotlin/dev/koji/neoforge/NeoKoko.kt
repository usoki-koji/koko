package dev.koji.neoforge

import dev.koji.koko.Koko
import dev.koji.neoforge.common.NeoCommonRegistry
import dev.koji.neoforge.compact.Compatibilities
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod

@Mod(Koko.MOD_ID)
class NeoKoko {
    constructor(modEventBus: IEventBus, modContainer: ModContainer) {
        Koko.LOGGER.info("Koko is loading...")
        Koko.init(NeoSkillsHandler)

        NeoCommonRegistry.register(modEventBus)
        Compatibilities.register()

        Koko.LOGGER.info("Koko has been successfully loaded.")
    }
}