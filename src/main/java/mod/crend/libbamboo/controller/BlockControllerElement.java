//? if yacl {
package mod.crend.libbamboo.controller;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.dropdown.AbstractDropdownControllerElement;
import mod.crend.libbamboo.BlockRegistryHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockControllerElement extends AbstractDropdownControllerElement<Block, Identifier> {
	private final BlockController blockController;
	protected Block currentBlock = null;
	protected Map<Identifier, Block> matchingBlocks = new HashMap<>();


	public BlockControllerElement(BlockController control, YACLScreen screen, Dimension<Integer> dim) {
		super(control, screen, dim);
		this.blockController = control;
	}

	@Override
	protected void drawValueText(DrawContext graphics, int mouseX, int mouseY, float delta) {
		var oldDimension = getDimension();
		setDimension(getDimension().withWidth(getDimension().width() - getDecorationPadding()));
		super.drawValueText(graphics, mouseX, mouseY, delta);
		setDimension(oldDimension);
		if (currentBlock != null) {
			graphics.drawItemWithoutEntity(new ItemStack(currentBlock), getDimension().xLimit() - getXPadding() - getDecorationPadding() + 2, getDimension().y() + 2);
		}
	}

	@Override
	public List<Identifier> computeMatchingValues() {
		List<Identifier> identifiers = BlockRegistryHelper.getMatchingBlockIdentifiers(inputField).toList();
		currentBlock = BlockRegistryHelper.getBlockFromName(inputField, null);
		for (Identifier identifier : identifiers) {
			matchingBlocks.put(identifier, Registries.BLOCK.get(identifier));
		}
		return identifiers;
	}

	@Override
	protected void renderDropdownEntry(DrawContext graphics, Dimension<Integer> entryDimension, Identifier identifier) {
		super.renderDropdownEntry(graphics, entryDimension, identifier);
		graphics.drawItemWithoutEntity(
				new ItemStack(matchingBlocks.get(identifier)),
				entryDimension.xLimit() - 2,
				entryDimension.y() + 1
		);
	}

	@Override
	public String getString(Identifier identifier) {
		if (identifier.getNamespace().equals(Identifier.DEFAULT_NAMESPACE)) {
			return identifier.getPath();
		}
		return identifier.toString();
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
		if (inputField.isEmpty() || blockController == null)
			return super.getValueText();

		if (inputFieldFocused)
			return Text.literal(inputField);

		Block pendingValue = blockController.option().pendingValue();
		if (pendingValue.equals(Blocks.AIR)) {
			return Text.empty();
		}
		return pendingValue.getName();
	}
}
//?}
