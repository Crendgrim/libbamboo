package mod.crend.libbamboo.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public interface MinecraftClientAccessor {
	@Final
	@Mutable
	@Accessor
	void setFramebuffer(Framebuffer framebuffer);
}
