package dev.koji.koko.common.models.modifiers.player

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.koji.koko.Koko
import dev.koji.koko.common.SkillsHandler
import dev.koji.koko.common.content.Paths
import dev.koji.koko.common.helpers.MainHelper
import dev.koji.koko.common.models.modifiers.AbstractSkillModifier
import dev.koji.koko.common.models.modifiers.AbstractSkillModifierFilter
import dev.koji.koko.common.models.modifiers.filters.AboveSkillModifierFilter
import dev.koji.koko.common.models.modifiers.filters.BellowSkillModifierFilter
import dev.koji.koko.common.models.modifiers.filters.RangeSkillModifierFilter
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.player.Player

class AttributeSkillModifier(
    val target: String,
    val filters: List<AbstractSkillModifierFilter>
) : AbstractSkillModifier() {
    override val type: String = Paths.DefaultModifiers.PLAYER_ATTRIBUTE

    override fun doAnyApplies(level: Int): AbstractSkillModifierFilter? {
        val applicableFilters = filters.filter { it.apply(level) }

        return when (applicableFilters.size) {
            0 -> null
            1 -> applicableFilters.first()
            else -> {
                Koko.LOGGER.warn("Filter overlapping is not supported.")

                applicableFilters.first()
            }
        }
    }

    override fun apply(applier: SkillsHandler.SkillModifierApplier, player: Player) =
        this.handleAttribute(applier, player, true)

    override fun unApply(applier: SkillsHandler.SkillModifierApplier, player: Player) =
        this.handleAttribute(applier, player, false)

    private fun handleAttribute(
        applier: SkillsHandler.SkillModifierApplier,
        player: Player,
        add: Boolean
    ) {
        val attributes = player.attributes

        val attributeLocation = MainHelper.safeParseResource(target)

        val attributeHolder = BuiltInRegistries.ATTRIBUTE.getHolder(
            ResourceKey.create(Registries.ATTRIBUTE, attributeLocation)
        )

        if (attributeHolder.isEmpty) return Koko.LOGGER.warn("Unable to find holder for $target")

        val attributeInstance = attributes.getInstance(attributeHolder.get())
            ?: return Koko.LOGGER.warn("Unable to find instance for $target")

        val modifier = when(val filter = applier.filter) {
            is AboveSkillModifierFilter -> AttributeModifier(
                Koko.toPath("attribute_${attributeLocation.path}"), filter.value, filter.operation
            )

            is RangeSkillModifierFilter -> AttributeModifier(
                Koko.toPath("attribute_${attributeLocation.path}"), filter.value, filter.operation
            )

            is BellowSkillModifierFilter -> AttributeModifier(
                Koko.toPath("attribute_${attributeLocation.path}"), filter.value, filter.operation
            )

            else -> return Koko.LOGGER.warn("Filter ${filter::class.simpleName} is not supported.")
        }

        if (add)
            attributeInstance.addOrUpdateTransientModifier(modifier)
        else
            attributeInstance.removeModifier(modifier)
    }
    companion object {
        val CODEC: MapCodec<AttributeSkillModifier> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.STRING.fieldOf("attribute").forGetter(AttributeSkillModifier::target),
                AbstractSkillModifierFilter.CODEC.listOf().fieldOf("filters").forGetter(AttributeSkillModifier::filters)
            ).apply(instance, ::AttributeSkillModifier)
        }
    }
}