# Expresiones Java de recetas de Rustic 1.2.0

Generado por `python3 tools/recipe_audit.py` a partir del commit
`88597f5ca4342a6b9d4509d93967ecbef6f8bafa`. **Ninguna está verificada en juego.**

Se conservan literalmente los argumentos completos de las llamadas y constructores
seleccionados en `Recipes.java`, incluidos espacios significativos en patrones. Se listan
expresiones fuente, no instancias finales: los bucles y condicionales pueden generar cero o
varias recetas; el cuerpo de una clase anónima, el registro exterior, las variables previas,
el orden y los efectos de integraciones no se incluyen en el recorte. Las condiciones y el
comportamiento deben verificarse en el archivo original enlazado. En particular, la receta
anónima de ambrosía sobrescribe métodos de calidad. Hay tres correspondencias iniciadas,
aún sin verificación en juego; consultar el registro `audit/recipe-parity.json`.
No se indexan los JSON (véase `RUSTIC_RECIPES.md`), ni recetas externas de Forestry,
Dynamic Trees o CraftTweaker, ni se interpreta Java. Esto no demuestra cobertura total.

## Resumen de expresiones detectadas

| Expresión | Cantidad | Estado |
|---|---:|---|
| `GameRegistry.addShapedRecipe` | 31 | Sin verificar |
| `GameRegistry.addShapelessRecipe` | 2 | Sin verificar |
| `GameRegistry.addSmelting` | 5 | Sin verificar |
| `new AdvancedCondenserRecipe` | 26 | Sin verificar |
| `new BasicCondenserRecipe` | 5 | Sin verificar |
| `new BrewingBarrelRecipe` | 8 | Sin verificar |
| `new CrushingTubRecipe` | 13 | Sin verificar |
| `new EvaporatingBasinRecipe` | 2 | Sin verificar |
| `new RecipeCabinet` | 1 | Sin verificar |
| `new RecipeNonIngredientReturn` | 3 | Sin verificar |
| `new RecipeOliveOil` | 1 | Sin verificar |
| `new RecipeVantaOil` | 1 | Sin verificar |
| `new ShapedOreRecipe` | 25 | Sin verificar |

Total: **123 expresiones fuente**, no recetas expandidas.

## Expresiones originales

### GameRegistry.addSmelting — línea 176

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L176) · Fabric: **en curso, sin probar**

```java
GameRegistry.addSmelting(new ItemStack(ModItems.HONEYCOMB), new ItemStack(ModItems.BEESWAX), 0.3F)
```

### GameRegistry.addSmelting — línea 181

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L181) · Fabric: **pendiente**

```java
GameRegistry.addSmelting(new ItemStack(ModBlocks.LOG, 1, meta), new ItemStack(Items.COAL, 1, 1), 0.15F)
```

### GameRegistry.addSmelting — línea 183

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L183) · Fabric: **en curso, sin probar**

```java
GameRegistry.addSmelting(new ItemStack(ModItems.IRON_DUST_TINY), new ItemStack(Items.IRON_NUGGET), 0.15F)
```

### GameRegistry.addSmelting — línea 184

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L184) · Fabric: **en curso, sin probar**

```java
GameRegistry.addSmelting(new ItemStack(ModItems.GOLD_DUST), new ItemStack(Items.GOLD_INGOT), 0.5F)
```

### GameRegistry.addSmelting — línea 186

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L186) · Fabric: **pendiente**

```java
GameRegistry.addSmelting(new ItemStack(Items.ROTTEN_FLESH), new ItemStack(ModItems.TALLOW), 0.3F)
```

### GameRegistry.addShapedRecipe — línea 299

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L299) · Fabric: **pendiente**

```java
GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "pillar_stone"), null, new ItemStack(ModBlocks.STONE_PILLAR, 6), "SS ", "SS ", "SS ", 'S',
					new ItemStack(Blocks.STONE, 1, 0))
```

### GameRegistry.addShapedRecipe — línea 301

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L301) · Fabric: **pendiente**

```java
GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "pillar_andesite"), null, new ItemStack(ModBlocks.ANDESITE_PILLAR, 6), "SS ", "SS ", "SS ", 'S',
					new ItemStack(Blocks.STONE, 1, 5))
```

### GameRegistry.addShapedRecipe — línea 303

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L303) · Fabric: **pendiente**

```java
GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "pillar_diorite"), null, new ItemStack(ModBlocks.DIORITE_PILLAR, 6), "SS ", "SS ", "SS ", 'S',
					new ItemStack(Blocks.STONE, 1, 3))
```

### GameRegistry.addShapedRecipe — línea 305

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L305) · Fabric: **pendiente**

```java
GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "pillar_granite"), null, new ItemStack(ModBlocks.GRANITE_PILLAR, 6), "SS ", "SS ", "SS ", 'S',
					new ItemStack(Blocks.STONE, 1, 1))
```

### GameRegistry.addShapedRecipe — línea 308

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L308) · Fabric: **pendiente**

```java
GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "pillar_slate"), null, new ItemStack(ModBlocks.SLATE_PILLAR, 6), "SS", "SS", "SS", 'S',
						new ItemStack(ModBlocks.SLATE))
```

### GameRegistry.addShapedRecipe — línea 313

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L313) · Fabric: **pendiente**

```java
GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "slate_roof"), null, new ItemStack(ModBlocks.SLATE_ROOF, 4), "SS", "SS", 'S',
					new ItemStack(ModBlocks.SLATE))
```

