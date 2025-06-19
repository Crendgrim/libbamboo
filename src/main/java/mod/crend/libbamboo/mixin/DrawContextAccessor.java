package mod.crend.libbamboo.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DrawContext.class)
public interface DrawContextAccessor {
	/*? if =1.21.5 {*/
	/*@Accessor
	VertexConsumerProvider.Immediate getVertexConsumers();
	*//*?}*/
}
