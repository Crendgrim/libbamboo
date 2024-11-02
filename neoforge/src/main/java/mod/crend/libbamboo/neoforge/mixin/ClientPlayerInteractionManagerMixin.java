package mod.crend.libbamboo.neoforge.mixin;

import mod.crend.libbamboo.event.HotbarEvent;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Inject(method="syncSelectedSlot", at=@At(value = "INVOKE", target="Lnet/minecraft/client/network/ClientPlayNetworkHandler;send(Lnet/minecraft/network/packet/Packet;)V"), require = 0)
    private void revealOnSlotChange(CallbackInfo ci) {
        HotbarEvent.SELECTED_SLOT_CHANGE.invoker().onSelectedSlotChange();
    }
}
