package dev.koji.koko.common.models

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import org.jetbrains.annotations.ApiStatus

data class SkillData(val xp: Double, val isUnlocked: Boolean) {
    companion object {
        val CODEC: Codec<SkillData> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.DOUBLE.fieldOf("xp").forGetter(SkillData::xp),
                Codec.BOOL.fieldOf("unlocked").forGetter(SkillData::isUnlocked)
            ).apply(instance, ::SkillData)
        }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SkillData> = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, SkillData::xp,
            ByteBufCodecs.BOOL, SkillData::isUnlocked,
            ::SkillData
        )
    }
}
