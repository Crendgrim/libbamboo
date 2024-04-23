package mod.crend.libbamboo.event;

import net.minecraft.client.network.ClientPlayerEntity;

public interface GameEvent {
	ClientEvent<Tick> TICK = ClientEventFactory.createArrayBacked();
	ClientEvent<WorldLoad> WORLD_LOAD = ClientEventFactory.createArrayBacked();
	ClientEvent<WorldTick> WORLD_TICK = ClientEventFactory.createArrayBacked();
	ClientEvent<WorldUnload> WORLD_UNLOAD = ClientEventFactory.createArrayBacked();
	ClientEvent<PlayerRespawn> PLAYER_RESPAWN = ClientEventFactory.createArrayBacked();

	interface Tick {
		void tick();
	}
	interface WorldLoad {
		void onWorldLoad(ClientPlayerEntity player);
	}
	interface WorldTick extends ClientEvent.Tick {
	}
	interface WorldUnload {
		void onWorldUnload();
	}
	interface PlayerRespawn {
		void onRespawn(ClientPlayerEntity player);
	}
}
