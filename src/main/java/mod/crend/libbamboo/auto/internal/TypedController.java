//? if yacl {
package mod.crend.libbamboo.auto.internal;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import mod.crend.libbamboo.controller.BlockController;
import mod.crend.libbamboo.controller.BlockOrTagController;
import mod.crend.libbamboo.controller.NestedController;
import mod.crend.libbamboo.controller.SubScreenController;
import mod.crend.libbamboo.controller.NestingController;
import mod.crend.libbamboo.type.BlockOrTag;
import mod.crend.libbamboo.type.ItemOrTag;
import mod.crend.libbamboo.auto.annotation.*;
import mod.crend.libbamboo.controller.DecoratedEnumController;
import mod.crend.libbamboo.controller.ItemOrTagController;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

class TypedController {
	private static Function<Option<Boolean>, ControllerBuilder<Boolean>> getBooleanController(Field field) {
		return TickBoxControllerBuilder::create;
	}

	private static Function<Option<Integer>, ControllerBuilder<Integer>> getIntegerController(Field field) {
		NumericRange range = field.getAnnotation(NumericRange.class);
		if (range != null) {
			return (opt -> IntegerSliderControllerBuilder.create(opt)
					.range((int) range.min(), (int) range.max())
					.step((int) range.interval())
			);
		}
		return IntegerFieldControllerBuilder::create;
	}

	private static Function<Option<Long>, ControllerBuilder<Long>> getLongController(Field field) {
		NumericRange range = field.getAnnotation(NumericRange.class);
		if (range != null) {
			return (opt -> LongSliderControllerBuilder.create(opt)
					.range(range.min(), range.max())
					.step(range.interval())
			);
		}
		return LongFieldControllerBuilder::create;
	}

	private static Function<Option<Float>, ControllerBuilder<Float>> getFloatController(Field field) {
		FloatingPointRange range = field.getAnnotation(FloatingPointRange.class);
		if (range != null) {
			return (opt -> FloatSliderControllerBuilder.create(opt)
					.range((float) range.min(), (float) range.max())
					.step((float) range.interval())
			);
		}
		return FloatFieldControllerBuilder::create;
	}

	private static Function<Option<Double>, ControllerBuilder<Double>> getDoubleController(Field field) {
		FloatingPointRange range = field.getAnnotation(FloatingPointRange.class);
		if (range != null) {
			return (opt -> DoubleSliderControllerBuilder.create(opt)
					.range(range.min(), range.max())
					.step(range.interval())
			);
		}
		return DoubleFieldControllerBuilder::create;
	}

	private static Function<Option<String>, ControllerBuilder<String>> getStringController(Field field) {
		StringOptions stringOptions = field.getAnnotation(StringOptions.class);

		if (stringOptions != null) {
			List<String> allowedValues = new ArrayList<>(Arrays.asList(stringOptions.options()));
			if (stringOptions.allowEmpty()) {
				allowedValues.add("");
			}
			return (opt ->
					DropdownStringControllerBuilder.create(opt)
							.values(allowedValues)
			);
		}

		return StringControllerBuilder::create;
	}

	@SuppressWarnings("unchecked")
	private static <T> Function<Option<T>, ControllerBuilder<T>> getEnumController(Field field, Class<Enum> clazz) {
		Decorate decorate = field.getAnnotation(Decorate.class);
		if (decorate != null) {
			if (!DecoratedEnumController.Decorator.class.isAssignableFrom(decorate.decorator())) {
				throw new RuntimeException("Decorator must be of type Decorator<T>!");
			}
			try {
				DecoratedEnumController.Decorator<T> decorator = (DecoratedEnumController.Decorator<T>) decorate.decorator().getConstructor().newInstance();
				return (opt -> (ControllerBuilder<T>) DecoratedEnumController.DecoratedEnumControllerBuilder.create((Option<Enum>) opt)
						.enumClass(clazz)
						.decorator((DecoratedEnumController.Decorator<Enum>) decorator)
				);
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException(e);
			}
		} else {
			return (opt -> (ControllerBuilder<T>) EnumControllerBuilder.create((Option<Enum>) opt)
					.enumClass(clazz)
			);
		}
	}

