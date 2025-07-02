//? if yacl {
package mod.crend.libbamboo.auto;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import mod.crend.libbamboo.auto.annotation.*;
import mod.crend.libbamboo.auto.internal.FieldParser;
import mod.crend.libbamboo.controller.NestingController;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * Automagic config UI generation from a config file based on annotations.
 *
 * @see AutoYacl#parse
 */
public class AutoYacl <T> {

	/**
	 * A wrapper can add fields to the current option builder, but no nested groups.
	 */
	protected static class Wrapper {
		protected final OptionAddable builder;
		protected final Object bDefaults;
		protected final Object bParent;
		protected final @Nullable Object bDummyConfig;
		protected final Map<String, OptionGroup.Builder> groups;
		protected final Map<String, Option<?>> options;
		protected final Map<String, List<EnableIf>> dependencies;
		protected final List<Pair<String, NestingController>> nested;
		protected final String modId;
		protected final ConfigClassHandler<?> configClassHandler;

		protected Wrapper(
				String modId,
				OptionAddable builder,
			  	Object bDefaults,
				Object bParent,
				@Nullable Object bDummyConfig,
				Map<String, OptionGroup.Builder> groups,
				Map<String, Option<?>> options,
			    Map<String, List<EnableIf>> dependencies,
				List<Pair<String, NestingController>> nested,
				ConfigClassHandler<?> configClassHandler
		) {
			this.modId = modId;
			this.builder = builder;
			this.bDefaults = bDefaults;
			this.bParent = bParent;
			this.bDummyConfig = bDummyConfig;
			this.groups = groups;
			this.options = options;
			this.dependencies = dependencies;
			this.nested = nested;
			this.configClassHandler = configClassHandler;
		}

		/**
		 * Returns the correct builder for this member field.
		 *
		 * <p> If the field has a @Category annotation, add to that category's builder; otherwise add it to the currently
		 * active one.
 		 */
		OptionAddable getContainingBuilder(Field field) {
			Category category = field.getAnnotation(Category.class);
			if (category != null && !category.group().isBlank()) {
				if (!groups.containsKey(category.group())) {
					groups.put(category.group(), OptionGroup.createBuilder()
							.name(Text.translatable(modId + ".group." + category.group())));
				}
				return groups.get(category.group());
			}
			return builder;
		}

		/**
		 * Register member fields in the correct order.
		 *
		 * <p>The correct order is to follow an @Order annotation, going in the order of field names supplied, and then
		 * any others in iteration order. This iteration order is *technically* JVM implementation detail, but we can
		 * fairly safely assume it is going to be the declaration order.
		 *
		 * @param transitiveWrapper the wrapper to add options to
		 * @param key the key of the field param
		 * @param field the object field of which we add members
		 */
		protected void registerMemberFields(Wrapper transitiveWrapper, String key, Field field) {
			Order order = field.getType().getAnnotation(Order.class);
			Set<String> handledFields;
			if (order != null) {
				handledFields = new HashSet<>(List.of(order.value()));
				for (String fieldName : order.value()) {
					try {
						transitiveWrapper.register(key + "." + fieldName, field.getType().getField(fieldName));
					} catch (NoSuchFieldException e) {
						throw new RuntimeException(e);
					}
				}
			} else {
				handledFields = new HashSet<>();
			}
			for (Field memberField : field.getType().getFields()) {
				if (!handledFields.contains(memberField.getName())) {
					transitiveWrapper.register(key + "." + memberField.getName(), memberField);
				}
			}
		}

