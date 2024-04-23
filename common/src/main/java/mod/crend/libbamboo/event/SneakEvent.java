package mod.crend.libbamboo.event;

import net.minecraft.client.network.ClientPlayerEntity;

public interface SneakEvent {
	ClientEvent<Start> START = ClientEventFactory.createArrayBacked();
	ClientEvent<Tick> TICK = ClientEventFactory.createArrayBacked();
	ClientEvent<Stop> STOP = ClientEventFactory.createArrayBacked();

	interface Start {
		void onStartSneaking(ClientPlayerEntity player);
	}
	interface Tick extends ClientEvent.Tick { }
	interface Stop {
		void onStopSneaking(ClientPlayerEntity player);
	}
}
