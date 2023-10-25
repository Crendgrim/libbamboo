package mod.crend.yaclx.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.dropdown.AbstractDropdownController;
import dev.isxander.yacl3.gui.controllers.dropdown.AbstractDropdownControllerElement;
import dev.isxander.yacl3.impl.controller.AbstractControllerBuilderImpl;
import mod.crend.yaclx.BlockRegistryHelper;
import mod.crend.yaclx.type.BlockOrTag;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Simple controller that simply runs the button action on press
 * and renders a {@link} Text on the right.
 */
public class BlockOrTagController extends AbstractDropdownController<BlockOrTag> {

	/**
	 * Constructs a block controller
	 *
	 * @param option bound option
	 */
	public BlockOrTagController(Option<BlockOrTag> option) {
		super(option);
	}

	static Stream<Identifier> getMatchingBlockTagIdentifiers(String value) {
		int sep = value.indexOf(Identifier.NAMESPACE_SEPARATOR);
		Predicate<TagKey<Block>> filterPredicate;
		if (sep == -1) {
			filterPredicate = tagKey ->
					tagKey.id().getPath().contains(value)
							|| tagKey.id().toString().toLowerCase().contains(value.toLowerCase());
		} else {
			String namespace = value.substring(0, sep);
			String path = value.substring(sep + 1);
			filterPredicate = tagKey -> tagKey.id().getNamespace().equals(namespace) && tagKey.id().getPath().startsWith(path);
		}
		return BlockOrTag.getBlockTags().stream()
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
		BlockOrTag.fromString(value, false).ifPresent(option::requestSet);
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
			return BlockOrTag.isBlockTag(value.substring(1));
		} else {
			return BlockRegistryHelper.isRegisteredBlock(value);
		}
	}

	@Override
	protected String getValidValue(String value, int offset) {
		if (value.startsWith("#")) {
			return value;
		} else {
			return BlockRegistryHelper.getMatchingBlockIdentifiers(value)
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
		return new BlockOrTagControllerElement(this, screen, widgetDimension);
	}

	public interface BlockOrTagControllerBuilder extends ControllerBuilder<BlockOrTag> {
		static BlockOrTagControllerBuilderImpl create(Option<BlockOrTag> option) {
			return new BlockOrTagControllerBuilderImpl(option);
		}
	}

	public static class BlockOrTagControllerBuilderImpl extends AbstractControllerBuilderImpl<BlockOrTag> implements BlockOrTagControllerBuilder {
		protected BlockOrTagControllerBuilderImpl(Option<BlockOrTag> option) {
			super(option);
		}

		@Override
		public Controller<BlockOrTag> build() {
			return new BlockOrTagController(option);
		}
	}

	public static class BlockOrTagControllerElement extends AbstractDropdownControllerElement<BlockOrTag, Identifier> {
		private final BlockOrTagController blockOrTagController;

		public BlockOrTagControllerElement(BlockOrTagController control, YACLScreen screen, Dimension<Integer> dim) {
			super(control, screen, dim);
			this.blockOrTagController = control;
		}

		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			if (inputFieldFocused && dropdownVisible && keyCode == InputUtil.GLFW_KEY_ENTER) {
				if (inputField.startsWith("#") && getDropdownLength() > 0) {
					inputField = getMatchingBlockTagIdentifiers(inputField.substring(1))
							.skip(selectedIndex)
							.findFirst()
							.map(id -> "#" + id)
							.orElseGet(blockOrTagController::getString);
					caretPos = getDefaultCaretPos();
					updateControl();
				}
			}
			return super.keyPressed(keyCode, scanCode, modifiers);
		}

		@Override
		public int getDropdownLength() {
			if (inputField.startsWith("#")) {
				return (int) getMatchingBlockTagIdentifiers(inputField.substring(1)).count();
			} else {
				return (int) BlockRegistryHelper.getMatchingBlockIdentifiers(inputField).count();
			}
		}

		@Override
		protected void drawValueText(DrawContext graphics, int mouseX, int mouseY, float delta) {
			var oldDimension = getDimension();
			setDimension(getDimension().withWidth(getDimension().width() - getDecorationPadding()));
			super.drawValueText(graphics, mouseX, mouseY, delta);
			setDimension(oldDimension);
			BlockOrTag.fromString(inputField, true)
					.ifPresent(blockOrTag -> graphics.drawItemWithoutEntity(
							new ItemStack(blockOrTag.getAnyBlock()),
							getDimension().xLimit() - getXPadding() - getDecorationPadding() + 2,
							getDimension().y() + 2)
					);
		}

		@Override
		public List<Identifier> computeMatchingValues() {
			if (inputField.startsWith("#")) {
				return getMatchingBlockTagIdentifiers(inputField.substring(1)).toList();
			} else {
				return BlockRegistryHelper.getMatchingBlockIdentifiers(inputField).toList();
			}
		}

		@Override
		public String getString(Identifier identifier) {
			// If we are filtering for block tags, show block tags
			if (inputField.startsWith("#")) {
				if (identifier.getNamespace().equals(Identifier.DEFAULT_NAMESPACE)) {
					return "#" + identifier.getPath();
				}
				return "#" + identifier;
			} else {
				if (identifier.getNamespace().equals(Identifier.DEFAULT_NAMESPACE)) {
					return identifier.getPath();
				}
				return identifier.toString();
			}
		}

		@Override
		protected void renderDropdownEntry(DrawContext graphics, Identifier identifier, int n) {
			super.renderDropdownEntry(graphics, identifier, n);
			Block block;
			if (inputField.startsWith("#")) {
				TagKey<Block> tagKey = TagKey.of(RegistryKeys.BLOCK, identifier);
				block = new BlockOrTag(tagKey).getAnyBlock();
			} else {
				block = Registries.BLOCK.get(identifier);
			}
			graphics.drawItemWithoutEntity(new ItemStack(block), getDimension().xLimit() - getDecorationPadding() - 2, getDimension().y() + n * getDimension().height() + 4);
		}

		@Override
		protected int getControlWidth() {
			return super.getControlWidth() + getDecorationPadding();
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
		protected Text getValueText() {
			if (inputField.isEmpty() || blockOrTagController == null)
				return super.getValueText();

			if (inputFieldFocused)
				return Text.literal(inputField);

			BlockOrTag blockOrTag = blockOrTagController.option.pendingValue();
			if (blockOrTag.isBlockTag() && blockOrTag.id().getNamespace().equals(Identifier.DEFAULT_NAMESPACE)) {
				return Text.literal("#" + blockOrTag.id().getPath());
			}
			if (blockOrTag.isBlock() && blockOrTag.getBlock().equals(Blocks.AIR)) {
				return Text.empty();
			}

			return blockOrTag.getName();
		}
	}
}
