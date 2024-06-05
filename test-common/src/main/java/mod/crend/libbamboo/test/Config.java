package mod.crend.libbamboo.test;

import dev.isxander.yacl3.config.v2.api.SerialEntry;
import mod.crend.libbamboo.type.BlockOrTag;
import mod.crend.libbamboo.type.ItemOrTag;
import mod.crend.libbamboo.render.ItemOrTagRenderer;
import mod.crend.libbamboo.auto.annotation.*;
import mod.crend.libbamboo.opt.ConfigStore;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Colors;

import java.awt.Color;
import java.util.List;
import java.util.Objects;

@AutoYaclConfig(modid = "libbambootest", translationKey = "libbamboo")
@SuppressWarnings("unused")
public class Config {
	public static final ConfigStore<Config> CONFIG_STORE = new ConfigStore<>(Config.class);

	@SerialEntry
	@OnSave(assetReload = true)
	@Label(key = "libbambootest.label")
	public boolean trivialOptions = true;

	@SerialEntry
	public boolean numericalRanges = true;

	@SerialEntry
	@TransitiveObject
	public Ranges ranges = new Ranges();

	@SerialEntry
	public int intValue = 17;

	@Order(value = {"doubleRange", "floatRange"})
	public static class Ranges {
		@SerialEntry
		@NumericRange(min = 0, max = 20, interval = 2)
		@EnableIf(field = "trivialOptions", value = EnableIf.BooleanPredicate.class)
		@EnableIf(field = "numericalRanges", value = EnableIf.BooleanPredicate.class)
		@Translation(key = "libbambootest.range")
		public int intRange = 10;

		@SerialEntry
		@NumericRange(min = 100, max = 1000, interval = 100)
		@EnableIf(field = "trivialOptions", value = EnableIf.BooleanPredicate.class)
		@EnableIf(field = "numericalRanges", value = EnableIf.BooleanPredicate.class)
		public long longRange = 0;

		@SerialEntry
		@FloatingPointRange(min = 2.0, max = 4.0, interval = 0.2)
		@EnableIf(field = "trivialOptions", value = EnableIf.BooleanPredicate.class)
		@EnableIf(field = "numericalRanges", value = EnableIf.BooleanPredicate.class)
		public double doubleRange = 5.4;

		@SerialEntry
		@FloatingPointRange(min = 0.4, max = 1.0, interval = 0.1)
		@EnableIf(field = "trivialOptions", value = EnableIf.BooleanPredicate.class)
		@EnableIf(field = "numericalRanges", value = EnableIf.BooleanPredicate.class)
		public float floatRange = 0.7f;
	}

	@SerialEntry
	public String string = "foo";

	@SerialEntry
	@StringOptions(options = {"Apple", "Banana", "Cherry", "Date"}, allowEmpty = true)
	@EnableIf(field = "trivialOptions", value = EnableIf.BooleanPredicate.class)
	public String stringOptions = "";

	@SerialEntry
	public Color color = new Color(0xFF0000);
	@SerialEntry
	public Color colorWithAlpha = new Color(0x00FF0080, true);

	@SerialEntry
	@Category(name = "general", group = "enums")
	@Listener(value = TestDescriptionImageRenderer.Callback.class)
	@DescriptionImage(value = TestDescriptionImageRenderer.Factory.class)
	public TestEnum testEnum = TestEnum.DEFAULT;

	@SerialEntry
	@Category(name = "general", group = "enums")
	@Decorate(decorator = TestEnumDecorator.class)
	public TestEnum decoratedEnum = TestEnum.MAGIC;

	public static class SubscreenObject {
		@SerialEntry
		public int integer = 0;

		@SerialEntry
		@Decorate(decorator = TestEnumDecorator.class)
		public TestEnum nestedEnum = TestEnum.MAGIC;

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			SubscreenObject that = (SubscreenObject) o;
			return integer == that.integer && nestedEnum == that.nestedEnum;
		}

		@Override
		public int hashCode() {
			return Objects.hash(integer, nestedEnum);
		}
	}

	@SerialEntry
	@Nesting("subscreen")
	public boolean enableNested = true;

	@SerialEntry
	@SubScreen
	@Nested
	public SubscreenObject subscreen = new SubscreenObject();


	@SerialEntry
	@Category(name = "item")
	@DescriptionImage(ItemOrTagRenderer.OfItem.class)
	public Item item = Items.BONE;

	@SerialEntry
	@Category(name = "item")
	@DescriptionImage(ItemOrTagRenderer.OfItemOrTag.class)
	public ItemOrTag itemOrTag = new ItemOrTag(Items.ARROW);

	@SerialEntry
	@Category(name = "item")
	@DescriptionImage(ItemOrTagRenderer.OfBlock.class)
	public Block block = Blocks.OAK_LOG;

	@SerialEntry
	@Category(name = "item")
	@DescriptionImage(ItemOrTagRenderer.OfBlockOrTag.class)
	public BlockOrTag blockOrTag = new BlockOrTag(Blocks.STRIPPED_BAMBOO_BLOCK);

	@SerialEntry
	@Category(name = "lists")
	@DescriptionImage(ItemOrTagRenderer.OfItem.class)
	@NumberOfItems(min = 3)
	public List<Item> itemList = List.of(Items.APPLE, Items.PUMPKIN_PIE, Items.CAKE, Items.COOKIE);

	@SerialEntry
	@Reverse
	@Category(name = "lists")
	@DescriptionImage(ItemOrTagRenderer.OfItem.class)
	@NumberOfItems(max = 6)
	public List<Item> reversedList = List.of(Items.APPLE, Items.PUMPKIN_PIE, Items.CAKE, Items.COOKIE);

	@SerialEntry
	@Category(name = "lists")
	@EnableIf(field = "trivialOptions", value = EnableIf.BooleanPredicate.class)
	@DescriptionImage(ItemOrTagRenderer.OfItemOrTag.class)
	public List<ItemOrTag> itemOrTagList = List.of(new ItemOrTag(Items.MINECART), new ItemOrTag(ItemTags.BOATS));

	@SerialEntry
	@Category(name = "lists")
	public List<Color> colorList = List.of(new Color(Colors.RED), new Color(Colors.BLACK));

	@SerialEntry
	@Category(name = "lists")
	@Decorate(decorator = TestEnumDecorator.class)
	public List<TestEnum> enumList = List.of(TestEnum.MAGIC, TestEnum.DIFFERENT);
}
