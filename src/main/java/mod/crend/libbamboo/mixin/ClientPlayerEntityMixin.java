package mod.crend.libbamboo.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import mod.crend.libbamboo.event.MountEvent;
import mod.crend.libbamboo.event.StatusEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if <=1.21.4 {
import net.minecraft.entity.Entity;
//?} else
/*import net.minecraft.entity.JumpingMount;*/

@Mixin(ClientPlayerEntity.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
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

    @WrapOperation(
            method = "tickMovement",
            at = @At(
                    value = "INVOKE",
                    //? if <=1.21.4 {
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getVehicle()Lnet/minecraft/entity/Entity;"
                    //?} else
                    /*target = "Lnet/minecraft/client/network/ClientPlayerEntity;getJumpingMount()Lnet/minecraft/entity/JumpingMount;"*/
            )
    )
    private /*? if <=1.21.4 {*/Entity/*?} else {*//*JumpingMount*//*?}*/ jumpBarChanged(ClientPlayerEntity instance, Operation</*? if <=1.21.4 {*/Entity/*?} else {*//*JumpingMount*//*?}*/> original) {
        var vehicle = original.call(instance);
        if (MinecraftClient.getInstance().player != null
                && MinecraftClient.getInstance().player.input./*? if <1.21.2 {*/jumping/*?} else {*//*playerInput.jump()*//*?}*/
        ) {
            MountEvent.MOUNT_JUMP.invoker().onJump(vehicle);
        }
        return vehicle;
    }
}
