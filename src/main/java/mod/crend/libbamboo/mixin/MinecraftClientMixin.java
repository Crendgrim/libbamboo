package mod.crend.libbamboo.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import mod.crend.libbamboo.event.InteractionEvent;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if <=1.21.5
import mod.crend.libbamboo.render.CustomFramebufferRenderer;

@Mixin(MinecraftClient.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public class MinecraftClientMixin {
	//? if <=1.21.5 {
	@Inject(method = "onResolutionChanged", at=@At("TAIL"))
	private void onResolutionChanged(CallbackInfo ci) {
		CustomFramebufferRenderer.resizeFramebuffer();
	}
	//?}

	@Inject(
			method = "doAttack",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;")
	)
	private void doAttack(CallbackInfoReturnable<Boolean> cir) {
		InteractionEvent.ATTACK.invoker().onAttack(MinecraftClient.getInstance().player, MinecraftClient.getInstance().crosshairTarget);
	}
}