		/**
		 * Register object transitively. Is overloaded for CategoryWrapper to also handle non-transitive registration.
		 */
		protected <T> void registerObject(OptionAddable containingBuilder, String key, Field field) {
			// register object transitively
			try {
				Wrapper transitiveWrapper = new Wrapper(
						modId,
						containingBuilder,
						field.get(bDefaults),
						field.get(bParent),
						bDummyConfig == null ? null : field.get(bDummyConfig),
						groups,
						options,
						dependencies,
						nested,
						configClassHandler
				);
				registerMemberFields(transitiveWrapper, key, field);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		/**
		 * Register a field with the given key. Main entry point for field parsing.
		 */
		protected <T> void register(String key, Field field) {
			OptionAddable containingBuilder = getContainingBuilder(field);
			
			Label label = field.getAnnotation(Label.class);
			if (label != null) {
				containingBuilder.option(LabelOption.create(Text.translatable(label.key())));
			}

			FieldParser<T> fieldParser = new FieldParser<>(modId, key, field, bDefaults, bParent, bDummyConfig, false);
			Option.Builder<T> option = fieldParser.optionBuilder(dependencies, configClassHandler);
			if (option != null) {
				options.put(key, option.build());
				containingBuilder.option(options.get(key));
				Nesting nesting = field.getAnnotation(Nesting.class);
				if (nesting != null) {
					nested.add(new Pair<>(nesting.value(), (NestingController) options.get(key).controller()));
				}
			} else {
				registerObject(containingBuilder, key, field);
			}
		}

		/**
		 * Shorthand to create a single field parsing context.
		 */
		public static <T> Option.Builder<T> createOptionBuilder(
				String modId,
				String key,
				Field field,
				Object defaults,
				Object parent,
				@Nullable Object dummy,
				Map<String,
				List<EnableIf>> dependencies,
				ConfigClassHandler<?> configClassHandler
		) {
			assert(field.getDeclaringClass().isInstance(defaults));
			assert(field.getDeclaringClass().isInstance(parent));
			FieldParser<T> fieldParser = new FieldParser<>(modId, key, field, defaults, parent, dummy, false);
			return fieldParser.optionBuilder(dependencies, configClassHandler);
		}


	}

	/**
	 * A category wrapper can, in addition to fields, also add objects as and fields to groups.
	 * Use @Category(group=) and @TransitiveObject to fine tune groupings.
	 */
	public static class CategoryWrapper extends Wrapper {
		protected final List<ListOption<?>> listOptions = new ArrayList<>();
		public CategoryWrapper(
				String modId,
				OptionAddable builder,
				Object bDefaults,
				Object bParent,
				@Nullable Object bDummyConfig,
				Map<String, List<EnableIf>> dependencies,
				ConfigClassHandler<?> configClassHandler
		) {
			// Each category uses its own set of groups and options
			super(modId, builder, bDefaults, bParent, bDummyConfig,
					new LinkedHashMap<>() /* groups */,
					new LinkedHashMap<>() /* options */,
					dependencies,
					new ArrayList<>(),
					configClassHandler);
		}
		private CategoryWrapper(String modId,
								OptionAddable builder,
								Object bDefaults,
								Object bParent,
								@Nullable Object bDummyConfig,
								Map<String, OptionGroup.Builder> groups,
								Map<String, Option<?>> options,
								Map<String, List<EnableIf>> dependencies,
								List<Pair<String, NestingController>> nested,
								ConfigClassHandler<?> configClassHandler
		) {
			super(modId, builder, bDefaults, bParent, bDummyConfig, groups, options, dependencies, nested, configClassHandler);
		}

		/**
		 * Registers object transitively. Uses a CategoryWrapper instead of using Wrapper.registerObject as our category
		 * builder may contain groups if a top-level object is transitive:
		 * - class Config
		 *    - transitive ConfigObject member
		 *        - group DetailConfigObject member
		 */
		protected void registerObjectTransitively(OptionAddable containingBuilder, String key, Field field) {
			// register object transitively
			try {
				Wrapper transitiveWrapper = new CategoryWrapper(
						modId,
						containingBuilder,
						field.get(bDefaults),
						field.get(bParent),
						bDummyConfig == null ? null : field.get(bDummyConfig),
						groups,
						options,
						dependencies,
						nested,
						configClassHandler
				);
				registerMemberFields(transitiveWrapper, key, field);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		/**
		 * Register object transitively or wrapped in an option group.
		 *
		 * <p>This method gets called when all basic supported field handling has been done, so the field definitely
		 * refers to an object of which we want to add all annotated member fields.
		 */
		@Override
		protected <T> void registerObject(OptionAddable containingBuilder, String key, Field field) {
			if (field.isAnnotationPresent(TransitiveObject.class)) {
				registerObjectTransitively(containingBuilder, key, field);
				return;
			}
			// If containingBuilder and builder are not equal, we are in a nested context
			if (containingBuilder != builder) {
				super.registerObject(containingBuilder, key, field);
				return;
			}
			// Special handling for ListOptions
			if (field.getType().equals(List.class)) {
				ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
				@SuppressWarnings("unchecked")
				Class<T> innerType = (Class<T>) parameterizedType.getActualTypeArguments()[0];
				FieldParser<T> fieldParser = new FieldParser<>(
						modId,
						key,
						field,
						bDefaults,
						bParent,
						bDummyConfig,
						false
				);
				ListOption.Builder<T> builder = fieldParser.listOptionBuilder(innerType,
						dependencies,
						field.isAnnotationPresent(Reverse.class)
				);
				if (builder != null) {
					if (field.isAnnotationPresent(NumberOfItems.class)) {
						NumberOfItems numberOfItems = field.getAnnotation(NumberOfItems.class);
						if (numberOfItems.min() >= 0) {
							builder.minimumNumberOfEntries(numberOfItems.min());
						}
						if (numberOfItems.max() >= 0) {
							builder.maximumNumberOfEntries(numberOfItems.max());
						}
					}
					ListOption<T> listOption = builder.build();
					options.put(key, listOption);
					listOptions.add(listOption);
				}
				return;
			}

			// Ensure we can reuse groups if they get added using @Category annotations
			if (!groups.containsKey(key)) {
				groups.put(key, OptionGroup.createBuilder());
			}
			FieldParser<T> fieldParser = new FieldParser<>(modId, key, field, bDefaults, bParent, bDummyConfig, true);
			var groupBuilder = groups.get(key);
			fieldParser.setCommonAttributes(groupBuilder);
			try {
				Wrapper groupWrapper = new Wrapper(
						modId,
						groupBuilder,
						field.get(bDefaults),
						field.get(bParent),
						bDummyConfig == null ? null : field.get(bDummyConfig),
						groups,
						options,
						dependencies,
						nested,
						configClassHandler
				);
				registerMemberFields(groupWrapper, key, field);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		/**
		 * Fully builds the category after building all of its contained options and option groups recursively.
		 * @return a built category
		 */
		public ConfigCategory build() {
			for (Pair<String, NestingController> toggles : nested) {
				toggles.getRight().setNestedOption(options.get(toggles.getLeft()));
			}
			ConfigCategory.Builder categoryBuilder = (ConfigCategory.Builder) builder;
			for (var group : groups.values()) {
				categoryBuilder.group(group.build());
			}
			for (var list : listOptions) {
				categoryBuilder.group(list);
			}
			return categoryBuilder.build();
		}
	}

	private CategoryWrapper wrapBuilder(String categoryName) {
		return new CategoryWrapper(
				modId,
				ConfigCategory.createBuilder().name(Text.translatable(modId + ".category." + categoryName)),
				configClassHandler.defaults(),
				configClassHandler.instance(),
				dummyConfig,
				dependencies,
				configClassHandler);
	}

	/**
	 * Parses the given config class's annotations and generates a YACL config UI.
	 *
	 * <p>You can use it in your screen factory as follows:
	 * <pre>
	 * public class ConfigScreenFactory {
	 *     public static Screen makeScreen(Screen parent) {
	 *         return YetAnotherConfigLib.create(Config.CONFIG_STORE.withYacl().instance,
	 *             (defaults, config, builder) -> AutoYacl.parse(Config.class, defaults, config, builder)
	 *         ).generateScreen(parent);
	 *     }
	 * }
	 * </pre>
	 *
	 * @param configClassHandler the class referring to T
	 * @param builder YACL screen builder. Should be empty at first and will return buildable.
	 * @return The builder after every field has been added to it.
	 * @param <T> config class
	 */
	@SuppressWarnings("unused")
	public static <T> YetAnotherConfigLib.Builder parse(
			ConfigClassHandler<T> configClassHandler,
			YetAnotherConfigLib.Builder builder
	) {
		return new AutoYacl<>(configClassHandler).parse(builder);
	}

	private final ConfigClassHandler<T> configClassHandler;
	public final String modId;
	private @Nullable T dummyConfig = null;
	public final Map<String, Option<?>> options = new LinkedHashMap<>();
	private final Map<String, List<EnableIf>> dependencies = new LinkedHashMap<>();

	/**
	 * Instance this class to get a dynamic builder from which you may create individual options.
	 *
	 * @param configClassHandler the class referring to T
	 */
	public AutoYacl(ConfigClassHandler<T> configClassHandler) {
		this.configClassHandler = configClassHandler;
		AutoYaclConfig ayc = configClassHandler.configClass().getAnnotation(AutoYaclConfig.class);
		this.modId = ayc.modid();
	}
	public AutoYacl(ConfigClassHandler<T> configClassHandler, String modId) {
		this.configClassHandler = configClassHandler;
		this.modId = modId;
	}

	@SuppressWarnings("UnusedReturnValue")
	public AutoYacl<T> dummyConfig(T dummyConfig) {
		this.dummyConfig = dummyConfig;
		return this;
	}

	protected Option<?> findDependantOption(String key, EnableIf enableIf) {
		if (key.lastIndexOf('.') != -1) {
			Option<?> opt = options.get(key.substring(0, key.lastIndexOf('.') + 1) + enableIf.field());
			if (opt != null) return opt;
			opt = options.get(enableIf.field());
			if (opt != null) return opt;
			throw new RuntimeException("Could not find dependant field " + enableIf.field() + " for field " + key
					+ ": Neither '" + enableIf.field() + "' nor '"
					+ key.substring(0, key.lastIndexOf('.') + 1) + enableIf.field() + "' are known.");
		} else {
			Option<?> opt = options.get(enableIf.field());
			if (opt != null) return opt;
			throw new RuntimeException("Could not find dependant field " + enableIf.field() + " for field " + key
					+ ": '" + enableIf.field() + "' is not known.");
		}
	}

	/**
	 * Resolve all @EnableIf dependencies and add the required listeners to toggle active status of options.
	 * We do this outside of the category builders, so that options from other categories can be referenced.
	 */
	protected void computeDependencies() {
		dependencies.forEach((key, list) -> {
			Option<?> dependingOption = options.get(key);

			var captureList = list.stream()
					.map(enableIf -> {
						Option<?> depend = findDependantOption(key, enableIf);
						try {
							return new Pair<>(depend, enableIf.value().getConstructor().newInstance());
						} catch (ReflectiveOperationException e) {
							throw new RuntimeException(e);
						}
					})
					.toList();
			// An option is disabled if any of the dependant options have a "false" predicate
			dependingOption.setAvailable(
					captureList.stream().allMatch(p ->
							p.getRight().test(p.getLeft().pendingValue())
					)
			);
			// Manually invoke the listeners on build so active states correspond to the current config values
			for (var pair : captureList) {
				pair.getLeft().addListener((opt, val) ->
						dependingOption.setAvailable(
								captureList.stream().allMatch(p ->
										p.getRight().test(p.getLeft().pendingValue())
								)
						)
				);
			}
		});
	}

	/**
	 * Parses all relevant annotations into a full YACL GUI and adds everything into the provided builder.
	 */
	public YetAnotherConfigLib.Builder parse(YetAnotherConfigLib.Builder builder) {
		String translationKey = modId + ".title";
		if (configClassHandler.configClass().isAnnotationPresent(AutoYaclConfig.class)) {
			AutoYaclConfig ayc = configClassHandler.configClass().getAnnotation(AutoYaclConfig.class);
			if (!ayc.translationKey().isBlank()) {
				translationKey = ayc.translationKey();
			}
		}
		Text modTitle = Text.translatable(translationKey);

		Map<String, CategoryWrapper> categories = new LinkedHashMap<>();
		CategoryWrapper categoryMain = wrapBuilder("general");
		categories.put("general", categoryMain);

		for (Field field : configClassHandler.configClass().getFields()) {
			if (field.isAnnotationPresent(SerialEntry.class)) {
				Category category = field.getAnnotation(Category.class);
				if (category == null) {
					categoryMain.register(field.getName(), field);
				} else {
					if (!categories.containsKey(category.name())) {
						categories.put(category.name(), wrapBuilder(category.name()));
					}
					categories.get(category.name()).register(field.getName(), field);
				}
			}
		}

		// Dependency resolution for @EnableIf annotations
		for (var entry : categories.values()) {
			options.putAll(entry.options);
			builder.category(entry.build());
		}
		computeDependencies();

		return builder.title(modTitle);
	}

	/**
	 * Only create an option from the field with the given key. All annotations except for @Category and @Label will be
	 * used to configure the option builder.
	 *
	 * @param key the name of the field in the config class
	 * @return an option builder that can be built or further configured
	 * @param <S> the type of the field
	 */
	@SuppressWarnings("unused")
	public <S> Option.Builder<S> makeOption(String key) {
		try {
			return Wrapper.createOptionBuilder(modId, key, configClassHandler.configClass().getField(key), configClassHandler.defaults(), configClassHandler.instance(), dummyConfig, dependencies, configClassHandler);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}
}
//?}
