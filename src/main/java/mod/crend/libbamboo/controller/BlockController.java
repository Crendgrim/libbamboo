//? if yacl {
package mod.crend.libbamboo.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.dropdown.AbstractDropdownController;
import dev.isxander.yacl3.impl.controller.AbstractControllerBuilderImpl;
import mod.crend.libbamboo.BlockRegistryHelper;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BlockController extends AbstractDropdownController<Block> {

	/**
	 * Constructs a block controller
	 *
	 * @param option bound option
	 */
	public BlockController(Option<Block> option) {
		super(option);
	}

	@Override
	public String getString() {
		return Registries.BLOCK.getId(option.pendingValue()).toString();
	}

	@Override
	public void setFromString(String value) {
		option.requestSet(BlockRegistryHelper.getBlockFromName(value, option.pendingValue()));
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
		return BlockRegistryHelper.isRegisteredBlock(value);
	}

	@Override
	protected String getValidValue(String value, int offset) {
		return BlockRegistryHelper.getMatchingBlockIdentifiers(value)
				.skip(offset)
				.findFirst()
				.map(Identifier::toString)
				.orElseGet(this::getString);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
		return new BlockControllerElement(this, screen, widgetDimension);
	}

	public interface BlockControllerBuilder extends ControllerBuilder<Block> {
		static BlockController.BlockControllerBuilderImpl create(Option<Block> option) {
			return new BlockController.BlockControllerBuilderImpl(option);
		}
	}

	public static class BlockControllerBuilderImpl extends AbstractControllerBuilderImpl<Block> implements BlockController.BlockControllerBuilder {
		protected BlockControllerBuilderImpl(Option<Block> option) {
			super(option);
		}

		@Override
		public Controller<Block> build() {
			return new BlockController(option);
		}
	}
}
//?}