	private static Function<Option<Color>, ControllerBuilder<Color>> getColorController(Field field) {
		return (opt -> ColorControllerBuilder.create(opt)
				.allowAlpha(true)
		);
	}

	private static Function<Option<Item>, ControllerBuilder<Item>> getItemController(Field field) {
		return ItemControllerBuilder::create;
	}

	private static Function<Option<ItemOrTag>, ControllerBuilder<ItemOrTag>> getItemOrTagController(Field field) {
		return ItemOrTagController.ItemOrTagControllerBuilder::create;
	}

	private static Function<Option<Block>, ControllerBuilder<Block>> getBlockController(Field field) {
		return BlockController.BlockControllerBuilder::create;
	}

	private static Function<Option<BlockOrTag>, ControllerBuilder<BlockOrTag>> getBlockOrTagController(Field field) {
		return BlockOrTagController.BlockOrTagControllerBuilder::create;
	}

	private static <T> Function<Option<T>, ControllerBuilder<T>> getSubScreenController(Field field, Class<T> clazz, String modId, ConfigClassHandler<?> configClassHandler) {
		return option -> SubScreenController.SubScreenControllerBuilder.create(option)
				.clazz(clazz)
				.modId(modId)
				.outerHandler(configClassHandler);
	}
	private static class CustomControllerBuilder<T> implements ControllerBuilder<T> {

		Option<T> option;
		CustomController.ControllerFactory<T> factory;

		public CustomControllerBuilder(Option<T> option, CustomController.ControllerFactory<T> factory) {
			this.option = option;
			this.factory = factory;
		}