### GameRegistry.addShapedRecipe — línea 315

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L315) · Fabric: **pendiente**

```java
GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "slate_roof_stairs"), null, new ItemStack(ModBlocks.SLATE_ROOF_STAIRS, 4), "S  ", "SS ", "SSS", 'S',
					new ItemStack(ModBlocks.SLATE_ROOF))
```

### GameRegistry.addShapedRecipe — línea 317

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L317) · Fabric: **pendiente**

```java
GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "slate_roof_slab"), null, new ItemStack(ModBlocks.SLATE_ROOF_SLAB_ITEM, 6), "SSS", 'S',
					new ItemStack(ModBlocks.SLATE_ROOF))
```

### GameRegistry.addShapedRecipe — línea 319

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L319) · Fabric: **pendiente**

```java
GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "slate_brick_stairs"), null, new ItemStack(ModBlocks.SLATE_BRICK_STAIRS, 4), "S  ", "SS ", "SSS", 'S',
					new ItemStack(ModBlocks.SLATE_BRICK))
```

### GameRegistry.addShapedRecipe — línea 321

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L321) · Fabric: **pendiente**

```java
GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "slate_brick_slab"), null, new ItemStack(ModBlocks.SLATE_BRICK_SLAB_ITEM, 6), "SSS", 'S',
					new ItemStack(ModBlocks.SLATE_BRICK))
```

### GameRegistry.addShapedRecipe — línea 323

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L323) · Fabric: **pendiente**

```java
GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "slate_tile"), null, new ItemStack(ModBlocks.SLATE_TILE), "S", 'S',
					new ItemStack(ModBlocks.SLATE))
```

### GameRegistry.addShapedRecipe — línea 325

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L325) · Fabric: **pendiente**

```java
GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "slate_tile_1"), null, new ItemStack(ModBlocks.SLATE_TILE), "S", 'S',
					new ItemStack(ModBlocks.SLATE_BRICK))
```

### GameRegistry.addShapedRecipe — línea 327

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L327) · Fabric: **pendiente**

```java
GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "slate_brick"), null, new ItemStack(ModBlocks.SLATE_BRICK, 4), "SS", "SS", 'S',
					new ItemStack(ModBlocks.SLATE_TILE))
```

### GameRegistry.addShapedRecipe — línea 329

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L329) · Fabric: **pendiente**

```java
GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "slate_chiseled"), null, new ItemStack(ModBlocks.SLATE_CHISELED, 4), "SS", "SS", 'S',
					new ItemStack(ModBlocks.SLATE_BRICK))
```

### new ShapedOreRecipe — línea 333

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L333) · Fabric: **pendiente**

```java
new ShapedOreRecipe(null, new ItemStack(ModBlocks.CLAY_WALL, 8), " P ", "PCP", " P ", 'P',
					"plankWood", 'C', Blocks.CLAY)
```

### new ShapedOreRecipe — línea 335

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L335) · Fabric: **pendiente**

```java
new ShapedOreRecipe(null, new ItemStack(ModBlocks.CLAY_WALL_CROSS), "P P", " C ", "P P",
					'P', "plankWood", 'C', ModBlocks.CLAY_WALL)
```

### new ShapedOreRecipe — línea 337

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L337) · Fabric: **pendiente**

```java
new ShapedOreRecipe(null, new ItemStack(ModBlocks.CLAY_WALL_DIAG), "P  ", " C ", "  P",
					'P', "plankWood", 'C', ModBlocks.CLAY_WALL)
```

### GameRegistry.addShapedRecipe — línea 341

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L341) · Fabric: **pendiente**

```java
GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "acacia_chair"), new ResourceLocation(Rustic.MODID, "chair"), new ItemStack(ModBlocks.CHAIR_ACACIA, 4), "P  ", "PPP", "S S", 'P',
					new ItemStack(Blocks.PLANKS, 1, 4), 'S', new ItemStack(Items.STICK))
```

### GameRegistry.addShapedRecipe — línea 343

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L343) · Fabric: **pendiente**

```java
GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "big_oak_chair"), new ResourceLocation(Rustic.MODID, "chair"), new ItemStack(ModBlocks.CHAIR_BIG_OAK, 4), "P  ", "PPP", "S S", 'P',
					new ItemStack(Blocks.PLANKS, 1, 5), 'S', new ItemStack(Items.STICK))
```

### GameRegistry.addShapedRecipe — línea 345

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L345) · Fabric: **pendiente**

```java
GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "birch_chair"), new ResourceLocation(Rustic.MODID, "chair"), new ItemStack(ModBlocks.CHAIR_BIRCH, 4), "P  ", "PPP", "S S", 'P',
					new ItemStack(Blocks.PLANKS, 1, 2), 'S', new ItemStack(Items.STICK))
```

### GameRegistry.addShapedRecipe — línea 347

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L347) · Fabric: **pendiente**

```java
GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "jungle_chair"), new ResourceLocation(Rustic.MODID, "chair"), new ItemStack(ModBlocks.CHAIR_JUNGLE, 4), "P  ", "PPP", "S S", 'P',
					new ItemStack(Blocks.PLANKS, 1, 3), 'S', new ItemStack(Items.STICK))
```

### GameRegistry.addShapedRecipe — línea 349

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L349) · Fabric: **pendiente**

```java
GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "oak_chair"), new ResourceLocation(Rustic.MODID, "chair"), new ItemStack(ModBlocks.CHAIR_OAK, 4), "P  ", "PPP", "S S", 'P',
					new ItemStack(Blocks.PLANKS, 1, 0), 'S', new ItemStack(Items.STICK))
```

