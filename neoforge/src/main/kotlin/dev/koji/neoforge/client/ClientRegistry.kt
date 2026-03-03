package dev.koji.neoforge.client

import com.mojang.blaze3d.platform.InputConstants
import dev.koji.koko.Koko
import dev.koji.koko.common.content.Translatable
import net.minecraft.client.KeyMapping
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
import org.lwjgl.glfw.GLFW

@EventBusSubscriber(modid = Koko.MOD_ID, value = [Dist.CLIENT])
object ClientRegistry {
    val OPEN_SKILLS = KeyMapping(
        Translatable.KEYS_KOKO_SKILLS,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_C,
        Translatable.KEYS_KOKO_CATEGORY
    )

    @SubscribeEvent
    fun register(event: RegisterKeyMappingsEvent) { event.register(OPEN_SKILLS) }
}