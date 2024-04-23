package mod.crend.libbamboo.event;

public interface StatusEvent {
	ClientEvent<Health> HEALTH = ClientEventFactory.createArrayBacked();
	ClientEvent<Food> FOOD = ClientEventFactory.createArrayBacked();
	ClientEvent<Armor> ARMOR = ClientEventFactory.createArrayBacked();
	ClientEvent<Air> AIR = ClientEventFactory.createArrayBacked();
	ClientEvent<Experience> EXPERIENCE = ClientEventFactory.createArrayBacked();

	interface Health extends ChangeEvent { }
	interface Food extends ChangeEvent { }
	interface Armor extends ChangeEvent { }
	interface Air extends ChangeEvent { }
	interface Experience {
		void onChange(float progress, int total, int level);
	}

	interface ChangeEvent {
		void onChange(float value, float previous, float max);
	}
}