### GameRegistry.addShapedRecipe — línea 351

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L351) · Fabric: **pendiente**

```java
GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "spruce_chair"), new ResourceLocation(Rustic.MODID, "chair"), new ItemStack(ModBlocks.CHAIR_SPRUCE, 4), "P  ", "PPP", "S S", 'P',
					new ItemStack(Blocks.PLANKS, 1, 1), 'S', new ItemStack(Items.STICK))
```

### GameRegistry.addShapedRecipe — línea 353

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L353) · Fabric: **pendiente**

```java
GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "olive_chair"), new ResourceLocation(Rustic.MODID, "chair"), new ItemStack(ModBlocks.CHAIR_OLIVE, 4), "p  ", "ppp", "s s", 'p',
					new ItemStack(ModBlocks.PLANKS, 1, BlockPlanksRustic.EnumType.OLIVE.getMetadata()), 's',
					new ItemStack(Items.STICK))
```

### GameRegistry.addShapedRecipe — línea 356

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L356) · Fabric: **pendiente**

```java
GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "ironwood_chair"), new ResourceLocation(Rustic.MODID, "chair"), new ItemStack(ModBlocks.CHAIR_IRONWOOD, 4), "p  ", "ppp", "s s", 'p',
					new ItemStack(ModBlocks.PLANKS, 1, BlockPlanksRustic.EnumType.IRONWOOD.getMetadata()), 's',
					new ItemStack(Items.STICK))
```

### GameRegistry.addShapedRecipe — línea 361

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L361) · Fabric: **pendiente**

```java
GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "acacia_table"), new ResourceLocation(Rustic.MODID, "table"), new ItemStack(ModBlocks.TABLE_ACACIA, 2), "PPP", "S S", 'P',
					new ItemStack(Blocks.PLANKS, 1, 4), 'S', new ItemStack(Items.STICK))
```

### GameRegistry.addShapedRecipe — línea 363

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L363) · Fabric: **pendiente**

```java
GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "big_oak_table"), new ResourceLocation(Rustic.MODID, "table"), new ItemStack(ModBlocks.TABLE_BIG_OAK, 2), "PPP", "S S", 'P',
					new ItemStack(Blocks.PLANKS, 1, 5), 'S', new ItemStack(Items.STICK))
```

### GameRegistry.addShapedRecipe — línea 365

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L365) · Fabric: **pendiente**

```java
GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "birch_table"), new ResourceLocation(Rustic.MODID, "table"), new ItemStack(ModBlocks.TABLE_BIRCH, 2), "PPP", "S S", 'P',
					new ItemStack(Blocks.PLANKS, 1, 2), 'S', new ItemStack(Items.STICK))
```

### GameRegistry.addShapedRecipe — línea 367

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L367) · Fabric: **pendiente**

```java
GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "jungle_table"), new ResourceLocation(Rustic.MODID, "table"), new ItemStack(ModBlocks.TABLE_JUNGLE, 2), "PPP", "S S", 'P',
					new ItemStack(Blocks.PLANKS, 1, 3), 'S', new ItemStack(Items.STICK))
```

### GameRegistry.addShapedRecipe — línea 369

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L369) · Fabric: **pendiente**

```java
GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "oak_table"), new ResourceLocation(Rustic.MODID, "table"), new ItemStack(ModBlocks.TABLE_OAK, 2), "PPP", "S S", 'P',
					new ItemStack(Blocks.PLANKS, 1, 0), 'S', new ItemStack(Items.STICK))
```

### GameRegistry.addShapedRecipe — línea 371

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L371) · Fabric: **pendiente**

```java
GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "spruce_table"), new ResourceLocation(Rustic.MODID, "table"), new ItemStack(ModBlocks.TABLE_SPRUCE, 2), "PPP", "S S", 'P',
					new ItemStack(Blocks.PLANKS, 1, 1), 'S', new ItemStack(Items.STICK))
```

### GameRegistry.addShapedRecipe — línea 373

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L373) · Fabric: **pendiente**

```java
GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "olive_table"), new ResourceLocation(Rustic.MODID, "table"), new ItemStack(ModBlocks.TABLE_OLIVE, 2), "ppp", "s s", 'p',
					new ItemStack(ModBlocks.PLANKS, 1, BlockPlanksRustic.EnumType.OLIVE.getMetadata()), 's',
					new ItemStack(Items.STICK))
```

### GameRegistry.addShapedRecipe — línea 376

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L376) · Fabric: **pendiente**

```java
GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "ironwood_table"), new ResourceLocation(Rustic.MODID, "table"), new ItemStack(ModBlocks.TABLE_IRONWOOD, 2), "ppp", "s s", 'p',
					new ItemStack(ModBlocks.PLANKS, 1, BlockPlanksRustic.EnumType.IRONWOOD.getMetadata()), 's',
					new ItemStack(Items.STICK))
```

### GameRegistry.addShapedRecipe — línea 381

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L381) · Fabric: **pendiente**

```java
GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "iron_lattice"), null, new ItemStack(ModBlocks.IRON_LATTICE, 16), " I ", "III", " I ", 'I',
					new ItemStack(Items.IRON_INGOT))
```

### new ShapedOreRecipe — línea 385

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L385) · Fabric: **pendiente**

```java
new ShapedOreRecipe(null, new ItemStack(ModBlocks.PAINTED_WOOD_WHITE, 8), "PPP", "PDP",
					"PPP", 'P', "plankWood", 'D', "dyeWhite")
```

