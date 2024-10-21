package mod.crend.libbamboo.event;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public interface TargetEvent {
	ClientEvent<TargetEntity> TARGETED_ENTITY_TICK = ClientEventFactory.createArrayBacked();
	ClientEvent<TargetEntity> TARGETED_ENTITY_CHANGED = ClientEventFactory.createArrayBacked();
	ClientEvent<TargetBlock> TARGETED_BLOCK_TICK = ClientEventFactory.createArrayBacked();
	ClientEvent<TargetBlock> TARGETED_BLOCK_CHANGED = ClientEventFactory.createArrayBacked();
	ClientEvent<NoTarget> NO_TARGET_TICK = ClientEventFactory.createArrayBacked();
	ClientEvent<NoTarget> NO_TARGET_TRIGGER = ClientEventFactory.createArrayBacked();

	interface TargetEntity {
		void target(Entity entity);
	}
	interface TargetBlock {
		void target(BlockPos blockPos, BlockState blockState);
	}
	interface NoTarget {
		void trigger();
	}
}
