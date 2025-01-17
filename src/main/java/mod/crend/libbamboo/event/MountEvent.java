package mod.crend.libbamboo.event;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public interface MountEvent {
	ClientEvent<MountHealth> MOUNT_HEALTH_CHANGE = ClientEventFactory.createArrayBacked();
	ClientEvent<MountJump> MOUNT_JUMP = ClientEventFactory.createArrayBacked();
	ClientEvent<RidingStart> START = ClientEventFactory.createArrayBacked();
	ClientEvent<RidingTick> TICK = ClientEventFactory.createArrayBacked();
	ClientEvent<RidingStop> STOP = ClientEventFactory.createArrayBacked();

	interface RidingStart {
		void onStartRiding(ClientPlayerEntity player, LivingEntity vehicle);
	}
	interface RidingTick {
		void tick(ClientPlayerEntity player, LivingEntity vehicle);
	}
	interface RidingStop {
		void onStopRiding(ClientPlayerEntity player);
	}
	interface MountHealth extends StatusEvent.ChangeEvent { }

	interface MountJump {
		void onJump(Entity vehicle);
	}
}
