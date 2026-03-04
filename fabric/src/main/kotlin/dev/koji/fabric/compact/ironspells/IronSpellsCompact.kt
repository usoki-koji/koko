package dev.koji.neoforge.compact.ironspells

import dev.koji.koko.Koko
import dev.koji.neoforge.NeoKokoConfig
import dev.koji.koko.common.content.Translatable
import dev.koji.koko.common.helpers.MainHelper
import dev.koji.koko.common.models.modifiers.AbstractSkillModifier
import dev.koji.koko.common.models.modifiers.AbstractSkillModifierFilter
import dev.koji.koko.common.models.sources.AbstractSkillSource
import dev.koji.neoforge.compact.ironspells.modifiers.SpellCastSkillEffect
import dev.koji.neoforge.compact.ironspells.modifiers.SpellInscribeSkillEffect
import dev.koji.neoforge.compact.ironspells.modifiers.filters.SpellCastSkillEffectFilter
import dev.koji.neoforge.compact.ironspells.modifiers.filters.SpellInscribeSkillEffectFilter
import dev.koji.neoforge.compact.ironspells.sources.SpellCastSource
import io.redspace.ironsspellbooks.api.events.InscribeSpellEvent
import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent
import io.redspace.ironsspellbooks.api.events.SpellPreCastEvent
import io.redspace.ironsspellbooks.api.spells.SpellData
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.SubscribeEvent
import java.util.*

object IronSpellsCompact {
    private val BLOCKED_PLAYER_INSTANCES = mutableSetOf<BlockedSpellInstance>()

    //TODO
    @SubscribeEvent
    fun onSpellPreCast(event: SpellPreCastEvent) {
        val player = event.entity

        val spellData = SpellData(MainHelper.safeParseResource(event.spellId), event.spellLevel, false)

        if (!this.isSpellBlockedFor(player, spellData, ISSBlockScope.CAST)) return

        event.isCanceled = true

        // Looks like this event will never fire in client.
        // if (!player.level().isClientSide) return

        MainHelper.sendMessageToPlayer(player, DefaultIronMessages.UNABLE_TO_CAST)
    }
    //TODO
    @SubscribeEvent
    fun onSpellCast(event: SpellOnCastEvent) {
        val spellData = SpellData(MainHelper.safeParseResource(event.spellId), event.spellLevel, false)

        this.spellEvaluate(Sources.PLAYER_SPELL_CAST, spellData, event.entity)
    }

    //TODO
    @SubscribeEvent
    fun onSpellInscribe(event: InscribeSpellEvent) {
        val player = event.entity

        if (this.isSpellBlockedFor(player, event.spellData, ISSBlockScope.INSCRIBE)) {
            event.isCanceled = true

            // Looks like this event will never fire in client.
            // if (!player.level().isClientSide) return

            MainHelper.sendMessageToPlayer(player, DefaultIronMessages.UNABLE_TO_INSCRIBE)

            return
        }

        this.spellEvaluate(Sources.PLAYER_SPELL_INSCRIBE, event.spellData, player)
    }

    fun spellEvaluate(
        source: String,
        spellData: SpellData,
        player: Player
    ) = Koko.skillsHandler.skillCheckup(player, source, Objects::equals, spellData.spell.spellId)

    fun addBlockedSpell(uuid: UUID, spellId: ResourceLocation, spellLevel: Int, scope: ISSBlockScope) {
        val blockedItems = this.getBlockedPlayerInstance(uuid).blockedItems
        var blockedItem = blockedItems.find { it.location == spellId }

        if (blockedItem == null) {
            blockedItem = BlockedSpell(spellId, spellLevel, mutableSetOf())

            blockedItems.add(blockedItem)
        }

        blockedItem.scopes.add(scope)
    }

    fun removeBlockedSpell(uuid: UUID, spellLocation: ResourceLocation, scope: ISSBlockScope) {
        val blockedItem = this.getBlockedPlayerInstance(uuid).blockedItems.find { it.location == spellLocation }

        if (blockedItem == null) return

        blockedItem.scopes.removeIf { it == scope }
    }

    fun clearBlockedItemsFor(uuid: UUID, scope: ISSBlockScope) {
        for (blockedItem in this.getBlockedPlayerInstance(uuid).blockedItems) {
            blockedItem.scopes.removeIf { it == scope }
        }
    }