### new ShapedOreRecipe — línea 387

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L387) · Fabric: **pendiente**

```java
new ShapedOreRecipe(null, new ItemStack(ModBlocks.PAINTED_WOOD_ORANGE, 8), "PPP", "PDP",
					"PPP", 'P', "plankWood", 'D', "dyeOrange")
```

### new ShapedOreRecipe — línea 389

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L389) · Fabric: **pendiente**

```java
new ShapedOreRecipe(null, new ItemStack(ModBlocks.PAINTED_WOOD_MAGENTA, 8), "PPP", "PDP",
					"PPP", 'P', "plankWood", 'D', "dyeMagenta")
```

### new ShapedOreRecipe — línea 391

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L391) · Fabric: **pendiente**

```java
new ShapedOreRecipe(null, new ItemStack(ModBlocks.PAINTED_WOOD_LIGHT_BLUE, 8), "PPP",
					"PDP", "PPP", 'P', "plankWood", 'D', "dyeLightBlue")
```

### new ShapedOreRecipe — línea 393

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L393) · Fabric: **pendiente**

```java
new ShapedOreRecipe(null, new ItemStack(ModBlocks.PAINTED_WOOD_YELLOW, 8), "PPP", "PDP",
					"PPP", 'P', "plankWood", 'D', "dyeYellow")
```

### new ShapedOreRecipe — línea 395

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L395) · Fabric: **pendiente**

```java
new ShapedOreRecipe(null, new ItemStack(ModBlocks.PAINTED_WOOD_LIME, 8), "PPP", "PDP",
					"PPP", 'P', "plankWood", 'D', "dyeLime")
```

### new ShapedOreRecipe — línea 397

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L397) · Fabric: **pendiente**

```java
new ShapedOreRecipe(null, new ItemStack(ModBlocks.PAINTED_WOOD_PINK, 8), "PPP", "PDP",
					"PPP", 'P', "plankWood", 'D', "dyePink")
```

### new ShapedOreRecipe — línea 399

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L399) · Fabric: **pendiente**

```java
new ShapedOreRecipe(null, new ItemStack(ModBlocks.PAINTED_WOOD_GRAY, 8), "PPP", "PDP",
					"PPP", 'P', "plankWood", 'D', "dyeGray")
```

### new ShapedOreRecipe — línea 401

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L401) · Fabric: **pendiente**

```java
new ShapedOreRecipe(null, new ItemStack(ModBlocks.PAINTED_WOOD_SILVER, 8), "PPP", "PDP",
					"PPP", 'P', "plankWood", 'D', "dyeLightGray")
```

### new ShapedOreRecipe — línea 403

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L403) · Fabric: **pendiente**

```java
new ShapedOreRecipe(null, new ItemStack(ModBlocks.PAINTED_WOOD_CYAN, 8), "PPP", "PDP",
					"PPP", 'P', "plankWood", 'D', "dyeCyan")
```

### new ShapedOreRecipe — línea 405

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L405) · Fabric: **pendiente**

```java
new ShapedOreRecipe(null, new ItemStack(ModBlocks.PAINTED_WOOD_PURPLE, 8), "PPP", "PDP",
					"PPP", 'P', "plankWood", 'D', "dyePurple")
```

### new ShapedOreRecipe — línea 407

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L407) · Fabric: **pendiente**

```java
new ShapedOreRecipe(null, new ItemStack(ModBlocks.PAINTED_WOOD_BLUE, 8), "PPP", "PDP",
					"PPP", 'P', "plankWood", 'D', "dyeBlue")
```

### new ShapedOreRecipe — línea 409

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L409) · Fabric: **pendiente**

```java
new ShapedOreRecipe(null, new ItemStack(ModBlocks.PAINTED_WOOD_BROWN, 8), "PPP", "PDP",
					"PPP", 'P', "plankWood", 'D', "dyeBrown")
```

### new ShapedOreRecipe — línea 411

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L411) · Fabric: **pendiente**

```java
new ShapedOreRecipe(null, new ItemStack(ModBlocks.PAINTED_WOOD_GREEN, 8), "PPP", "PDP",
					"PPP", 'P', "plankWood", 'D', "dyeGreen")
```

### new ShapedOreRecipe — línea 413

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L413) · Fabric: **pendiente**

```java
new ShapedOreRecipe(null, new ItemStack(ModBlocks.PAINTED_WOOD_RED, 8), "PPP", "PDP",
					"PPP", 'P', "plankWood", 'D', "dyeRed")
```

### new ShapedOreRecipe — línea 415

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L415) · Fabric: **pendiente**

```java
new ShapedOreRecipe(null, new ItemStack(ModBlocks.PAINTED_WOOD_BLACK, 8), "PPP", "PDP",
					"PPP", 'P', "plankWood", 'D', "dyeBlack")
```

### new RecipeNonIngredientReturn — línea 422

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L422) · Fabric: **pendiente**

```java
new RecipeNonIngredientReturn(null, aleWortBucket, new ItemStack(Items.BREAD),
				new ItemStack(Items.SUGAR), new ItemStack(Items.WATER_BUCKET))
```

### new RecipeCabinet — línea 427

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L427) · Fabric: **pendiente**

```java
new RecipeCabinet()
```

### new RecipeNonIngredientReturn — línea 430

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L430) · Fabric: **pendiente**

```java
new RecipeNonIngredientReturn(null, new ItemStack(Items.GLASS_BOTTLE),
					new ItemStack(ModItems.FLUID_BOTTLE))
```

