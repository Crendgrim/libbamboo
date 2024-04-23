package mod.crend.libbamboo.event;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public interface MoveEvent {
	ClientEvent<Moving> MOVING = ClientEventFactory.createArrayBacked();
	ClientEvent<StandingStill> STANDING_STILL = ClientEventFactory.createArrayBacked();

	interface Moving {
		void onMove(ClientPlayerEntity player, Vec3d position);
	}
	interface StandingStill {
		void onStandingStill(ClientPlayerEntity player, Vec3d position);
	}
}
