package mod.crend.libbamboo.event;

import net.minecraft.client.gui.screen.ingame.HandledScreen;

public interface ScreenEvent {
	ClientEvent<Chat> CHAT = ClientEventFactory.createArrayBacked();
	ClientEvent<Open> OPEN = ClientEventFactory.createArrayBacked();
	ClientEvent<Tick> TICK = ClientEventFactory.createArrayBacked();
	ClientEvent<Close> CLOSE = ClientEventFactory.createArrayBacked();
	ClientEvent<Pause> PAUSE = ClientEventFactory.createArrayBacked();
	ClientEvent<PauseTick> PAUSE_TICK = ClientEventFactory.createArrayBacked();
	ClientEvent<Unpause> UNPAUSE = ClientEventFactory.createArrayBacked();

	interface Chat {
		void onOpenChat();
	}
	interface Open {
		void onOpenScreen(HandledScreen<?> screen);
	}
	interface Tick {
		void tick(HandledScreen<?> screen);
	}
	interface Close {
		void onCloseScreen();
	}
	interface Pause {
		void onOpenPauseScreen();
	}
	interface PauseTick {
		void tick();
	}
	interface Unpause {
		void onClosePauseScreen();
	}
}
