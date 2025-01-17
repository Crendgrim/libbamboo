package mod.crend.libbamboo.auto.annotation;

import mod.crend.libbamboo.controller.DecoratedEnumController;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Decorate {
	Class<? extends DecoratedEnumController.Decorator<?>> decorator();
}
