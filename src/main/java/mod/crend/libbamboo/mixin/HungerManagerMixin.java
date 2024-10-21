package mod.crend.libbamboo.mixin;

import mod.crend.libbamboo.event.StatusEvent;
import net.minecraft.entity.player.HungerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HungerManager.class)
public class HungerManagerMixin {
    @Shadow private int foodLevel;

    @Inject(method="setFoodLevel", at=@At("HEAD"))
    private void setFoodLevel(int foodLevel, CallbackInfo ci) {
        StatusEvent.FOOD.invoker().onChange(foodLevel, this.foodLevel, 20);
    }
}
