package mod.crend.libbamboo.event;

import net.minecraft.client.network.ClientPlayerEntity;

public interface ClientEvent<T> {
	T invoker();

	void register(T listener);

	boolean isRegistered();

	interface Tick {
		void tick(ClientPlayerEntity player);
	}
}
