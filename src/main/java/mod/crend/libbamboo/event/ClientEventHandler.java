package mod.crend.libbamboo.event;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ClientEventHandler {
	static final int TICKS_UNTIL_STANDING_STILL = 5;

	static class GameState {
		ItemStack previousMainHandStack;
		ItemStack previousOffHandStack;
		HitResult previousHitResult;
		Vec3d previousPosition;
		int ticksUntilStandingStill = 0;
		float previousVehicleHealth = 0;
		float previousArmor = 0;
		float previousAir = 0;
		float previousFood = 0;
		boolean wasInPauseScreen = false;
		boolean screenWasOpen = false;
		boolean wasSneaking = false;
		boolean wasFlying = false;
		boolean wasRiding = false;

		public GameState(ClientPlayerEntity player) {
			previousMainHandStack = player.getMainHandStack().copy();
			previousOffHandStack = player.getOffHandStack().copy();
			previousHitResult = MinecraftClient.getInstance().crosshairTarget;
			previousPosition = player.getPos();
		}

		public void tick(ClientPlayerEntity player) {
			if (HotbarEvent.MAIN_HAND_CHANGE.isRegistered()) {
				ItemStack mainHandStack = player.getMainHandStack();
				if (!ItemStack.areEqual(mainHandStack, previousMainHandStack)) {
					previousMainHandStack = mainHandStack.copy();
					HotbarEvent.MAIN_HAND_CHANGE.invoker().onChange(mainHandStack);
				}
			}
			if (HotbarEvent.OFF_HAND_CHANGE.isRegistered()) {
				ItemStack offHandStack = player.getOffHandStack();
				if (!ItemStack.areEqual(offHandStack, previousOffHandStack)) {
					previousOffHandStack = offHandStack.copy();
					HotbarEvent.OFF_HAND_CHANGE.invoker().onChange(offHandStack);
				}
			}

			HitResult hitResult = MinecraftClient.getInstance().crosshairTarget;
			if (hitResult != null && !hitResult.equals(previousHitResult)) {
				switch (hitResult.getType()) {
					case MISS -> {
						if (previousHitResult == null || previousHitResult.getType() != HitResult.Type.MISS) {
							TargetEvent.NO_TARGET_TRIGGER.invoker().trigger();
						}
						TargetEvent.NO_TARGET_TICK.invoker().trigger();
					}
					case BLOCK -> {
						if (TargetEvent.TARGETED_BLOCK_TICK.isRegistered() || TargetEvent.TARGETED_BLOCK_CHANGED.isRegistered()) {
							BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
							BlockState blockState = player.clientWorld.getBlockState(blockPos);
							if (TargetEvent.TARGETED_BLOCK_CHANGED.isRegistered()) {
								if (previousHitResult == null || previousHitResult.getType() != HitResult.Type.BLOCK) {
									TargetEvent.TARGETED_BLOCK_CHANGED.invoker().target(blockPos, blockState);
								} else {
									BlockPos previousBlockPos = ((BlockHitResult) previousHitResult).getBlockPos();
									BlockState previousBlockState = player.clientWorld.getBlockState(previousBlockPos);
									if (!blockPos.equals(previousBlockPos) || !blockState.equals(previousBlockState)) {
										TargetEvent.TARGETED_BLOCK_CHANGED.invoker().target(blockPos, blockState);
									}
								}
							}
							TargetEvent.TARGETED_BLOCK_TICK.invoker().target(blockPos, blockState);
						}
					}
					case ENTITY -> {
						Entity entity = ((EntityHitResult) hitResult).getEntity();
						if (TargetEvent.TARGETED_ENTITY_CHANGED.isRegistered()) {
							if (previousHitResult == null || previousHitResult.getType() != HitResult.Type.ENTITY
									|| !entity.equals(((EntityHitResult) previousHitResult).getEntity())) {
								TargetEvent.TARGETED_ENTITY_CHANGED.invoker().target(entity);
							}
						}
						TargetEvent.TARGETED_ENTITY_TICK.invoker().target(entity);
					}
				}
				previousHitResult = hitResult;
			}

			if (player.isSneaking()) {
				if (!wasSneaking) {
					SneakEvent.START.invoker().onStartSneaking(player);
				}
				SneakEvent.TICK.invoker().tick(player);
				wasSneaking = true;
			} else if (wasSneaking) {
				SneakEvent.STOP.invoker().onStopSneaking(player);
				wasSneaking = false;
			}

			if (player./*? if <1.21.2 {*/isFallFlying/*?} else {*//*isGliding*//*?}*/()) {
				if (!wasFlying) {
					FlyEvent.START.invoker().onStartFlying(player);
				}
				FlyEvent.TICK.invoker().tick(player);
				wasFlying = true;
			} else if (wasFlying) {
				FlyEvent.STOP.invoker().onStopFlying(player);
				wasFlying = false;
			}

			if (player.isUsingItem()) {
				InteractionEvent.USING_ITEM_TICK.invoker().tick(player);
			}

			if (MinecraftClient.getInstance().interactionManager.getBlockBreakingProgress() >= 0) {
				InteractionEvent.MINING_TICK.invoker().tick(player);
			}

			if (MoveEvent.MOVING.isRegistered() || MoveEvent.STANDING_STILL.isRegistered()) {
				Vec3d position = player.getPos();
				if (!position.equals(previousPosition)) {
					MoveEvent.MOVING.invoker().onMove(player, position);
					previousPosition = position;
					ticksUntilStandingStill = TICKS_UNTIL_STANDING_STILL;
				} else if (ticksUntilStandingStill == 0) {
					MoveEvent.STANDING_STILL.invoker().onStandingStill(player, position);
				} else {
					// Smoothing: call the move event on the first tick that the position didn't change
					if (ticksUntilStandingStill == TICKS_UNTIL_STANDING_STILL) {
						MoveEvent.MOVING.invoker().onMove(player, position);
					}
					--ticksUntilStandingStill;
				}
			}

			if (player.getAir() != previousAir) {
				StatusEvent.AIR.invoker().onChange(player.getAir(), previousAir, player.getMaxAir());
				previousAir = player.getAir();
			}
			if (player.getArmor() != previousArmor) {
				StatusEvent.ARMOR.invoker().onChange(player.getArmor(), previousArmor, 30);
				previousArmor = player.getArmor();
			}
			if (player.getHungerManager().getFoodLevel() != previousFood) {
				StatusEvent.FOOD.invoker().onChange(player.getHungerManager().getFoodLevel(), previousFood, 20);
				previousFood = player.getHungerManager().getFoodLevel();
			}

			if (player.getVehicle() instanceof LivingEntity vehicle) {
				if (!wasRiding) {
					MountEvent.START.invoker().onStartRiding(player, vehicle);
				}
				MountEvent.TICK.invoker().tick(player, vehicle);
				if (vehicle.getHealth() != previousVehicleHealth) {
					MountEvent.MOUNT_HEALTH_CHANGE.invoker().onChange(vehicle.getHealth(), previousVehicleHealth, vehicle.getMaxHealth());
				}
				wasRiding = true;
			} else if (wasRiding) {
				MountEvent.STOP.invoker().onStopRiding(player);
				wasRiding = false;
				previousVehicleHealth = 0;
			}

			Screen currentScreen = MinecraftClient.getInstance().currentScreen;
			if (currentScreen instanceof ChatScreen) {
				ScreenEvent.CHAT.invoker().onOpenChat();
			}

			if (currentScreen instanceof HandledScreen<?> handledScreen) {
				ScreenEvent.TICK.invoker().tick(handledScreen);
				if (!screenWasOpen) {
					ScreenEvent.OPEN.invoker().onOpenScreen(handledScreen);
					screenWasOpen = true;
				}
			} else if (screenWasOpen) {
				ScreenEvent.CLOSE.invoker().onCloseScreen();
				screenWasOpen = false;
			}

			if (currentScreen instanceof GameMenuScreen) {
				if (!wasInPauseScreen) ScreenEvent.PAUSE.invoker().onOpenPauseScreen();
				ScreenEvent.PAUSE_TICK.invoker().tick();
				wasInPauseScreen = true;
			} else if (wasInPauseScreen) {
				ScreenEvent.UNPAUSE.invoker().onClosePauseScreen();
			}
		}
	}

	boolean wasWorldLoaded = false;
	GameState state = null;

	public ClientEventHandler() {
		GameEvent.TICK.register(this::tick);
		GameEvent.WORLD_LOAD.register(this::init);
		GameEvent.PLAYER_RESPAWN.register(this::init);
	}

	public void init(ClientPlayerEntity player) {
		state = new GameState(player);
	}

	public void tick() {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if (player != null && MinecraftClient.getInstance().world != null) {
			if (!wasWorldLoaded) {
				GameEvent.WORLD_LOAD.invoker().onWorldLoad(player);
			}
			wasWorldLoaded = true;
			GameEvent.WORLD_TICK.invoker().tick(player);
			state.tick(player);
		} else {
			wasWorldLoaded = false;
			state = null;
			GameEvent.WORLD_UNLOAD.invoker().onWorldUnload();
		}
	}

}
