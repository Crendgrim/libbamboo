package mod.crend.libbamboo.event;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.hit.HitResult;

public interface InteractionEvent {
	ClientEvent<Attack> ATTACK = ClientEventFactory.createArrayBacked();
	ClientEvent<MiningTick> MINING_TICK = ClientEventFactory.createArrayBacked();
	ClientEvent<UsingItemTick> USING_ITEM_TICK = ClientEventFactory.createArrayBacked();

	interface Attack {
		void onAttack(ClientPlayerEntity player, HitResult hitResult);
	}
	interface MiningTick extends ClientEvent.Tick { }
	interface UsingItemTick extends ClientEvent.Tick { }
}
