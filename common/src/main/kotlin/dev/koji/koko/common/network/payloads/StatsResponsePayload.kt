package dev.koji.koko.common.network.payloads

import dev.koji.koko.Koko
import dev.koji.koko.common.models.SkillData
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation

class StatsResponsePayload(val skillData: Map<ResourceLocation, SkillData>) : CustomPacketPayload {
    companion object {
        private val MAP_CODEC = ByteBufCodecs.map(
            { _ -> mutableMapOf<ResourceLocation, SkillData>() },
            ResourceLocation.STREAM_CODEC, SkillData.STREAM_CODEC
        )

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, StatsResponsePayload> =
            MAP_CODEC.map(::StatsResponsePayload) { it.skillData.toMutableMap() }

        val ID = Koko.toPath("stats_response_payload")
        val TYPE = CustomPacketPayload.Type<StatsResponsePayload>(ID)
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE
}
