package dev.koji.koko.common.models.modifiers

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import dev.koji.koko.common.content.Paths
import dev.koji.koko.common.models.modifiers.filters.AboveSkillModifierFilter
import dev.koji.koko.common.models.modifiers.filters.BellowSkillModifierFilter
import dev.koji.koko.common.models.modifiers.filters.BlockedSkillModifierFilter
import dev.koji.koko.common.models.modifiers.filters.RangeSkillModifierFilter

abstract class AbstractSkillModifierFilter {
    abstract val type: String

    abstract fun apply(level: Int): Boolean

    companion object {
        val CODEC: Codec<AbstractSkillModifierFilter> = Codec.STRING.dispatch(
            { source -> source.type },
            { type -> codecMapper[type] ?: throw IllegalArgumentException("$type is not supported") }
        )

        val codecMapper = mutableMapOf<String, MapCodec<out AbstractSkillModifierFilter>>(
            Paths.DefaultFilters.ABOVE to AboveSkillModifierFilter.CODEC,
            Paths.DefaultFilters.RANGE to RangeSkillModifierFilter.CODEC,
            Paths.DefaultFilters.BELLOW to BellowSkillModifierFilter.CODEC,
            Paths.DefaultFilters.BLOCKED to BlockedSkillModifierFilter.CODEC
        )

        fun registerCodec(path: String, codec: MapCodec<out AbstractSkillModifierFilter>) { codecMapper[path] = codec }
    }
}