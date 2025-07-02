//? if yacl {
package mod.crend.libbamboo.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.dropdown.AbstractDropdownController;
import dev.isxander.yacl3.gui.controllers.dropdown.AbstractDropdownControllerElement;
import dev.isxander.yacl3.gui.controllers.dropdown.ItemController;
import dev.isxander.yacl3.gui.utils.ItemRegistryHelper;
import dev.isxander.yacl3.impl.controller.AbstractControllerBuilderImpl;
import mod.crend.libbamboo.type.ItemOrTag;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Simple controller that simply runs the button action on press
 * and renders a {@link} Text on the right.
 */
public class ItemOrTagController extends AbstractDropdownController<ItemOrTag> {

	/**
	 * Constructs an item controller
	 *
	 * @param option bound option
	 */
	public ItemOrTagController(Option<ItemOrTag> option) {
		super(option);
	}

	static Stream<Identifier> getMatchingItemTagIdentifiers(String value) {
		int sep = value.indexOf(Identifier.NAMESPACE_SEPARATOR);
		Predicate<TagKey<Item>> filterPredicate;
		if (sep == -1) {
			filterPredicate = tagKey ->
					tagKey.id().getPath().contains(value)
							|| tagKey.id().toString().toLowerCase().contains(value.toLowerCase());
		} else {
			String namespace = value.substring(0, sep);
			String path = value.substring(sep + 1);
			filterPredicate = tagKey -> tagKey.id().getNamespace().equals(namespace) && tagKey.id().getPath().startsWith(path);
		}
		return ItemOrTag.getItemTags().stream()
				.filter(filterPredicate)
				.sorted((t1, t2) -> {
					String path = (sep == -1 ? value : value.substring(sep + 1));
					boolean id1StartsWith = t1.id().getPath().startsWith(path);
					boolean id2StartsWith = t2.id().getPath().startsWith(path);
					if (id1StartsWith) {
						if (id2StartsWith) {
							return t1.id().compareTo(t2.id());
						}
						return -1;
					}
					if (id2StartsWith) {
						return 1;
					}
					return t1.id().compareTo(t2.id());
				})
				.map(TagKey::id);
	}

	@Override
	public String getString() {
		return option.pendingValue().toString();
	}

	@Override
	public void setFromString(String value) {
		ItemOrTag.fromString(value, false).ifPresent(option::requestSet);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Text formatValue() {
		return Text.literal(getString());
	}

	@Override
	public boolean isValueValid(String value) {
		if (value.startsWith("#")) {
			return ItemOrTag.isItemTag(value.substring(1));
		} else {
			return ItemRegistryHelper.isRegisteredItem(value);
		}
	}

	@Override
	protected String getValidValue(String value, int offset) {
		if (value.startsWith("#")) {
			// TODO look up all fitting item tags
			return value;
		} else {
			return ItemRegistryHelper.getMatchingItemIdentifiers(value)
					.skip(offset)
					.findFirst()
					.map(Identifier::toString)
					.orElseGet(this::getString);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
		return new ItemOrTagControllerElement(this, screen, widgetDimension);
	}

	public interface ItemOrTagControllerBuilder extends ControllerBuilder<ItemOrTag> {
		static ItemOrTagControllerBuilderImpl create(Option<ItemOrTag> option) {
			return new ItemOrTagControllerBuilderImpl(option);
		}
	}

	public static class ItemOrTagControllerBuilderImpl extends AbstractControllerBuilderImpl<ItemOrTag> implements ItemOrTagControllerBuilder {
		protected ItemOrTagControllerBuilderImpl(Option<ItemOrTag> option) {
			super(option);
		}

		@Override
		public Controller<ItemOrTag> build() {
			return new ItemOrTagController(option);
		}
	}

	public static class ItemOrTagControllerElement extends AbstractDropdownControllerElement<ItemOrTag, Identifier> {
		private final ItemOrTagController itemOrTagController;
		protected ItemOrTag currentItem = null;
		protected Map<Identifier, ItemOrTag> matchingItems = new HashMap<>();

		public ItemOrTagControllerElement(ItemOrTagController control, YACLScreen screen, Dimension<Integer> dim) {
			super(control, screen, dim);
			this.itemOrTagController = control;
		}

		@Override
		protected void drawValueText(DrawContext graphics, int mouseX, int mouseY, float delta) {
			var oldDimension = getDimension();
			setDimension(getDimension().withWidth(getDimension().width() - getDecorationPadding()));
			super.drawValueText(graphics, mouseX, mouseY, delta);
			setDimension(oldDimension);
			if (currentItem != null && currentItem.getAnyItem() != null) {
				graphics.drawItemWithoutEntity(currentItem.getAnyItem().getDefaultStack(), getDimension().xLimit() - getXPadding() - getDecorationPadding() + 2, getDimension().y() + 2);
			}
		}

		@Override
		public List<Identifier> computeMatchingValues() {
			List<Identifier> identifiers;
			if (inputField.startsWith("#")) {
				identifiers = getMatchingItemTagIdentifiers(inputField.substring(1)).toList();
				ItemOrTag.fromString(inputField.substring(1), true)
						.ifPresent(itemOrTag -> currentItem = itemOrTag);
				for (Identifier identifier : identifiers) {
					matchingItems.put(identifier, new ItemOrTag(TagKey.of(RegistryKeys.ITEM, identifier)));
				}
			} else {
				identifiers = ItemRegistryHelper.getMatchingItemIdentifiers(inputField).toList();
				currentItem = new ItemOrTag(ItemRegistryHelper.getItemFromName(inputField, null));
				for (Identifier identifier : identifiers) {
					matchingItems.put(identifier, new ItemOrTag(Registries.ITEM.get(identifier)));
				}
			}
			return identifiers;
		}

		@Override
		protected void renderDropdownEntry(DrawContext graphics, Dimension<Integer> entryDimension, Identifier identifier) {
			super.renderDropdownEntry(graphics, entryDimension, identifier);
			graphics.drawItemWithoutEntity(
					new ItemStack(matchingItems.get(identifier).getAnyItem()),
					entryDimension.xLimit() - 2,
					entryDimension.y() + 1
			);
		}

		@Override
		public String getString(Identifier identifier) {
			// If we are filtering for item tags, show item tags
			if (inputField.startsWith("#")) {
				if (identifier.getNamespace().equals(Identifier.DEFAULT_NAMESPACE)) {
					return "#" + identifier.getPath();
				}
				return "#" + identifier;
			} else {
				return Registries.ITEM.get(identifier).toString();
			}
		}

		@Override
		protected int getDecorationPadding() {
			return 16;
		}

		@Override
		protected int getDropdownEntryPadding() {
			return 4;
		}

		@Override
		protected int getControlWidth() {
			return super.getControlWidth() + getDecorationPadding();
		}

		@Override
		protected Text getValueText() {
			if (inputField.isEmpty() || itemOrTagController == null)
				return super.getValueText();

			if (inputFieldFocused)
				return Text.literal(inputField);

			ItemOrTag itemOrTag = itemOrTagController.option.pendingValue();
			if (itemOrTag.isItemTag() && itemOrTag.id().getNamespace().equals(Identifier.DEFAULT_NAMESPACE)) {
				return Text.literal("#" + itemOrTag.id().getPath());
			}

			return itemOrTag.getName();
		}
	}
}
//?}
