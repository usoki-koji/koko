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
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, StatsRequestPayload> =
            StreamCodec.of(
                { _, _ -> },
                { StatsRequestPayload() }
            )

        val ID = Koko.toPath("stats_request_payload")
        val TYPE = CustomPacketPayload.Type<StatsRequestPayload>(ID)
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE
}