package me.horeatise;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;

public class RotateCameraClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		// Register the /rotatecamera command
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(buildCommand()));
	}


	private LiteralArgumentBuilder<FabricClientCommandSource> buildCommand() {
		return ClientCommandManager.literal("rotatecamera")
				.then(ClientCommandManager.argument("yaw", FloatArgumentType.floatArg(-180, 180))
						.then(ClientCommandManager.argument("pitch", FloatArgumentType.floatArg(-90, 90))
								.executes(ctx -> {
									float yaw = FloatArgumentType.getFloat(ctx, "yaw");
									float pitch = FloatArgumentType.getFloat(ctx, "pitch");
									// Call the method to rotate the player's camera smoothly here
									rotateCamera(yaw, pitch);
									return 1;
								})));
	}

	private void rotateCamera(float yaw, float pitch) {
		Entity camera = MinecraftClient.getInstance().getCameraEntity();

		// Calculate the rotation difference and divide it into steps for smooth transition
		float yawDiff = yaw - camera.getYaw();
		float pitchDiff = pitch - camera.getPitch();
		int steps = 20; // Number of steps for the transition



		float yawStep = yawDiff / steps;
		float pitchStep = pitchDiff / steps;

		// Gradually update the camera's rotation angles
		for (int i = 0; i < steps; i++) {
			float newYaw = camera.getYaw() + yawStep;
			float newPitch = camera.getPitch() + pitchStep;
			System.out.println(newYaw + " " + newPitch);

			camera.setYaw(newYaw);
			camera.setPitch(newPitch);

			/*
			In the context of the provided code, the line
			MinecraftClient.getInstance().getEntityRenderDispatcher().rotationChanged(entity);
			is being used to inform the game's rendering system that the player's (or entity's) rotation is being
			smoothly adjusted. This is necessary to ensure that the rendered visuals of the player accurately
			reflect the gradual rotation changes being made in the mod. Without this call, the rendered
			representation of the player might not update in real-time as the rotation changes.
			 */

			//MinecraftClient.getInstance().getEntityRenderDispatcher().shouldRenderHitboxes();
			try {
				Thread.sleep(20); // Sleep for a short duration between steps
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

