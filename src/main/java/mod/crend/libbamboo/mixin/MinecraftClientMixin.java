package mod.crend.libbamboo.mixin;

import mod.crend.libbamboo.event.InteractionEvent;
import mod.crend.libbamboo.render.CustomFramebufferRenderer;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Inject(method = "onResolutionChanged", at=@At("TAIL"))
	private void onResolutionChanged(CallbackInfo ci) {
		CustomFramebufferRenderer.resizeFramebuffer();
	}

	@Inject(
			method = "doAttack",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;")
	)
	private void doAttack(CallbackInfoReturnable<Boolean> cir) {
		InteractionEvent.ATTACK.invoker().onAttack(MinecraftClient.getInstance().player, MinecraftClient.getInstance().crosshairTarget);
	}
}
