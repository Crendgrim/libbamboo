package mod.crend.yaclx.auto.annotation;

import dev.isxander.yacl3.gui.image.ImageRenderer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DescriptionImage {
	Class<? extends DescriptionImageRendererFactory<?>> value();

	@FunctionalInterface
	interface DescriptionImageRendererFactory<T> {
		ImageRenderer create(T value);
	}
}
