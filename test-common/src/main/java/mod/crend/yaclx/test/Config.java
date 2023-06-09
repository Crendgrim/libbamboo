package mod.crend.yaclx.test;

import dev.isxander.yacl3.config.ConfigEntry;
import mod.crend.yaclx.type.ItemOrTag;
import mod.crend.yaclx.render.ItemOrTagRenderer;
import mod.crend.yaclx.auto.annotation.*;
import mod.crend.yaclx.opt.ConfigStore;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Colors;

import java.awt.Color;
import java.util.List;

@AutoYaclConfig(modid = "yaclxtest", translationKey = "yaclx")
@SuppressWarnings("unused")
public class Config {
	public static final ConfigStore<Config> CONFIG_STORE = new ConfigStore<>(Config.class);

	@ConfigEntry
	@OnSave(assetReload = true)
	@Label(key = "yaclxtest.label")
	public boolean trivialOptions = true;

	@ConfigEntry
	public boolean numericalRanges = true;

	@ConfigEntry
	@TransitiveObject
	public Ranges ranges = new Ranges();

	@ConfigEntry
	public int intValue = 17;

	@Order(value = {"doubleRange", "floatRange"})
	public static class Ranges {
		@ConfigEntry
		@NumericRange(min = 0, max = 20, interval = 2)
		@EnableIf(field = "trivialOptions", value = EnableIf.BooleanPredicate.class)
		@EnableIf(field = "numericalRanges", value = EnableIf.BooleanPredicate.class)
		@Translation(key = "yaclxtest.range")
		public int intRange = 10;

		@ConfigEntry
		@NumericRange(min = 100, max = 1000, interval = 100)
		@EnableIf(field = "trivialOptions", value = EnableIf.BooleanPredicate.class)
		@EnableIf(field = "numericalRanges", value = EnableIf.BooleanPredicate.class)
		public long longRange = 0;

		@ConfigEntry
		@FloatingPointRange(min = 2.0, max = 4.0, interval = 0.2)
		@EnableIf(field = "trivialOptions", value = EnableIf.BooleanPredicate.class)
		@EnableIf(field = "numericalRanges", value = EnableIf.BooleanPredicate.class)
		public double doubleRange = 5.4;

		@ConfigEntry
		@FloatingPointRange(min = 0.4, max = 1.0, interval = 0.1)
		@EnableIf(field = "trivialOptions", value = EnableIf.BooleanPredicate.class)
		@EnableIf(field = "numericalRanges", value = EnableIf.BooleanPredicate.class)
		public float floatRange = 0.7f;
	}

	@ConfigEntry
	public String string = "foo";

	@ConfigEntry
	@StringOptions(options = {"Apple", "Banana", "Cherry", "Date"}, allowEmpty = true)
	@EnableIf(field = "trivialOptions", value = EnableIf.BooleanPredicate.class)
	public String stringOptions = "";

	@ConfigEntry
	public Color color = new Color(0xFF0000);
	@ConfigEntry
	public Color colorWithAlpha = new Color(0x00FF0080, true);

	@ConfigEntry
	@Category(name = "general", group = "enums")
	@Listener(value = TestDescriptionImageRenderer.Callback.class)
	@DescriptionImage(value = TestDescriptionImageRenderer.Factory.class)
	public TestEnum testEnum = TestEnum.DEFAULT;

	@ConfigEntry
	@Category(name = "general", group = "enums")
	@Decorate(decorator = TestEnumDecorator.class)
	public TestEnum decoratedEnum = TestEnum.MAGIC;

	@ConfigEntry
	@Category(name = "item")
	@DescriptionImage(ItemOrTagRenderer.OfItem.class)
	public Item item = Items.BONE;

	@ConfigEntry
	@Category(name = "item")
	@DescriptionImage(ItemOrTagRenderer.OfItemOrTag.class)
	public ItemOrTag itemOrTag = new ItemOrTag(Items.ARROW);

	@ConfigEntry
	@Category(name = "lists")
	@DescriptionImage(ItemOrTagRenderer.OfItem.class)
	public List<Item> itemList = List.of(Items.APPLE, Items.PUMPKIN_PIE, Items.CAKE, Items.COOKIE);

	@ConfigEntry
	@Reverse
	@Category(name = "lists")
	@DescriptionImage(ItemOrTagRenderer.OfItem.class)
	public List<Item> reversedList = List.of(Items.APPLE, Items.PUMPKIN_PIE, Items.CAKE, Items.COOKIE);

	@ConfigEntry
	@Category(name = "lists")
	@EnableIf(field = "trivialOptions", value = EnableIf.BooleanPredicate.class)
	@DescriptionImage(ItemOrTagRenderer.OfItemOrTag.class)
	public List<ItemOrTag> itemOrTagList = List.of(new ItemOrTag(Items.MINECART), new ItemOrTag(ItemTags.BOATS));

	@ConfigEntry
	@Category(name = "lists")
	public List<Color> colorList = List.of(new Color(Colors.RED), new Color(Colors.BLACK));
}