		@Override
		public Controller<T> build() {
			return factory.create(option);
		}
	}
	private static <T, U extends Option<T>> Function<U, ControllerBuilder<T>> getCustomController(Field field) {
		CustomController customController = field.getAnnotation(CustomController.class);
		try {
			@SuppressWarnings("unchecked")
			CustomController.ControllerFactory<T> factory
					= (CustomController.ControllerFactory<T>) customController.value().getConstructor().newInstance();
			return opt -> new CustomControllerBuilder<>(opt, factory);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	private static Function<Option<Boolean>, ControllerBuilder<Boolean>> getNestedToggleController(Field field) {
		return NestingController.NestingControllerBuilder::create;
	}

	private static <T> Option.Builder<T> makeBuilder(FieldParser<?> fieldParser) {
		@SuppressWarnings("unchecked")
		FieldParser<T> parser = (FieldParser<T>) fieldParser;
		return Option.<T>createBuilder()
				.binding(parser.makeBinding())
				.listener(parser.makeListener());
	}

	private static <T> Option.Builder<T> makeNullableBuilder(FieldParser<?> fieldParser) {
		@SuppressWarnings("unchecked")
		FieldParser<T> parser = (FieldParser<T>) fieldParser;
		return Option.<T>createBuilder()
				.binding(parser.makeNullableBinding())
				.listener(parser.makeListener());
	}

	private static <T> ListOption.Builder<T> makeListBuilder(FieldParser<?> fieldParser, boolean reverse) {
		@SuppressWarnings("unchecked")
		FieldParser<T> parser = (FieldParser<T>) fieldParser;
		return ListOption.<T>createBuilder()
				.binding(parser.makeListBinding(reverse))
				.listener(parser.makeListListener(reverse));
	}

	@SuppressWarnings("unchecked")
	private static <T> Function<Option<T>, ControllerBuilder<T>> getController(Field field, Class<T> type, String modId, ConfigClassHandler<?> configClassHandler) {

		if (field.isAnnotationPresent(SubScreen.class)) {
			return getSubScreenController(field, type, modId, configClassHandler);
		}

		if (field.isAnnotationPresent(CustomController.class)) {
			return getCustomController(field);
		}

		if (field.isAnnotationPresent(Nesting.class)) {
			return opt -> (ControllerBuilder<T>) getNestedToggleController(field).apply((Option<Boolean>) opt);
		}

		if (type.equals(boolean.class)) {

			return opt -> (ControllerBuilder<T>) getBooleanController(field).apply((Option<Boolean>) opt);

		} else if (type.equals(int.class)) {

			return opt -> (ControllerBuilder<T>) getIntegerController(field).apply((Option<Integer>) opt);

		} else if (type.equals(long.class)) {

			return opt -> (ControllerBuilder<T>) getLongController(field).apply((Option<Long>) opt);

		} else if (type.equals(float.class)) {

			return opt -> (ControllerBuilder<T>) getFloatController(field).apply((Option<Float>) opt);

		} else if (type.equals(double.class)) {

			return opt -> (ControllerBuilder<T>) getDoubleController(field).apply((Option<Double>) opt);

		} else if (type.equals(String.class)) {

			return opt -> (ControllerBuilder<T>) getStringController(field).apply((Option<String>) opt);

		} else if (type.isEnum()) {

			return opt -> (ControllerBuilder<T>) getEnumController(field, (Class<Enum>) field.getType()).apply((Option<Object>) opt);

		} else if (type.equals(Color.class)) {

			return opt -> (ControllerBuilder<T>) getColorController(field).apply((Option<Color>) opt);

		} else if (type.equals(Item.class)) {

			return opt -> (ControllerBuilder<T>) getItemController(field).apply((Option<Item>) opt);

		} else if (type.equals(ItemOrTag.class)) {

			return opt -> (ControllerBuilder<T>) getItemOrTagController(field).apply((Option<ItemOrTag>) opt);

		} else if (type.equals(Block.class)) {

			return opt -> (ControllerBuilder<T>) getBlockController(field).apply((Option<Block>) opt);

		} else if (type.equals(BlockOrTag.class)) {

			return opt -> (ControllerBuilder<T>) getBlockOrTagController(field).apply((Option<BlockOrTag>) opt);

		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> Option.Builder<T> fromType(FieldParser<T> fieldParser, String modId, ConfigClassHandler<?> configClassHandler) {
		Field field = fieldParser.field();
		Class<T> type = (Class<T>) field.getType();

		Function<Option<T>, ControllerBuilder<T>> controller = getController(field, type, modId, configClassHandler);
		if (controller == null) return null;

		if (field.isAnnotationPresent(Nested.class)) {
			Function<Option<T>, ControllerBuilder<T>> nestedController = controller;
			controller = opt -> NestedController.NestedControllerBuilder.create(opt).nestedControl(nestedController.apply(opt).build());
		}

		Option.Builder<T> builder;
		if (type.equals(String.class)) {
			builder = TypedController.makeNullableBuilder(fieldParser);
		} else {
			builder = TypedController.makeBuilder(fieldParser);
		}

		return builder.controller(controller);
	}

	@SuppressWarnings("unchecked")
	public static <T> ListOption.Builder<T> fromListType(Class<T> type, FieldParser<?> fieldParser, boolean reverse) {
		Field field = fieldParser.field();

		Function<Option<T>, ControllerBuilder<T>> controller = getController(field, type, null, null);
		if (controller == null) return null;

		if (field.isAnnotationPresent(Nested.class)) {
			Function<Option<T>, ControllerBuilder<T>> nestedController = controller;
			controller = opt -> NestedController.NestedControllerBuilder.create(opt).nestedControl(nestedController.apply(opt).build());
		}

		if (type.isEnum()) {
			return (ListOption.Builder<T>) TypedController.<Enum>makeListBuilder(fieldParser, reverse)
					.initial((Enum) type.getEnumConstants()[0])
					.controller(getEnumController(field, (Class<Enum>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]));
		}

		ListOption.Builder<T> listBuilder = TypedController.<T>makeListBuilder(fieldParser, reverse)
				.controller(controller);

		if (type.equals(String.class)) {
			((ListOption.Builder<String>) listBuilder).initial("");
		} else if (type.equals(Color.class)) {
			((ListOption.Builder<Color>) listBuilder).initial(Color.BLACK);
		} else if (type.equals(Item.class)) {
			((ListOption.Builder<Item>) listBuilder).initial(Items.AIR);
		} else if (type.equals(ItemOrTag.class)) {
			((ListOption.Builder<ItemOrTag>) listBuilder).initial(new ItemOrTag(Items.AIR));
		} else if (type.equals(Block.class)) {
			((ListOption.Builder<Block>) listBuilder).initial(Blocks.AIR);
		} else if (type.equals(BlockOrTag.class)) {
			((ListOption.Builder<BlockOrTag>) listBuilder).initial(new BlockOrTag(Blocks.AIR));
		}
		return listBuilder;
	}

}
//?}
