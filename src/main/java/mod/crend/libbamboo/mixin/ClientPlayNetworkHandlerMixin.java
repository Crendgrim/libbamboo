package mod.crend.libbamboo.mixin;

import mod.crend.libbamboo.event.GameEvent;
import net.minecraft.client.MinecraftClient;
//? if <1.20.6 {
//?} else {
/*import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.network.ClientConnection;
*///?}
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin /*? if >=1.20.6 {*//*extends ClientCommonNetworkHandler*//*?}*/ {
//? if <1.20.6 {
    @Shadow @Final private MinecraftClient client;
//?} else {
  /*public ClientPlayNetworkHandlerMixin(MinecraftClient client, ClientConnection clientConnection, ClientConnectionState clientConnectionState) {
        super(client, clientConnection, clientConnectionState);
    }
*///?}

    @Inject(method="onPlayerRespawn", at=@At("TAIL"))
    private void onPlayerRespawn(PlayerRespawnS2CPacket packet, CallbackInfo ci) {
        GameEvent.PLAYER_RESPAWN.invoker().onRespawn(this.client.player);
    }
}