### new RecipeNonIngredientReturn — línea 433

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L433) · Fabric: **pendiente**

```java
new RecipeNonIngredientReturn(null, new ItemStack(ModBlocks.LIQUID_BARREL),
				new ItemStack(ModBlocks.LIQUID_BARREL))
```

### new RecipeOliveOil — línea 439

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L439) · Fabric: **pendiente**

```java
new RecipeOliveOil()
```

### new RecipeVantaOil — línea 444

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L444) · Fabric: **pendiente**

```java
new RecipeVantaOil()
```

### GameRegistry.addShapelessRecipe — línea 447

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L447) · Fabric: **pendiente**

```java
GameRegistry.addShapelessRecipe(new ResourceLocation(Rustic.MODID, "oliveseed"), null, new ItemStack(DynamicTreesCompat.getOliveSeed()), Ingredient.fromItem(ModItems.OLIVES))
```

### GameRegistry.addShapelessRecipe — línea 448

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L448) · Fabric: **pendiente**

```java
GameRegistry.addShapelessRecipe(new ResourceLocation(Rustic.MODID, "ironwoodseed"), null, new ItemStack(DynamicTreesCompat.getIronwoodSeed()), Ingredient.fromItem(ModItems.IRONBERRIES))
```

### new ShapedOreRecipe — línea 456

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L456) · Fabric: **pendiente**

```java
new ShapedOreRecipe(null,
					new ItemStack(ModBlocks.CANDLE_SILVER, 4),
						"S",
						"W",
						"I",
					'S', new ItemStack(Items.STRING),
					'W', "wax",
					'I', "ingotSilver"
			)
```

### new ShapedOreRecipe — línea 465

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L465) · Fabric: **pendiente**

```java
new ShapedOreRecipe(null,
					new ItemStack(ModBlocks.CANDLE_DOUBLE_SILVER, 4),
						"S S",
						"W W",
						" I ",
					'S', new ItemStack(Items.STRING),
					'W', "wax",
					'I', "ingotSilver"
			)
```

### new ShapedOreRecipe — línea 474

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L474) · Fabric: **pendiente**

```java
new ShapedOreRecipe(null,
					new ItemStack(ModBlocks.CANDLE_LEVER_SILVER, 1),
						"C",
						"R",
					'C', new ItemStack(ModBlocks.CANDLE_SILVER),
					'R', "dustRedstone"
			)
```

### new ShapedOreRecipe — línea 481

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L481) · Fabric: **pendiente**

```java
new ShapedOreRecipe(null,
					new ItemStack(ModBlocks.CHAIN_SILVER, 12),
						"I",
						"I",
						"I",
					'I', "ingotSilver"
			)
```

### new ShapedOreRecipe — línea 488

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L488) · Fabric: **pendiente**

```java
new ShapedOreRecipe(null,
					new ItemStack(ModBlocks.CHANDELIER_SILVER, 2),
						" I ",
						"C C",
						"III",
					'I', "ingotSilver",
					'C', ModBlocks.CHAIN_SILVER
			)
```

### new ShapedOreRecipe — línea 496

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L496) · Fabric: **pendiente**

```java
new ShapedOreRecipe(null,
					new ItemStack(ModBlocks.SILVER_LANTERN, 4),
						"I",
						"C",
						"I",
					'I', "ingotSilver",
					'C', Ingredient.fromStacks(new ItemStack(Items.COAL, 1, 0), new ItemStack(Items.COAL, 1, 1))
			)
```

### new CrushingTubRecipe — línea 509

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L509) · Fabric: **pendiente**

```java
new CrushingTubRecipe(new FluidStack(ModFluids.OLIVE_OIL, 250), new ItemStack(ModItems.OLIVES))
```

### new CrushingTubRecipe — línea 510

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L510) · Fabric: **pendiente**

```java
new CrushingTubRecipe(new FluidStack(ModFluids.IRONBERRY_JUICE, 250),
				new ItemStack(ModItems.IRONBERRIES))
```

### new CrushingTubRecipe — línea 512

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L512) · Fabric: **pendiente**

```java
new CrushingTubRecipe(new FluidStack(FluidRegistry.WATER, 250),
				new ItemStack(Items.REEDS), new ItemStack(Items.SUGAR, 2))
```

### new CrushingTubRecipe — línea 514

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L514) · Fabric: **pendiente**

```java
new CrushingTubRecipe(new FluidStack(ModFluids.WILDBERRY_JUICE, 250),
				new ItemStack(ModItems.WILDBERRIES))
```

### new CrushingTubRecipe — línea 517

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L517) · Fabric: **pendiente**

```java
new CrushingTubRecipe(new FluidStack(ModFluids.GRAPE_JUICE, 250), new ItemStack(ModItems.GRAPES))
```

### new CrushingTubRecipe — línea 519

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L519) · Fabric: **pendiente**

```java
new CrushingTubRecipe(new FluidStack(ModFluids.APPLE_JUICE, 250),
				new ItemStack(Items.APPLE), new ItemStack(ModBlocks.APPLE_SEEDS))
```

### new CrushingTubRecipe — línea 522

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L522) · Fabric: **pendiente**

```java
new CrushingTubRecipe(new FluidStack(ModFluids.APPLE_JUICE, 250),
					new ItemStack(Items.APPLE), new ItemStack(DynamicTreesCompat.getAppleSeed()))
```

### new CrushingTubRecipe — línea 526

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L526) · Fabric: **pendiente**

