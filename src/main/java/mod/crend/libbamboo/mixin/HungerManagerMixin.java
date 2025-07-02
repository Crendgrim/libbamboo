package mod.crend.libbamboo.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import mod.crend.libbamboo.event.StatusEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.HungerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HungerManager.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public class HungerManagerMixin {
    @Inject(method="setFoodLevel", at=@At("HEAD"))
    private void setFoodLevel(int foodLevel, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player != null) {
            StatusEvent.FOOD.invoker().onChange(foodLevel, MinecraftClient.getInstance().player.getHungerManager().getFoodLevel(), 20);
        }
    }
}
