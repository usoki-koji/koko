package dev.koji.koko

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler
import dev.isxander.yacl3.config.v2.api.SerialEntry
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen
import dev.isxander.yacl3.config.v2.api.autogen.StringField
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder
import dev.koji.koko.common.content.Translatable.MESSAGES_CURIOS_UNABLE_TO_WEAR
import dev.koji.koko.common.content.Translatable.MESSAGES_ISS_UNABLE_TO_CAST
import dev.koji.koko.common.content.Translatable.MESSAGES_ISS_UNABLE_TO_INSCRIBE
import dev.koji.koko.common.content.Translatable.MESSAGES_UNABLE_TO_ARMOR
import dev.koji.koko.common.content.Translatable.MESSAGES_UNABLE_TO_ATTACK
import dev.koji.koko.common.content.Translatable.MESSAGES_UNABLE_TO_CONSUME
import dev.koji.koko.common.content.Translatable.MESSAGES_UNABLE_TO_CRAFT
import dev.koji.koko.common.content.Translatable.MESSAGES_UNABLE_TO_FORGE
import dev.koji.koko.common.content.Translatable.MESSAGES_UNABLE_TO_USE
import java.nio.file.Path

class KokoConfig {
    @SerialEntry @AutoGen(category = MESSAGES_CATEGORY) @StringField
    var unableToUseMessage = MESSAGES_UNABLE_TO_USE

    @SerialEntry @AutoGen(category = MESSAGES_CATEGORY) @StringField
    var unableToAttackMessage = MESSAGES_UNABLE_TO_ATTACK

    @SerialEntry @AutoGen(category = MESSAGES_CATEGORY) @StringField
    var unableToConsumeMessage = MESSAGES_UNABLE_TO_CONSUME

    @SerialEntry @AutoGen(category = MESSAGES_CATEGORY) @StringField
    var unableToCraftMessage = MESSAGES_UNABLE_TO_CRAFT

    @SerialEntry @AutoGen(category = MESSAGES_CATEGORY) @StringField
    var unableToForgeMessage = MESSAGES_UNABLE_TO_FORGE

    @SerialEntry @AutoGen(category = MESSAGES_CATEGORY) @StringField
    var unableToArmorMessage = MESSAGES_UNABLE_TO_ARMOR

    @SerialEntry @AutoGen(category = MESSAGES_CATEGORY) @StringField

    var curiosUnableToWear = MESSAGES_CURIOS_UNABLE_TO_WEAR
    @SerialEntry @AutoGen(category = MESSAGES_CATEGORY) @StringField
    var issUnableToCast = MESSAGES_ISS_UNABLE_TO_CAST

    @SerialEntry @AutoGen(category = MESSAGES_CATEGORY) @StringField
    var issUnableToInscribe = MESSAGES_ISS_UNABLE_TO_INSCRIBE

    fun init() { HANDLER.load() }

    companion object {
        const val MESSAGES_CATEGORY: String = "messages"

        val HANDLER: ConfigClassHandler<KokoConfig> = ConfigClassHandler.createBuilder(KokoConfig::class.java)
            .id(Koko.toPath(Koko.MOD_ID))
            .serializer { config ->
                GsonConfigSerializerBuilder.create<KokoConfig>(config)
                    .setPath(Path.of("config", Koko.MOD_ID + ".json"))
                    .build()
            }
            .build()
    }
}