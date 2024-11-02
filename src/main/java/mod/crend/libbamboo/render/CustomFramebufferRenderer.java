package mod.crend.libbamboo.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mod.crend.libbamboo.mixin.MinecraftClientAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
//? if >=1.21.2
/*import net.minecraft.client.gl.ShaderProgramKeys;*/
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.Window;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;

public class CustomFramebufferRenderer {
	static Framebuffer framebuffer = null;
	//? if <1.21.2 {
	static int previousFramebuffer;
	//?} else
	/*static Framebuffer builtinFramebuffer;*/

	public static void init() {
		// Setup extra framebuffer to draw into
		//? if <1.21.2
		previousFramebuffer = GlStateManager.getBoundFramebuffer();
		if (framebuffer == null) {
			Window window = MinecraftClient.getInstance().getWindow();
			framebuffer = new SimpleFramebuffer(window.getFramebufferWidth(), window.getFramebufferHeight(), true/*? if <1.21.2 {*/, MinecraftClient.IS_SYSTEM_MAC/*?}*/);
			framebuffer.setClearColor(0, 0, 0, 0);
			//? if >=1.21.2
			/*builtinFramebuffer = MinecraftClient.getInstance().getFramebuffer();*/
		}
		//? if <1.21.2 {
		framebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
		//?} else {
		/*framebuffer.clear();
		((MinecraftClientAccessor) MinecraftClient.getInstance()).setFramebuffer(framebuffer);
		*///?}
		framebuffer.beginWrite(false);
	}

	public static void draw(DrawContext context) {
		// Restore the original framebuffer
		//? if <1.21.2 {
		GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, previousFramebuffer);
		//?} else {
		/*((MinecraftClientAccessor) MinecraftClient.getInstance()).setFramebuffer(builtinFramebuffer);
		builtinFramebuffer.beginWrite(false);
		*///?}

		// Render the custom framebuffer's contents with transparency into the main buffer
		Window window = MinecraftClient.getInstance().getWindow();
		int width = window.getScaledWidth();
		int height = window.getScaledHeight();

		RenderSystem.setShaderTexture(0, framebuffer.getColorAttachment());
		RenderSystem.setShader(/*? if <1.21.2 {*/GameRenderer::getPositionTexProgram/*?} else {*//*ShaderProgramKeys.POSITION_TEX*//*?}*/);
		Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
		//? if <1.21 {
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(matrix4f, 0, 0, 0).texture(0, 1).next();
		bufferBuilder.vertex(matrix4f, 0, height, 0).texture(0, 0).next();
		bufferBuilder.vertex(matrix4f, width, height, 0).texture(1, 0).next();
		bufferBuilder.vertex(matrix4f, width, 0, 0).texture(1, 1).next();
		//?} else {
		
		/*BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(matrix4f, 0, 0, 0).texture(0, 1);
		bufferBuilder.vertex(matrix4f, 0, height, 0).texture(0, 0);
		bufferBuilder.vertex(matrix4f, width, height, 0).texture(1, 0);
		bufferBuilder.vertex(matrix4f, width, 0, 0).texture(1, 1);
		 *///?}
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

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
