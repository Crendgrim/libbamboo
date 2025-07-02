//? if =1.21.5 {
/*package mod.crend.libbamboo.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DrawContext.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public interface DrawContextAccessor {
	@Accessor
	VertexConsumerProvider.Immediate getVertexConsumers();
}
*///?}
