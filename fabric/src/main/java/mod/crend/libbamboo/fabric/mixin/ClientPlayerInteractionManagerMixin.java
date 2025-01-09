package mod.crend.libbamboo.fabric.mixin;

import mod.crend.libbamboo.event.HotbarEvent;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
//? if <1.20.6 {
    @Inject(method="syncSelectedSlot", at=@At(value = "INVOKE", target="Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"), require = 0)
//?} else {
    /*@Inject(method="syncSelectedSlot", at=@At(value = "INVOKE", target="Lnet/minecraft/client/network/ClientCommonNetworkHandler;send(Lnet/minecraft/network/packet/Packet;)V"), require = 0)
*///?}
    private void revealOnSlotChangeNeo(CallbackInfo ci) {
        HotbarEvent.SELECTED_SLOT_CHANGE.invoker().onSelectedSlotChange();
    }
}