```java
new CrushingTubRecipe(new FluidStack(ModFluids.HONEY, 250), new ItemStack(ModItems.HONEYCOMB))
```

### new CrushingTubRecipe — línea 528

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L528) · Fabric: **pendiente**

```java
new CrushingTubRecipe(new FluidStack(ModFluids.GOLDEN_APPLE_JUICE, 100), new ItemStack(Items.GOLDEN_APPLE, 1, 0), new ItemStack(ModBlocks.APPLE_SEEDS))
```

### new CrushingTubRecipe — línea 529

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L529) · Fabric: **pendiente**

```java
new CrushingTubRecipe(new FluidStack(ModFluids.GOLDEN_APPLE_JUICE, 1000), new ItemStack(Items.GOLDEN_APPLE, 1, 1), new ItemStack(ModBlocks.APPLE_SEEDS))
```

### new CrushingTubRecipe — línea 531

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L531) · Fabric: **pendiente**

```java
new CrushingTubRecipe(new FluidStack(ModFluids.GOLDEN_APPLE_JUICE, 100), new ItemStack(Items.GOLDEN_APPLE, 1, 0), new ItemStack(DynamicTreesCompat.getAppleSeed()))
```

### new CrushingTubRecipe — línea 532

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L532) · Fabric: **pendiente**

```java
new CrushingTubRecipe(new FluidStack(ModFluids.GOLDEN_APPLE_JUICE, 1000), new ItemStack(Items.GOLDEN_APPLE, 1, 1), new ItemStack(DynamicTreesCompat.getAppleSeed()))
```

### new CrushingTubRecipe — línea 535

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L535) · Fabric: **pendiente**

```java
new CrushingTubRecipe(new FluidStack(ModFluids.VANTA_OIL, 250), new ItemStack(Herbs.VANTA_LILY))
```

### new EvaporatingBasinRecipe — línea 541

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L541) · Fabric: **pendiente**

```java
new EvaporatingBasinRecipe(
						new ItemStack(ModItems.IRON_DUST_TINY, 1),
						new FluidStack(ModFluids.IRONBERRY_JUICE, 500)
				)
```

### new EvaporatingBasinRecipe — línea 548

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L548) · Fabric: **pendiente**

```java
new EvaporatingBasinRecipe(
						new ItemStack(ModItems.GOLD_DUST, 1),
						new FluidStack(ModFluids.GOLDEN_APPLE_JUICE, 100)
				)
```

### new BasicCondenserRecipe — línea 556

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L556) · Fabric: **pendiente**

```java
new BasicCondenserRecipe(new PotionEffect(MobEffects.INSTANT_HEALTH, 1),
				new ItemStack(Herbs.CHAMOMILE), new ItemStack(Items.BEEF))
```

### new AdvancedCondenserRecipe — línea 558

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L558) · Fabric: **pendiente**

```java
new AdvancedCondenserRecipe(new PotionEffect(MobEffects.INSTANT_HEALTH, 1, 1),
				new ItemStack(Herbs.MARSH_MALLOW), new ItemStack(Herbs.CHAMOMILE), new ItemStack(Items.BEEF))
```

### new BasicCondenserRecipe — línea 560

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L560) · Fabric: **pendiente**

```java
new BasicCondenserRecipe(new PotionEffect(MobEffects.REGENERATION, 900),
				new ItemStack(Herbs.COHOSH), new ItemStack(ModItems.HONEYCOMB))
```

### new AdvancedCondenserRecipe — línea 562

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L562) · Fabric: **pendiente**

```java
new AdvancedCondenserRecipe(new PotionEffect(MobEffects.REGENERATION, 1800),
				new ItemStack(Herbs.HORSETAIL), new ItemStack(Herbs.COHOSH), new ItemStack(ModItems.HONEYCOMB))
```

### new AdvancedCondenserRecipe — línea 564

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L564) · Fabric: **pendiente**

```java
new AdvancedCondenserRecipe(new PotionEffect(MobEffects.REGENERATION, 450, 1),
				new ItemStack(Herbs.MARSH_MALLOW), new ItemStack(Herbs.COHOSH), new ItemStack(ModItems.HONEYCOMB))
```

### new BasicCondenserRecipe — línea 566

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L566) · Fabric: **pendiente**

```java
new BasicCondenserRecipe(new PotionEffect(MobEffects.WITHER, 900),
				new ItemStack(Herbs.DEATHSTALK), new ItemStack(Blocks.SOUL_SAND))
```

### new AdvancedCondenserRecipe — línea 568

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L568) · Fabric: **pendiente**

```java
new AdvancedCondenserRecipe(new PotionEffect(MobEffects.WITHER, 1800),
				new ItemStack(Herbs.HORSETAIL), new ItemStack(Herbs.DEATHSTALK), new ItemStack(Blocks.SOUL_SAND))
```

### new AdvancedCondenserRecipe — línea 570

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L570) · Fabric: **pendiente**

```java
new AdvancedCondenserRecipe(new PotionEffect(MobEffects.WITHER, 450, 1),
				new ItemStack(Herbs.MARSH_MALLOW), new ItemStack(Herbs.DEATHSTALK), new ItemStack(Blocks.SOUL_SAND))
```

### new BasicCondenserRecipe — línea 572

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L572) · Fabric: **pendiente**

```java
new BasicCondenserRecipe(new PotionEffect(MobEffects.NIGHT_VISION, 3600),
				new ItemStack(Herbs.MOONCAP), new ItemStack(Items.SPIDER_EYE))
```

### new AdvancedCondenserRecipe — línea 574

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L574) · Fabric: **pendiente**