    fun clearAllBlockedItemsFor(uuid: UUID) = this.getBlockedPlayerInstance(uuid).blockedItems.clear()

    fun isSpellBlockedFor(player: Player, spellData: SpellData, scope: ISSBlockScope): Boolean =
        this.isSpellBlockedFor(player.uuid, spellData, scope)

    fun isSpellBlockedFor(uuid: UUID, spellData: SpellData, scope: ISSBlockScope): Boolean {
        val blockedRecipes = this.getBlockedItemsInScope(uuid, scope)

        val isBlocked = blockedRecipes.find { this.isSpellLevelIsRestricted(spellData, it.level) }

        return (isBlocked != null)
    }

    fun isSpellLevelIsRestricted(spellData: SpellData, level: Int): Boolean = (spellData.level > level)

    fun getBlockedItemsInScope(uuid: UUID, scope: ISSBlockScope): Set<BlockedSpell> {
        val blockedItems = BLOCKED_PLAYER_INSTANCES.find { it.uuid == uuid }
            ?.blockedItems
            ?.filter { it.scopes.contains(scope) }
            ?.toSet()

        return blockedItems ?: emptySet()
    }

    fun getBlockedPlayerInstance(player: Player) =
        this.getBlockedPlayerInstance(player.uuid)

    fun getBlockedPlayerInstance(uuid: UUID): BlockedSpellInstance {
        var foundInstance = BLOCKED_PLAYER_INSTANCES.find {
            it.uuid == uuid
        }

        if (foundInstance == null) {
            foundInstance = BlockedSpellInstance(uuid, mutableSetOf())

            BLOCKED_PLAYER_INSTANCES.add(foundInstance)
        }

        return foundInstance
    }

    fun register() {
        AbstractSkillSource.registerCodec(Sources.PLAYER_SPELL_CAST, SpellCastSource.Companion.CODEC)
        AbstractSkillModifier.registerCodec(Effects.PLAYER_SPELL_CAST, SpellCastSkillEffect.Companion.CODEC)
        AbstractSkillModifierFilter
            .registerCodec(Filters.PLAYER_SPELL_CAST_FILTER, SpellCastSkillEffectFilter.Companion.CODEC)

        AbstractSkillSource.registerCodec(Sources.PLAYER_SPELL_INSCRIBE, SpellCastSource.Companion.CODEC)
        AbstractSkillModifier.registerCodec(Effects.PLAYER_SPELL_INSCRIBE, SpellInscribeSkillEffect.Companion.CODEC)
        AbstractSkillModifierFilter
            .registerCodec(Filters.PLAYER_SPELL_INSCRIBE_FILTER, SpellInscribeSkillEffectFilter.CODEC)
    }


    data class BlockedSpellInstance(
        val uuid: UUID,
        val blockedItems: MutableSet<BlockedSpell>,
    ) {
        override fun equals(other: Any?): Boolean {
            return other is BlockedSpellInstance && other.uuid == this.uuid
        }

        override fun hashCode(): Int = uuid.hashCode()
    }

    data class BlockedSpell(val location: ResourceLocation, val level: Int, val scopes: MutableSet<ISSBlockScope>)

    enum class ISSBlockScope { CAST, INSCRIBE }

    object Sources {
        const val PLAYER_SPELL_CAST = "player/ispell_cast"
        const val PLAYER_SPELL_INSCRIBE = "player/ispell_inscribe"
    }

    object Effects {
        const val PLAYER_SPELL_CAST = "player/ispell_precast"
        const val PLAYER_SPELL_INSCRIBE = "player/ispell_inscribe"
    }

    object Filters {
        const val PLAYER_SPELL_CAST_FILTER = "player/ispell_precast_filter"
        const val PLAYER_SPELL_INSCRIBE_FILTER = "player/ispell_inscribe_filter"
    }

    object DefaultIronMessages {
        val UNABLE_TO_CAST = NeoKokoConfig.getMessageConfig(Translatable.MESSAGES_ISS_UNABLE_TO_CAST)
        val UNABLE_TO_INSCRIBE = NeoKokoConfig.getMessageConfig(Translatable.MESSAGES_ISS_UNABLE_TO_INSCRIBE)
    }
}