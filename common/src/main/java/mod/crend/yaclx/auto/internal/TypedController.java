package mod.crend.yaclx.auto.internal;

import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.*;
import mod.crend.yaclx.controller.BlockController;
import mod.crend.yaclx.controller.BlockOrTagController;
import mod.crend.yaclx.type.BlockOrTag;
import mod.crend.yaclx.type.ItemOrTag;
import mod.crend.yaclx.auto.annotation.*;
import mod.crend.yaclx.controller.DecoratedEnumController;
import mod.crend.yaclx.controller.ItemOrTagController;
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
	private static <T extends Enum<T>> Function<Option<T>, ControllerBuilder<T>> getEnumController(Field field, Class<?> clazz) {
		Decorate decorate = field.getAnnotation(Decorate.class);
		if (decorate != null) {
			if (!DecoratedEnumController.Decorator.class.isAssignableFrom(decorate.decorator())) {
				throw new RuntimeException("Decorator must be of type Decorator<T>!");
			}
			try {
				DecoratedEnumController.Decorator<T> decorator = (DecoratedEnumController.Decorator<T>) decorate.decorator().getConstructor().newInstance();
				return (opt -> DecoratedEnumController.DecoratedEnumControllerBuilder.create(opt)
						.enumClass((Class<T>) clazz)
						.decorator(decorator)
				);
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException(e);
			}
		} else {
			return (opt -> EnumControllerBuilder.create(opt)
					.enumClass((Class<T>) clazz)
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
	public static <T> Option.Builder<T> fromType(FieldParser<T> fieldParser) {
		return (Option.Builder<T>) internalFromType(fieldParser);
	}

	@SuppressWarnings("unchecked")
	private static Option.Builder<?> internalFromType(FieldParser<?> fieldParser) {
		Field field = fieldParser.field();
		Class<?> type = field.getType();

		if (type.equals(boolean.class)) {

			return TypedController.<Boolean>makeBuilder(fieldParser)
					.controller(getBooleanController(field));

		} else if (type.equals(int.class)) {

			return TypedController.<Integer>makeBuilder(fieldParser)
					.controller(getIntegerController(field));

		} else if (type.equals(long.class)) {

			return TypedController.<Long>makeBuilder(fieldParser)
					.controller(getLongController(field));

		} else if (type.equals(float.class)) {

			return TypedController.<Float>makeBuilder(fieldParser)
					.controller(getFloatController(field));

		} else if (type.equals(double.class)) {

			return TypedController.<Double>makeBuilder(fieldParser)
					.controller(getDoubleController(field));

		} else if (type.equals(String.class)) {

			return TypedController.<String>makeNullableBuilder(fieldParser)
					.controller(getStringController(field));

		} else if (type.isEnum()) {

			return TypedController.<Enum>makeBuilder(fieldParser)
					.controller(getEnumController(field, field.getType()));

		} else if (type.equals(Color.class)) {

			return TypedController.<Color>makeBuilder(fieldParser)
					.controller(getColorController(field));

		} else if (type.equals(Item.class)) {

			return TypedController.<Item>makeBuilder(fieldParser)
					.controller(getItemController(field));

		} else if (type.equals(ItemOrTag.class)) {

			return TypedController.<ItemOrTag>makeBuilder(fieldParser)
					.controller(getItemOrTagController(field));

		} else if (type.equals(Block.class)) {

			return TypedController.<Block>makeBuilder(fieldParser)
					.controller(getBlockController(field));

		} else if (type.equals(BlockOrTag.class)) {

			return TypedController.<BlockOrTag>makeBuilder(fieldParser)
					.controller(getBlockOrTagController(field));

		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> ListOption.Builder<T> fromListType(Class<T> type, FieldParser<?> fieldParser, boolean reverse) {
		return (ListOption.Builder<T>) internalFromListType(type, fieldParser, reverse);
	}
	@SuppressWarnings("unchecked")
	public static ListOption.Builder<?> internalFromListType(Class<?> type, FieldParser<?> fieldParser, boolean reverse) {
		Field field = fieldParser.field();

		if (type.equals(boolean.class)) {

			return TypedController.<Boolean>makeListBuilder(fieldParser, reverse)
					.controller(getBooleanController(field));

		} else if (type.equals(int.class)) {

			return TypedController.<Integer>makeListBuilder(fieldParser, reverse)
					.controller(getIntegerController(field));

		} else if (type.equals(long.class)) {

			return TypedController.<Long>makeListBuilder(fieldParser, reverse)
					.controller(getLongController(field));

		} else if (type.equals(float.class)) {

			return TypedController.<Float>makeListBuilder(fieldParser, reverse)
					.controller(getFloatController(field));

		} else if (type.equals(double.class)) {

			return TypedController.<Double>makeListBuilder(fieldParser, reverse)
					.controller(getDoubleController(field));

		} else if (type.equals(String.class)) {

			return TypedController.<String>makeListBuilder(fieldParser, reverse)
					.initial("")
					.controller(getStringController(field));

		} else if (type.isEnum()) {

			return TypedController.<Enum>makeListBuilder(fieldParser, reverse)
					.initial((Enum) type.getEnumConstants()[0])
					.controller(getEnumController(field, (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]));

		} else if (type.equals(Color.class)) {

			return TypedController.<Color>makeListBuilder(fieldParser, reverse)
					.initial(Color.BLACK)
					.controller(getColorController(field));

		} else if (type.equals(Item.class)) {

			return TypedController.<Item>makeListBuilder(fieldParser, reverse)
					.initial(Items.AIR)
					.controller(getItemController(field));

		} else if (type.equals(ItemOrTag.class)) {

			return TypedController.<ItemOrTag>makeListBuilder(fieldParser, reverse)
					.initial(new ItemOrTag(Items.AIR))
					.controller(getItemOrTagController(field));

		} else if (type.equals(Block.class)) {

			return TypedController.<Block>makeListBuilder(fieldParser, reverse)
					.initial(Blocks.AIR)
					.controller(getBlockController(field));

		} else if (type.equals(BlockOrTag.class)) {

			return TypedController.<BlockOrTag>makeListBuilder(fieldParser, reverse)
					.initial(new BlockOrTag(Blocks.AIR))
					.controller(getBlockOrTagController(field));

		}

		return null;
	}
}
