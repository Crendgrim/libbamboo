package mod.crend.libbamboo.auto.internal;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.gui.image.ImageRenderer;
import mod.crend.libbamboo.auto.annotation.*;
import mod.crend.libbamboo.render.ListImageRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public record FieldParser<T>(
		String modId,
		String key,
		Field field,
		Object defaults,
		Object parent,
		Object dummy,
		boolean isGroup
) {
	@SuppressWarnings("unchecked")
	public Class<T> getType() {
		return (Class<T>) field.getType();
	}

	public String getTranslationKey() {
		Translation translationKey = field.getAnnotation(Translation.class);
		return (translationKey == null
				? modId + (isGroup ? ".group." : ".option.") + key
				: translationKey.key());
	}

	public String getDescriptionTranslationKey() {
		Translation translationKey = field.getAnnotation(Translation.class);
		if (translationKey == null || translationKey.description().isBlank()) {
			String descriptionTranslationKey = getTranslationKey() + ".description";
			return (I18n.hasTranslation(descriptionTranslationKey) ? descriptionTranslationKey : getTranslationKey());
		}
		return translationKey.description();
	}

	private OptionDescription buildDescription(@Nullable T value, @Nullable Consumer<ImageRenderer> withRenderer) {
		var description = OptionDescription.createBuilder()
				.text(Text.translatable(getDescriptionTranslationKey()));
		DescriptionImage descriptionImage = field.getAnnotation(DescriptionImage.class);
		if (descriptionImage != null) {
			try {
				@SuppressWarnings("unchecked")
				DescriptionImage.DescriptionImageRendererFactory<T> factory
						= (DescriptionImage.DescriptionImageRendererFactory<T>) descriptionImage.value().getConstructor().newInstance();
				ImageRenderer renderer = factory.create(value);
				if (withRenderer != null) withRenderer.accept(renderer);
				description.customImage(CompletableFuture.completedFuture(Optional.of(renderer)));
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException(e);
			}
		}
		return description.build();
	}
	private OptionDescription buildDescription(T value) {
		return buildDescription(value, null);
	}

	private OptionDescription buildListDescription(ListOption.Builder<T> builder) {
		return buildDescription(null, imageRenderer -> {
			if (imageRenderer instanceof ListImageRenderer) {
				@SuppressWarnings("unchecked")
				ListImageRenderer<T> listImageRenderer = (ListImageRenderer<T>) imageRenderer;
				builder.listener((opt, val) -> listImageRenderer.setList(val));
			}
		});
	}

	private void parseDependencies(Map<String, List<EnableIf>> dependencies) {
		EnableIf[] enableIfList = field.getAnnotationsByType(EnableIf.class);
		if (enableIfList.length > 0) {
			dependencies.put(key, Arrays.asList(enableIfList));
		}
	}

	public void setCommonAttributes(Option.Builder<T> optionBuilder, Map<String, List<EnableIf>> dependencies) {
		optionBuilder.name(Text.translatable(getTranslationKey()));
		optionBuilder.description(this::buildDescription);
		parseDependencies(dependencies);
		Listener[] listeners = field.getAnnotationsByType(Listener.class);
		for (Listener listener : listeners) {
			try {
				BiConsumer<String, Object> callback = listener.value().getConstructor().newInstance();
				optionBuilder.listener((opt, value) -> callback.accept(key, value));
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException(e);
			}
		}
		OnSave onSave = field.getAnnotation(OnSave.class);
		if (onSave != null) {
			if (onSave.gameRestart()) optionBuilder.flag(OptionFlag.GAME_RESTART);
			if (onSave.reloadChunks()) optionBuilder.flag(OptionFlag.RELOAD_CHUNKS);
			if (onSave.worldRenderUpdate()) optionBuilder.flag(OptionFlag.WORLD_RENDER_UPDATE);
			if (onSave.assetReload()) optionBuilder.flag(OptionFlag.ASSET_RELOAD);
		}
	}

	public void setCommonAttributes(ListOption.Builder<T> optionBuilder, Map<String, List<EnableIf>> dependencies) {
		optionBuilder.name(Text.translatable(getTranslationKey()));
		optionBuilder.description(buildListDescription(optionBuilder));
		parseDependencies(dependencies);
		Listener[] listeners = field.getAnnotationsByType(Listener.class);
		for (Listener listener : listeners) {
			try {
				BiConsumer<String, Object> callback = listener.value().getConstructor().newInstance();
				optionBuilder.listener((opt, value) -> callback.accept(key, value));
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException(e);
			}
		}
		OnSave onSave = field.getAnnotation(OnSave.class);
		if (onSave != null) {
			if (onSave.gameRestart()) optionBuilder.flag(OptionFlag.GAME_RESTART);
			if (onSave.reloadChunks()) optionBuilder.flag(OptionFlag.RELOAD_CHUNKS);
			if (onSave.worldRenderUpdate()) optionBuilder.flag(OptionFlag.WORLD_RENDER_UPDATE);
			if (onSave.assetReload()) optionBuilder.flag(OptionFlag.ASSET_RELOAD);
		}
	}

	public void setCommonAttributes(OptionGroup.Builder optionGroupBuilder) {
		optionGroupBuilder.name(Text.translatable(getTranslationKey()));
		optionGroupBuilder.description(OptionDescription.createBuilder()
				.text(Text.translatable(getDescriptionTranslationKey()))
				.build()
		);
	}

	public Option.Builder<T> optionBuilder(Map<String, List<EnableIf>> dependencies) {
		Option.Builder<T> optionBuilder = TypedController.fromType(this);
		if (optionBuilder != null) {
			setCommonAttributes(optionBuilder, dependencies);
			return optionBuilder;
		}
		return null;
	}

	public ListOption.Builder<T> listOptionBuilder(Class<T> clazz, Map<String, List<EnableIf>> dependencies, boolean reverse) {
		ListOption.Builder<T> optionBuilder = TypedController.fromListType(clazz, this, reverse);
		if (optionBuilder != null) {
			setCommonAttributes(optionBuilder, dependencies);
			return optionBuilder;
		}
		return null;
	}

	private BindingHelper<T> getBinding() {
		return BindingHelper.create(field);
	}

	private BindingHelper<T> getNullableBinding() {
		return BindingHelper.create(field, getType());
	}

	private BindingHelper<List<T>> getListBinding(boolean reverse) {
		return BindingHelper.create(field, reverse);
	}

	public Binding<T> makeBinding() {
		return getBinding().makeBinding(defaults, parent);
	}

	public Binding<T> makeNullableBinding() {
		return getNullableBinding().makeBinding(defaults, parent);
	}

	public Binding<List<T>> makeListBinding(boolean reverse) {
		return getListBinding(reverse).makeBinding(defaults, parent);
	}

	public BiConsumer<Option<T>, T> makeListener() {
		return getBinding().makeListener(dummy);
	}

	public BiConsumer<Option<List<T>>, List<T>> makeListListener(boolean reverse) {
		return getListBinding(reverse).makeListener(dummy);
	}

}
