package mod.crend.libbamboo.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.Window;
import org.joml.Matrix4f;

//? if >=1.21.2 {
/*import mod.crend.libbamboo.mixin.MinecraftClientAccessor;
import mod.crend.libbamboo.mixin.DrawContextAccessor;
*///?}
//? if >=1.21.2 && <=1.21.4
/*import net.minecraft.client.gl.ShaderProgramKeys;*/
//? if <=1.21.4 {
import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.opengl.GL30;
//?} else {
/*import com.mojang.blaze3d.textures.GpuTexture;
import mod.crend.libbamboo.LibBamboo;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;
*///?}

public class CustomFramebufferRenderer {
	static Framebuffer framebuffer = null;
	//? if <1.21.2 {
	static int previousFramebuffer;
	//?} else
	/*static Framebuffer builtinFramebuffer;*/
	//? if >1.21.4 {
	/*static Identifier framebufferTextureId = Identifier.of(LibBamboo.MOD_ID, "custom_framebuffer");
	static AbstractTexture framebufferTexture = new AbstractTexture() {
		@Override
		public GpuTexture getGlTexture() {
			if (framebuffer == null || framebuffer.getColorAttachment() == null) {
				throw new IllegalStateException("Color attachment is null!");
			}
			return framebuffer.getColorAttachment();
		}

		@Override
		public void setFilter(boolean bilinear, boolean mipmap) {
		}

		@Override
		public void close() {
		}
	};
	*///?}

	public static void init() {
		// Setup extra framebuffer to draw into
		//? if <1.21.2
		previousFramebuffer = GlStateManager.getBoundFramebuffer();
		if (framebuffer == null) {
			Window window = MinecraftClient.getInstance().getWindow();
			//? if >1.21.4
			/*MinecraftClient.getInstance().getTextureManager().registerTexture(framebufferTextureId, framebufferTexture);*/
			framebuffer = new SimpleFramebuffer(
					/*? if >1.21.4 {*//*framebufferTextureId.toString(),*//*?}*/
					window.getFramebufferWidth(),
					window.getFramebufferHeight(),
					true
					/*? if <1.21.2 {*/, MinecraftClient.IS_SYSTEM_MAC/*?}*/
			);
			//? if <=1.21.4
			framebuffer.setClearColor(0, 0, 0, 0);
			//? if >=1.21.2
			/*builtinFramebuffer = MinecraftClient.getInstance().getFramebuffer();*/
		}
		//? if <1.21.2 {
		framebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
		framebuffer.beginWrite(false);
		//?} else if <=1.21.4 {
		/*framebuffer.clear();
		((MinecraftClientAccessor) MinecraftClient.getInstance()).setFramebuffer(framebuffer);
		framebuffer.beginWrite(false);
		*///?} else {
		/*((MinecraftClientAccessor) MinecraftClient.getInstance()).setFramebuffer(framebuffer);
		RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(framebuffer.getColorAttachment(), 0, framebuffer.getDepthAttachment(), 1.0);
		*///?}
	}

	public static void draw(DrawContext context) {
		// Restore the original framebuffer
		//? if <1.21.2 {
		GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, previousFramebuffer);
		//?} else {
		/*((MinecraftClientAccessor) MinecraftClient.getInstance()).setFramebuffer(builtinFramebuffer);
		//? if <=1.21.4
		builtinFramebuffer.beginWrite(false);
		*///?}
		//? if >1.21.4
		/*context.draw();*/

		// Render the custom framebuffer's contents with transparency into the main buffer
		Window window = MinecraftClient.getInstance().getWindow();
		int width = window.getScaledWidth();
		int height = window.getScaledHeight();

		//? if <=1.21.4 {
		RenderSystem.setShaderTexture(0, framebuffer.getColorAttachment());
		RenderSystem.setShader(/*? if <1.21.2 {*/GameRenderer::getPositionTexProgram/*?} else {*//*ShaderProgramKeys.POSITION_TEX*//*?}*/);
		//?}

		Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();

		//? if <1.21 {
		BufferBuilder vertexConsumer = Tessellator.getInstance().getBuffer();
		vertexConsumer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		//?} else if <=1.21.4 {
		/*BufferBuilder vertexConsumer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		 *///?} else {
		/*VertexConsumer vertexConsumer = ((DrawContextAccessor) context).getVertexConsumers().getBuffer(RenderLayer.getGuiTextured(framebufferTextureId));
		*///?}

		vertexConsumer.vertex(matrix4f, 0, 0, 0).texture(0, 1)/*? <1.21 {*/.next()/*?}*//*? >1.21.4 {*//*.color(-1)*//*?}*/;
		vertexConsumer.vertex(matrix4f, 0, height, 0).texture(0, 0)/*? <1.21 {*/.next()/*?}*//*? >1.21.4 {*//*.color(-1)*//*?}*/;;
		vertexConsumer.vertex(matrix4f, width, height, 0).texture(1, 0)/*? <1.21 {*/.next()/*?}*//*? >1.21.4 {*//*.color(-1)*//*?}*/;;
		vertexConsumer.vertex(matrix4f, width, 0, 0).texture(1, 1)/*? <1.21 {*/.next()/*?}*//*? >1.21.4 {*//*.color(-1)*//*?}*/;;

		//? if <=1.21.4
		BufferRenderer.drawWithGlobalProgram(vertexConsumer.end());

		//? if >=1.21.2
		/*context.draw();*/
	}

	public static void resizeFramebuffer() {
		if (framebuffer != null) {
			Window window = MinecraftClient.getInstance().getWindow();
			framebuffer.resize(window.getFramebufferWidth(), window.getFramebufferHeight() /*? if <1.21.2 {*/, MinecraftClient.IS_SYSTEM_MAC/*?}*/);
		}
	}
}
