package mod.crend.libbamboo.auto.annotation;

import java.lang.annotation.*;
import java.util.function.BiConsumer;

@Repeatable(Listener.Listeners.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Listener {
	Class<? extends Callback> value();

	interface Callback extends BiConsumer<String, Object> { }

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@interface Listeners {
		Listener[] value();
	}
}
