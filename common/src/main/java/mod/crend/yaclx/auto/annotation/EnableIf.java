package mod.crend.yaclx.auto.annotation;

import java.lang.annotation.*;

@Repeatable(EnableIf.EnableIfList.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EnableIf {
	String field();
	Class<? extends Predicate> value();

	interface Predicate extends java.util.function.Predicate<Object> { }

	class BooleanPredicate implements Predicate {
		@Override
		public boolean test(Object value) {
			return value == Boolean.TRUE;
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@interface EnableIfList {
		EnableIf[] value();
	}

}

