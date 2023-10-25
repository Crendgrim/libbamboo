package mod.crend.yaclx.render;

import dev.isxander.yacl3.gui.image.ImageRenderer;
import mod.crend.yaclx.type.BlockOrTag;
import mod.crend.yaclx.type.ItemOrTag;
import mod.crend.yaclx.auto.annotation.DescriptionImage;
import net.minecraft.block.Block;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ItemOrTagRenderer implements ImageRenderer {
	protected final List<Item> list = new ArrayList<>();

	public ItemOrTagRenderer(Collection<Item> list) {
		this.list.addAll(list);
	}
	public ItemOrTagRenderer(Item item) {
		this.list.add(item);
	}

	@Override
	public int render(DrawContext graphics, int x, int y, int renderWidth, float tickDelta) {
		int dx = 0, dy = 0;
		for (Item entry : list) {
			if (entry == Items.AIR) continue;
			graphics.drawItemWithoutEntity(new ItemStack(entry), x + dx, y + dy);
			dx += 20;
			if (dx + 20 > renderWidth) {
				dx = 0;
				dy += 20;
			}
		}

		return dy + 20;
	}

	@Override
	public void close() { }

	protected static class ItemListRenderer extends ItemOrTagRenderer implements ListImageRenderer<Item> {
		public ItemListRenderer() {
			super(List.of());
		}

		public void setList(List<Item> list) {
			this.list.clear();
			this.list.addAll(list);
		}
	}
	protected static class ItemOrTagListRenderer extends ItemOrTagRenderer implements ListImageRenderer<ItemOrTag> {
		public ItemOrTagListRenderer() {
			super(List.of());
		}

		public void setList(List<ItemOrTag> list) {
			this.list.clear();
			for (ItemOrTag itemOrTag : list) {
				this.list.addAll(itemOrTag.getAllItems());
			}
		}
	}

	protected static class BlockListRenderer extends ItemOrTagRenderer implements ListImageRenderer<Block> {
		public BlockListRenderer() {
			super(List.of());
		}

		public void setList(List<Block> list) {
			this.list.clear();
			this.list.addAll(list.stream().map(Block::asItem).toList());
		}
	}
	protected static class BlockOrTagListRenderer extends ItemOrTagRenderer implements ListImageRenderer<BlockOrTag> {
		public BlockOrTagListRenderer() {
			super(List.of());
		}

		public void setList(List<BlockOrTag> list) {
			this.list.clear();
			for (BlockOrTag blockOrTag : list) {
				this.list.addAll(blockOrTag.getAllBlocks().stream().map(Block::asItem).toList());
			}
		}
	}


	public static class OfItem implements DescriptionImage.DescriptionImageRendererFactory<Item> {
		@Override
		public ImageRenderer create(Item value) {
			if (value == null) return new ItemListRenderer();
			return new ItemOrTagRenderer(value);
		}
	}
	public static class OfItemOrTag implements DescriptionImage.DescriptionImageRendererFactory<ItemOrTag> {
		@Override
		public ImageRenderer create(ItemOrTag value) {
			if (value == null) return new ItemOrTagListRenderer();
			return new ItemOrTagRenderer(value.getAllItems());
		}
	}
	public static class OfBlock implements DescriptionImage.DescriptionImageRendererFactory<Block> {
		@Override
		public ImageRenderer create(Block value) {
			if (value == null) return new BlockListRenderer();
			return new ItemOrTagRenderer(value.asItem());
		}
	}
	public static class OfBlockOrTag implements DescriptionImage.DescriptionImageRendererFactory<BlockOrTag> {
		@Override
		public ImageRenderer create(BlockOrTag value) {
			if (value == null) return new BlockOrTagListRenderer();
			return new ItemOrTagRenderer(value.getAllBlocks().stream().map(Block::asItem).toList());
		}
	}
}
