package mod.crend.libbamboo.auto.annotation;

//? if yacl {
import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
//?}

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CustomController {
	Class<? extends ControllerFactory<?>> value();

	@FunctionalInterface
	interface ControllerFactory<T> {
		//? if yacl {
		Controller<T> create(Option<T> option);
		//?} else
		/*Object create(Object option);*/
	}
}