```java
new AdvancedCondenserRecipe(new PotionEffect(MobEffects.NIGHT_VISION, 9600),
				new ItemStack(Herbs.HORSETAIL), new ItemStack(Herbs.MOONCAP), new ItemStack(Items.SPIDER_EYE))
```

### new BasicCondenserRecipe — línea 576

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L576) · Fabric: **pendiente**

```java
new BasicCondenserRecipe(new PotionEffect(MobEffects.SPEED, 3600),
				new ItemStack(Herbs.WIND_THISTLE), new ItemStack(Items.SUGAR))
```

### new AdvancedCondenserRecipe — línea 578

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L578) · Fabric: **pendiente**

```java
new AdvancedCondenserRecipe(new PotionEffect(MobEffects.SPEED, 9600),
				new ItemStack(Herbs.HORSETAIL), new ItemStack(Herbs.WIND_THISTLE), new ItemStack(Items.SUGAR))
```

### new AdvancedCondenserRecipe — línea 580

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L580) · Fabric: **pendiente**

```java
new AdvancedCondenserRecipe(new PotionEffect(MobEffects.SPEED, 1800, 1),
				new ItemStack(Herbs.MARSH_MALLOW), new ItemStack(Herbs.WIND_THISTLE), new ItemStack(Items.SUGAR))
```

### new AdvancedCondenserRecipe — línea 583

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L583) · Fabric: **pendiente**

```java
new AdvancedCondenserRecipe(new PotionEffect(MobEffects.FIRE_RESISTANCE, 3600), ItemStack.EMPTY,
						new ItemStack(Herbs.ALOE_VERA), new ItemStack(Items.BRICK), new ItemStack(Items.COAL))
```

### new AdvancedCondenserRecipe — línea 585

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L585) · Fabric: **pendiente**

```java
new AdvancedCondenserRecipe(new PotionEffect(MobEffects.FIRE_RESISTANCE, 9600),
				new ItemStack(Herbs.HORSETAIL), new ItemStack(Herbs.ALOE_VERA), new ItemStack(Items.BRICK),
				new ItemStack(Items.COAL))
```

### new AdvancedCondenserRecipe — línea 588

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L588) · Fabric: **pendiente**

```java
new AdvancedCondenserRecipe(new PotionEffect(MobEffects.HEALTH_BOOST, 3600),
				ItemStack.EMPTY, new ItemStack(Herbs.BLOOD_ORCHID), new ItemStack(Items.ROTTEN_FLESH),
				new ItemStack(Items.REDSTONE))
```

### new AdvancedCondenserRecipe — línea 591

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L591) · Fabric: **pendiente**

```java
new AdvancedCondenserRecipe(new PotionEffect(MobEffects.HEALTH_BOOST, 9600),
				new ItemStack(Herbs.HORSETAIL), new ItemStack(Herbs.BLOOD_ORCHID), new ItemStack(Items.ROTTEN_FLESH),
				new ItemStack(Items.REDSTONE))
```

### new AdvancedCondenserRecipe — línea 594

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L594) · Fabric: **pendiente**

```java
new AdvancedCondenserRecipe(new PotionEffect(MobEffects.HEALTH_BOOST, 1800, 1),
				new ItemStack(Herbs.MARSH_MALLOW), new ItemStack(Herbs.BLOOD_ORCHID), new ItemStack(Items.ROTTEN_FLESH),
				new ItemStack(Items.REDSTONE))
```

### new AdvancedCondenserRecipe — línea 597

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L597) · Fabric: **pendiente**

```java
new AdvancedCondenserRecipe(new PotionEffect(MobEffects.HASTE, 3600), ItemStack.EMPTY,
				new ItemStack(Herbs.CORE_ROOT), new ItemStack(Items.IRON_NUGGET), new ItemStack(Items.REDSTONE))
```

### new AdvancedCondenserRecipe — línea 599

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L599) · Fabric: **pendiente**

```java
new AdvancedCondenserRecipe(new PotionEffect(MobEffects.HASTE, 9600),
				new ItemStack(Herbs.HORSETAIL), new ItemStack(Herbs.CORE_ROOT), new ItemStack(Items.IRON_NUGGET),
				new ItemStack(Items.REDSTONE))
```

### new AdvancedCondenserRecipe — línea 602

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L602) · Fabric: **pendiente**

```java
new AdvancedCondenserRecipe(new PotionEffect(MobEffects.HASTE, 1800, 1),
				new ItemStack(Herbs.MARSH_MALLOW), new ItemStack(Herbs.CORE_ROOT), new ItemStack(Items.IRON_NUGGET),
				new ItemStack(Items.REDSTONE))
```

### new AdvancedCondenserRecipe — línea 605

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L605) · Fabric: **pendiente**

```java
new AdvancedCondenserRecipe(new PotionEffect(MobEffects.STRENGTH, 3600), ItemStack.EMPTY,
				new ItemStack(Herbs.GINSENG), new ItemStack(Items.BONE), new ItemStack(Items.GUNPOWDER))
```

### new AdvancedCondenserRecipe — línea 608

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L608) · Fabric: **pendiente**

```java
new AdvancedCondenserRecipe(new PotionEffect(MobEffects.STRENGTH, 9600), new ItemStack(Herbs.HORSETAIL),
						new ItemStack(Herbs.GINSENG), new ItemStack(Items.BONE), new ItemStack(Items.GUNPOWDER))
```

### new AdvancedCondenserRecipe — línea 610

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L610) · Fabric: **pendiente**

