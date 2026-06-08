package dev.koji.mixins;

import dev.koji.koko.common.content.Paths;
import dev.koji.koko.common.events.EntityEventHandler;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TamableAnimal.class)
public abstract class TamableEntityMixin {
    @Inject(method = "tame", at = @At("TAIL"))
    private void tameMixin(Player player, CallbackInfo ci) {
        TamableAnimal entity = (TamableAnimal) (Object) this;

        EntityEventHandler.INSTANCE.entityEvaluate(player, Paths.DefaultSources.ENTITY_TAME, entity);
    }
}
