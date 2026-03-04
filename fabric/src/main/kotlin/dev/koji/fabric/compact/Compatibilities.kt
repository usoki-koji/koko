package dev.koji.neoforge.compact

import dev.koji.neoforge.compact.curios.CuriosCompact
import dev.koji.neoforge.compact.ironspells.IronSpellsCompact
import net.neoforged.fml.ModList
import net.neoforged.neoforge.common.NeoForge

object Compatibilities {
    fun register() {
        val modList = ModList.get()

        if (modList.isLoaded("curios")) {
            CuriosCompact.register()

            NeoForge.EVENT_BUS.register(CuriosCompact)
        }

        if (modList.isLoaded("irons_spellbooks")) {
            IronSpellsCompact.register()

            NeoForge.EVENT_BUS.register(IronSpellsCompact)
        }
    }
}