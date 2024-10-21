package mod.crend.libbamboo.event;

import net.minecraft.item.ItemStack;

public interface HotbarEvent {
	ClientEvent<MainHand> MAIN_HAND_CHANGE = ClientEventFactory.createArrayBacked();
	ClientEvent<OffHand> OFF_HAND_CHANGE = ClientEventFactory.createArrayBacked();
	ClientEvent<SelectedSlot> SELECTED_SLOT_CHANGE = ClientEventFactory.createArrayBacked();

	interface StackChangeEvent {
		void onChange(ItemStack itemStack);
	}

	interface MainHand extends StackChangeEvent { }

	interface OffHand extends StackChangeEvent { }

	interface SelectedSlot {
		void onSelectedSlotChange();
	}
}