```java
new AdvancedCondenserRecipe(new PotionEffect(MobEffects.STRENGTH, 1800, 1),
				new ItemStack(Herbs.MARSH_MALLOW), new ItemStack(Herbs.GINSENG), new ItemStack(Items.BONE),
				new ItemStack(Items.GUNPOWDER))
```

### new AdvancedCondenserRecipe — línea 613

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L613) · Fabric: **pendiente**

```java
new AdvancedCondenserRecipe(new PotionEffect(PotionsRustic.IRON_SKIN_POTION, 3600),
				ItemStack.EMPTY, new ItemStack(ModItems.IRONBERRIES), new ItemStack(Items.LEATHER),
				new ItemStack(Items.CLAY_BALL))
```

### new AdvancedCondenserRecipe — línea 616

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L616) · Fabric: **pendiente**

```java
new AdvancedCondenserRecipe(new PotionEffect(PotionsRustic.IRON_SKIN_POTION, 9600),
				new ItemStack(Herbs.HORSETAIL), new ItemStack(ModItems.IRONBERRIES), new ItemStack(Items.LEATHER),
				new ItemStack(Items.CLAY_BALL))
```

### new AdvancedCondenserRecipe — línea 619

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L619) · Fabric: **pendiente**

```java
new AdvancedCondenserRecipe(new PotionEffect(PotionsRustic.IRON_SKIN_POTION, 1800, 1),
				new ItemStack(Herbs.MARSH_MALLOW), new ItemStack(ModItems.IRONBERRIES), new ItemStack(Items.LEATHER),
				new ItemStack(Items.CLAY_BALL))
```

### new AdvancedCondenserRecipe — línea 623

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L623) · Fabric: **pendiente**

```java
new AdvancedCondenserRecipe(new PotionEffect(PotionsRustic.FEATHER_POTION, 3600), ItemStack.EMPTY,
						new ItemStack(Herbs.CLOUDSBLUFF), new ItemStack(Items.FEATHER), new ItemStack(Items.PAPER))
```

### new AdvancedCondenserRecipe — línea 625

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L625) · Fabric: **pendiente**

```java
new AdvancedCondenserRecipe(new PotionEffect(PotionsRustic.FEATHER_POTION, 9600),
				new ItemStack(Herbs.HORSETAIL), new ItemStack(Herbs.CLOUDSBLUFF), new ItemStack(Items.FEATHER),
				new ItemStack(Items.PAPER))
```

### new AdvancedCondenserRecipe — línea 628

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L628) · Fabric: **pendiente**

```java
new AdvancedCondenserRecipe(new PotionEffect(PotionsRustic.BLAZING_TRAIL_POTION, 3600),
				ItemStack.EMPTY, new ItemStack(ModItems.CHILI_PEPPER), new ItemStack(Items.BLAZE_POWDER),
				new ItemStack(Blocks.NETHERRACK))
```

### new AdvancedCondenserRecipe — línea 631

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L631) · Fabric: **pendiente**

```java
new AdvancedCondenserRecipe(new PotionEffect(PotionsRustic.BLAZING_TRAIL_POTION, 9600),
				new ItemStack(Herbs.HORSETAIL), new ItemStack(ModItems.CHILI_PEPPER), new ItemStack(Items.BLAZE_POWDER),
				new ItemStack(Blocks.NETHERRACK))
```

### new BrewingBarrelRecipe — línea 638

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L638) · Fabric: **pendiente**

```java
new BrewingBarrelRecipe(new FluidStack(ModFluids.ALE, 1), new FluidStack(ModFluids.ALE_WORT, 1))
```

### new BrewingBarrelRecipe — línea 640

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L640) · Fabric: **pendiente**

```java
new BrewingBarrelRecipe(new FluidStack(ModFluids.CIDER, 1), new FluidStack(ModFluids.APPLE_JUICE, 1))
```

### new BrewingBarrelRecipe — línea 641

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L641) · Fabric: **pendiente**

```java
new BrewingBarrelRecipe(new FluidStack(ModFluids.IRON_WINE, 1),
				new FluidStack(ModFluids.IRONBERRY_JUICE, 1))
```

### new BrewingBarrelRecipe — línea 644

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L644) · Fabric: **pendiente**

```java
new BrewingBarrelRecipe(new FluidStack(ModFluids.MEAD, 1), new FluidStack(ModFluids.HONEY, 1))
```

### new BrewingBarrelRecipe — línea 646

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L646) · Fabric: **pendiente**

```java
new BrewingBarrelRecipe(new FluidStack(ModFluids.MEAD, 1), new FluidStack(FluidRegistry.getFluid("for.honey"), 1))
```

### new BrewingBarrelRecipe — línea 648

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L648) · Fabric: **pendiente**

```java
new BrewingBarrelRecipe(new FluidStack(ModFluids.WILDBERRY_WINE, 1),
				new FluidStack(ModFluids.WILDBERRY_JUICE, 1))
```

### new BrewingBarrelRecipe — línea 651

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L651) · Fabric: **pendiente**

```java
new BrewingBarrelRecipe(new FluidStack(ModFluids.WINE, 1), new FluidStack(ModFluids.GRAPE_JUICE, 1))
```

### new BrewingBarrelRecipe — línea 652

[Abrir fuente completa](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java#L652) · Fabric: **pendiente**

```java
new BrewingBarrelRecipe(new FluidStack(ModFluids.AMBROSIA, 1), new FluidStack(ModFluids.GOLDEN_APPLE_JUICE, 1))
```

