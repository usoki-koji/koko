package dev.koji.koko.common.network.payloads

import dev.koji.koko.Koko
import dev.koji.koko.common.models.SkillData
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation

class StatsRequestPayload : CustomPacketPayload {
    companion object {
        private val MAP_CODEC = ByteBufCodecs.map(
            { _ -> mapOf<ResourceLocation, SkillData>() },
            ResourceLocation.STREAM_CODEC,
            SkillData.STREAM_CODEC
        )

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, StatsResponsePayload> =
            MAP_CODEC.map(::StatsResponsePayload) { it.skillData }

        val TYPE = CustomPacketPayload.Type<StatsResponsePayload>(Koko.toPath("skills_payload"))
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE
}