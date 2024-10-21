package mod.crend.libbamboo.mixin;

import mod.crend.libbamboo.event.InteractionEvent;
import mod.crend.libbamboo.render.CustomFramebufferRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

	@Shadow
	public ClientPlayerEntity player;

	@Shadow
	public HitResult crosshairTarget;

	@Inject(
			method = "doAttack",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;", shift = At.Shift.BEFORE)
	)
	private void doAttack(CallbackInfoReturnable<Boolean> cir) {
		InteractionEvent.ATTACK.invoker().onAttack(player, crosshairTarget);
	}
}
