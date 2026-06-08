package dev.koji.koko.common.network.payloads

import dev.koji.koko.Koko
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation

class IncomingXpPayload(val skill: ResourceLocation, val xp: Double) : CustomPacketPayload {
    companion object {
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, IncomingXpPayload> = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, IncomingXpPayload::skill,
            ByteBufCodecs.DOUBLE, { it.xp },
            ::IncomingXpPayload
        )

        val ID = Koko.toPath("incoming_xp_payload")
        val TYPE = CustomPacketPayload.Type<IncomingXpPayload>(ID)
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE
}
