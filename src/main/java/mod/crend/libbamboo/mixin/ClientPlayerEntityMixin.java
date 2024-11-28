package mod.crend.libbamboo.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import mod.crend.libbamboo.event.MountEvent;
import mod.crend.libbamboo.event.StatusEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "updateHealth", at = @At("HEAD"))
    private void updateHealth(float health, CallbackInfo ci) {
        StatusEvent.HEALTH.invoker().onChange(health, getHealth(), getMaxHealth());
    }
    @Inject(method = "setExperience", at = @At("TAIL"))
    private void setExperience(float progress, int total, int level, CallbackInfo ci) {
        StatusEvent.EXPERIENCE.invoker().onChange(progress, total, level);
    }


    // Jumpbar

    @WrapOperation(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getVehicle()Lnet/minecraft/entity/Entity;"))
    private Entity jumpBarChanged(ClientPlayerEntity instance, Operation<Entity> original) {
        Entity vehicle = original.call(instance);
        if (MinecraftClient.getInstance().player != null
                && MinecraftClient.getInstance().player.input./*? if <1.21.2 {*/jumping/*?} else {*//*playerInput.jump()*//*?}*/
        ) {
            MountEvent.MOUNT_JUMP.invoker().onJump(vehicle);
        }
        return vehicle;
    }
}
