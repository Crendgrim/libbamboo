# YACLX: Yet Another Config Library Extensions

Adds some useful extensions to YACL that are not fitting to be included upstream.


## Useful Data Types

### ItemOrTag
Represents either an item, or an item tag. Has utility methods to obtain items from this identifier.
Serializes to an identifier for items, or the tag name prepended by a `#` for item tags.

### Serializers
A few (de)serializers (adapters) are provided that are used to support `Color`, `Item`, and `ItemOrTag` objects.


## Additional Controllers
There are four additional option controllers present:

### DecoratedEnumController
This controller works like a cycling enum controller, but allows adding a decorator that paints
a symbol based on the current enum value. This symbol is painted to the right of the enum value
text on the button.

### DropdownStringController
This controller enhances a regular String input box with a dropdown. It takes a list of allowed
values. If the text box is focused, a dropdown is shown. All values matching the current contents
of the text box are displayed. The dropdown box can be navigated using <arrow up> and <arrow down>.
On unfocusing the text box, the currently selected dropdown entry is inserted into the text box.
The contents of the input will always be restricted to the permitted values.

### ItemController
This dropdown controller works on an `Item` field, and presents like a String controller. Valid inputs
are the identifiers of items. Identifiers matching the current text content are shown in the dropdown.
On selecting any identifier, the corresponding item's sprite is drawn onto the controller.

### ItemOrTagController
This enhances the ItemController by also supporting item tags, using the `ItemOrTag` data type. String
input will be regarded as an item identifier unless it starts with a `#`, at which point it will be
parsed as an item tag identifier. The item, or one of the items of the item tag, will be rendered onto
the controller.


## Optional YACL
YaclX comes with an abstraction layer called `ConfigStore`. This layer will use YACL for deserialization
of the config if it is present, or use raw GSON if not. With this, the config file will always be parsed
and config values can be used without YACL being installed. However, no config GUI will be provided.

### NameableEnum
Enum classes used in config should implement `mod.crend.yaclx.type.NameableEnum` instead of
`dev.isxander.yacl3.api.NameableEnum` to allow parsing these values without YACL installed.

### ConfigUpdater
ConfigStore objects may be supplied with a `ConfigUpdater` object. This gets invoked on the config file
contents before they are parsed, and may thus be used to correct wrong data types of fields if these have
changed between mod versions.


## AutoYACL
AutoYACL is to YACL what AutoConfig is to ClothConfig. It allows for annotation-based GUI building.

Instead of specifying the binding, controller, description, etc. for every option, it simply parses the
annotations present on any `@SerialEntry` annotated field of the config.

Any field that is recognized as a supported data type gets an option controller. Other types will be
interpreted as organization classes containing further config entries, and parsed recursively. The option
will be considered a group on the upper level of categories, and its fields grouped that way. As YACL does
not support nested groups, any such object within a group will get its fields added to this group. To
enforce this behavior on the category level, you may use `@TransitiveObject`.

Supported data types are: `boolean`, `int`, `long`, `float`, `double`, `String`, `Enum<?>`, `Color`,
`Item`, `ItemOrTag` and `List<T>` where `T` is any of the above.

### `@AutoYaclConfig`
Main annotation, should be affixed to the config class. Sets up mod id and optionally translation and file
name for the config file. If the translation key is not provided, `<modid>.title` will be used. If no file
name is provided, `<modid>.json` will be used.

### `@Category`
Can be used to assign entries to categories and groups. The main category's key is `"general"`.
Translation entries for categories are `<modid>.category.<category name>`.
Optionally can take a `group=` key (translation entry `<modid>.group.<group name>`), grouping related
options in a collapsible group.

### `@Order`
Can be assigned to classes; either the base config class, or any classes used within. Takes a list of
Strings, where each String should be the name of a field. Fields get added to the option list in the order
they appear in this list, with any left over inserted after. This allows to manually tune the layout of
options.

### `@TransitiveObject`
Can be assigned to container fields. Will always elevate internal fields to the outer layer instead of
grouping them in a collapsible option group.

### `@Decorate`
Applicable to `Enum<?>` fields. Takes the class object of a default-constructible class implementing the
`DecoratedEnumController.Decorator<?>` interface. Can be used to draw a relevant icon onto the enum option
controller based on the current value.

### `@DescriptionImage`
Applicable to any option field. Takes the class object of a default-constructible class implementing the
`DescriptionImageRendererFactory<?>` factory interface. Can be used to supply an image renderer which uses
the current field value to add a relevant image to the description.

An exemplary renderer is provided as `ItemOrTagRenderer.Factory.class` for `ItemOrTag` values, rendering
all loaded items under that tag (Fabric only).

### `@EnableIf`
Applicable to any option field. Can be repeated. Takes a String which is the field name of another config
value, and the class object of a default-constructible class implementing the `EnableIf.Predicate`
interface. This predicate will be called with the other field's pending value. If any of the assigned
predicates return `false`, the annotated field will be disabled.

A built-in predicate `EnableIf.BooleanPredicate.class` is supplied that can be used to disable options
based on a boolean field.

### `@FloatingPointRange` and `@NumericRange`
Applicable to `float` and `double` (or `int` and `long`, respectively) fields. Can be used to restrict 
the valid values of this field. Takes minimum and maximum values as well as a step size. Fields annotated
with these annotations will get a sliding controller rather than an input one. The validator will accept
any values within this range, as well as the default config value.

### `@StringOptions`
Applicable to `String` fields. Can be used to supply a list of valid values for the option. These will
be rendered using a dropdown controller (see above for details).

### `@Label`
Applicable to any field. Will register a text label option with the supplied translation string before
the field gets added.

### `@Listener`
Applicable to any option field. Takes the class object of a default-constructible class implementing the
`Listener.Callback` interface. This callback will be invoked any time the pending option changes.

### `@OnSave`
Applicable to any option field. Can be used to supply flags that get invoked on saving the config if
these fields have changed. The following flags are supported:
- `gameRestart`: The user gets asked to restart their game for changes to take effect.
- `reloadChunks`: All chunks are reloaded.
- `worldRenderUpdate`: ALl chunks are re-rendered.
- `assetReload`: All resources are reloaded.

Each flag is only invoked once, regardless of how many modified options are annotated with the flag.

### `@Reverse`
Applicable to any `List<?>` field. Because YACL's list controller only supports adding at the top, this
can be used to reverse any lists in the GUI.

### `@Translation`
Applicable to any field. Can be used to overwrite the default translation key. The default key is
`<modid>.option.<fieldname>` for fields and `<modid>.group.<groupname>` for classes.

If the `description()` key is not supplied, the description will use, if it exists, the translation key
`<field translation key>.description`; or the field's translation key otherwise.


## ConfigValidator
Uses the range and option annotations to check all values of a config object for whether they are in
range. This gets called automatically when using AutoYACL, but may also be manually invoked.
