# Mecánicas: investigación inicial

Base: `original/rustic-pre-1.2.0` (commit `88597f5ca4342a6b9d4509d93967ecbef6f8bafa`, versión declarada 1.2.0). **El port está incompleto; ningún sistema está verificado en juego**. Este documento resume hallazgos, no sustituye la auditoría exhaustiva de cada ruta de código.

| Sistema | Hallazgo comprobable en la fuente original | Fuentes principales | Estado |
|---|---|---|---|
| Abejas | `BEE` es un objeto; colmena y apiario gestionan producción. Las entidades registradas son tomate y asiento, no una entidad abeja propia. No se debe sustituir sin más por la abeja vanilla moderna. | `ModItems`, `ModEntities`, `BlockBeehive`, `TileEntityApiary`, `original/wiki/Bees.md` | Pendiente |
| Cultivos | Estacas, tomate, chile, uvas, bayas, hierbas y suelo fértil tienen clases propias. Sus estados, semillas, cosecha y configuración deben compararse individualmente. | `common/blocks/crops`, `Config`, `common/world` | Pendiente |
| Árboles | Olivo, ironwood y manzano tienen generación y/o lógica de plantones/hojas diferenciadas; existen variantes con metadatos. | `common/world`, `BlockSaplingRustic`, `BlockLeavesRustic`, cultivos | Pendiente |
| Fermentación | Barril con tanques de entrada, salida y cultivo; el tiempo por defecto es 12000 ticks y la calidad afecta a resultados. La ambrosía tiene una receta con cálculo propio; consultar fuente además de la wiki. | `TileEntityBrewingBarrel`, `BrewingBarrelRecipe`, `Recipes`, `ModFluids`, `Config`, `original/wiki/Brewing.md` | Pendiente |
| Bebidas | Ale, sidra, vino de ironberry, hidromiel, vino de bayas, vino y ambrosía; efectos dependen de calidad y de la lógica de embriaguez. | `ModFluids`, `FluidBooze`, `common/potions` | Pendiente |
| Alquimia | Condensadores simple y avanzado, retortas, elixires, modificadores y efectos; recetas con entradas y duraciones explícitas, aceite vanta y aceite de oliva tienen lógica especial. | `Recipes`, `TileEntityCondenser*`, `RecipeOliveOil`, `RecipeVantaOil`, `ElixirUtils` | Pendiente |
| Procesos adicionales | Tina de trituración y cubeta de evaporación usan recetas propias y fluidos, no son crafteos normales. | `TileEntityCrushingTub`, `TileEntityEvaporatingBasin`, `Recipes` | Pendiente |
| Almacenamiento y decoración | Armarios simples/dobles, barriles, barriles de líquidos y vasijas poseen inventarios y/o lógica especiales. Sillas, cadenas, velas y otros bloques también tienen clases específicas. | `common/blocks`, `common/tileentity`, `common/inventory` | Pendiente |
| Almanaque | Las categorías, entradas, páginas y relaciones se construyen en Java; los textos están en traducciones, con condiciones de configuración. GUI y animaciones propias. | `BookManager`, `GuiBook`, `assets/rustic/lang`, `textures/gui/book` | Pendiente |
| Configuración | Además de habilitar familias de bloques, afecta generación, abejas, calidad y duración de fermentación, compatibilidad, recetas y HUD. | `common/Config.java` | Pendiente |
| Integraciones | JEI, CraftTweaker, Dynamic Trees, Forestry y referencias a Quark. No se ha demostrado aún disponibilidad ni compatibilidad para 26.3 de las integraciones opcionales. | `rustic/compat`, `Recipes.java`, `Config.java` | Pendiente |
| Red y multijugador | Hay mensajes y sincronización de tile entities; traducir las garantías a la arquitectura moderna requiere pruebas servidor/cliente. | `common/network`, `common/tileentity`, `core` | Pendiente |

Hay muchos detalles adicionales en las fuentes, como NBT, cambios de inventario, drop aleatorio, requisitos de bioma y renderizadores especiales. Deben cotejarse y probarse antes de declarar paridad.

## Detalle auditado: uvas y bayas silvestres

[`ModItems.java:178-189`](original/rustic-pre-1.2.0/src/main/java/rustic/common/items/ModItems.java#L178) define bayas con 2 puntos de hambre y multiplicador de saturación 0,5, y uvas con 3 puntos y multiplicador 0,3. Ambas tardan 16 ticks en consumirse, no son alimento para lobos y no habilitan `setAlwaysEdible`. Se han declarado sus componentes de alimento y consumo, y se ha probado el consumo en GameTest de servidor y en un cliente individual automatizado. Falta comparar en vivo con el original y probar lobos, cultivos, cosecha, trituración, etiquetas de compatibilidad, pestaña original, dos jugadores y paridad en servidor dedicado.

## Detalle auditado: apiario

En [`TileEntityApiary.java`](original/rustic-pre-1.2.0/src/main/java/rustic/common/tileentity/TileEntityApiary.java), con `n > 0` abejas y lógica de producción en servidor:

- Tiempo de reproducción: truncar a entero `multiplicador_reproducción * 1600 / (n / 20 + 1)` ticks. Producción de panal: `multiplicador_panal * 800 / (n / 20 + 1)` ticks. Los contadores se reinician al superar el tiempo, incluso cuando la pila está llena; tope 64 de cada producto.
- Probabilidad por tick de intentar acelerar cultivo: `1 / ceil(2048 / (n * multiplicador_cultivo))`, si el multiplicador no es cero. Se elige X y Z en −4…4 y se comprueban Y+1, Y e Y−1 en ese orden, deteniéndose en el primer `IGrowable` o `IPlantable`.
- El inventario tiene dos manejadores separados; acceso desde abajo expone panal y desde otras caras, abejas. Al romperse, se expulsan sus contenidos en servidor.
- Se serializan pilas y existe compatibilidad de lectura con el formato anterior `items`; los temporizadores no se serializan en esta revisión. Esto debe valorarse explícitamente en las pruebas de guardado, no asumir que son persistentes.

## Detalle auditado: fermentación

Fuentes: [`TileEntityBrewingBarrel.java`](original/rustic-pre-1.2.0/src/main/java/rustic/common/tileentity/TileEntityBrewingBarrel.java), [`BrewingBarrelRecipe.java`](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/BrewingBarrelRecipe.java), [`Recipes.java`](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java), [`FluidBooze.java`](original/rustic-pre-1.2.0/src/main/java/rustic/common/blocks/fluids/FluidBooze.java), [`ModFluids.java`](original/rustic-pre-1.2.0/src/main/java/rustic/common/blocks/fluids/ModFluids.java).

- Tanques de entrada y salida de 8000 mB, cultivo de 1000 mB, seis ranuras de entrada/salida para recipientes. Se serializan tanques, inventario y `brewTime`.
- Cambios de fluido reinician el tiempo; el proceso vuelve a buscar receta periódicamente. Al completar, transfiere como máximo el mínimo de entrada y espacio de salida, solo si los ensayos de llenado/vaciado permiten transferir todo ese importe; el cultivo no se consume. Si ya hay salida del mismo fluido, el código copia el fluido de salida existente, incluida su calidad, en vez de mezclar las calidades.
- Sin cultivo, `BrewingBarrelRecipe` produce calidad `(5 + nextInt(71))/100`, de 0,05 a 0,75 inclusive. Con cultivo del mismo tipo, parte de `(int)(100 * auxQuality)` y suma un entero aleatorio en el intervalo configurado inclusivo (por defecto −1…4), limitando a [0, 1]. La truncación y aritmética `float` de Java son relevantes. Si el cultivo no contiene `Quality`, la ruta del código no asigna una calidad al resultado.
- Ambrosía tiene una implementación especial: sin cultivo, elige `r` de 0…25 con probabilidad 1/4 y de 14…25 con probabilidad 3/4, y usa `(49+r)/100`. Con cultivo usa límites `max(0, mínimo)` y `max(7, máximo)` tras normalizar el máximo configurado. No se debe reemplazar por la receta genérica de fermentación.
- Al beber, `FluidBooze` limita la calidad leída a [0,1], usa cero si falta el NBT float `Quality`, calcula embriaguez y luego aplica el efecto específico. `ModFluids` define ramas diferentes para calidad inferior a 0,5. La ambrosía mala aplica daño `Float.MAX_VALUE`; la de calidad superior a 0,99 asigna 999999 ticks de `UNDYING`.

## Detalle auditado: condensadores

En [`CondenserRecipe.java`](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/CondenserRecipe.java), [`BasicCondenserRecipe.java`](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/BasicCondenserRecipe.java) y [`AdvancedCondenserRecipe.java`](original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/AdvancedCondenserRecipe.java): la receta base exige botella de vidrio y agua; declara 125 mB y consumo de un elemento por entrada y botella. La simple rechaza modificador y declara 400 ticks; la avanzada comprueba modificador, metadatos y eventualmente NBT, y declara 300 ticks. La selección y consumo efectivo se deben contrastar con las tile entities, no solo con los constructores. La comparación de NBT en la base contiene un `tempInputs.remove(stack)` en vez de `remove(temp)`; no se debe corregir silenciosamente sin un caso de reproducción y una decisión explícita de compatibilidad.

### Inventario previo: arbusto de bayas y pizarra (referencia 1.2.0)

- `BlockBerryBush`: propiedad booleana `berries`, selección 2..14 × 0..16 × 2..14, sin colisión sólida, movimiento horizontal ×0,6; por tick aleatorio con luz superior ≥9 y probabilidad 1/41 el arbusto sin fruto madura. Interactuar con fruto entrega una baya y vacía el arbusto, excepto con harina de huesos en mano. Harina madura el arbusto vacío o intenta colocar uno vacío en una posición válida entre las ocho vecinas; la propagación natural está comentada en esta revisión. Al romperlo, además del arbusto, un fruto si estaba maduro. La colocación y supervivencia dependen de `canSustainPlant` Forge, no solo de una lista fija; faltan equivalencias Fabric, tintes, desplazamiento configurable, inflamabilidad y generación.
- `WorldGenWildberries` y `WorldGeneratorRustic`: generación por dimensiones/configuración, clima mediante lista negra de diez tipos Forge, selección de posiciones y hasta `MAX_WILDBERRY_ATTEMPTS`; se debe definir y probar la equivalencia de biomas modernos sin dar por idéntica la clasificación de Forge.
- `ModBlocks.init`: `slate`, `slate_roof`, `slate_tile`, `slate_brick`, `slate_chiseled`, bloques de roca con dureza 2, en pestaña decorativa y habilitados por `ENABLE_SLATE`; `BlockBase` registra bloque y objeto. Sus modelos originales son cubos con texturas `rustic:blocks/<id>`. Faltan generación natural, configuración, pilares, escaleras y losas.
- `Recipes.java:313–330`: 2×2 pizarra → 4 techos; 1 pizarra o 1 ladrillo → 1 baldosa; 2×2 baldosas → 4 ladrillos; 2×2 ladrillos → 4 cinceladas. Las otras recetas requieren pilares, escaleras o losas todavía no migradas. La clasificación OreDictionary, desbloqueos y visualización en JEI necesitan evaluación independiente.

### Inventario previo: formas de pizarra

- `BlockPillar("slate")`: roca, dureza 2, eje por cara de colocación (`x`, `y`, `z`), gira al rotar, se obtiene como pilar vertical; receta `pillar_slate` 2×3 pizarra → 6. Activado por `ENABLE_SLATE` y `ENABLE_PILLARS`.
- `BlockStairsBase`: escaleras de techo y ladrillo con comportamiento de `BlockStairs`, estados de orientación, mitad y forma; recetas en escalera de seis bloques → 4. Los modelos del original distinguen recto, interior y exterior.
- `BlockSlabBase`/`BlockDoubleSlabBase`: mitades superior/inferior y bloque doble separado en 1.12; al romper doble entrega dos losas. Receta horizontal de tres bloques → 6 losas. En 26.3 las dos mitades y doble se representan como estados de un único bloque; se mantendrá documentada la correspondencia de IDs antiguos y falta conversión de mundos existentes. El agua en los estados modernos no existía en el original.
- Recursos: las tablas Forge 1.12 usan `rustic:<modelo>` y para las losas padres `block/half_slab` y `block/upper_slab`; hay que adaptar sus referencias a `rustic:block/...` y a los nombres de padres vanilla actuales, conservando las texturas originales y las variantes. Falta validación visual de todas las orientaciones.

### Inventario previo: chile (`chili_pepper`)

`ModItems.java:137–152` crea `ItemFoodBase("chili_pepper", 3, 0.4F, false)`, siempre comestible, con duración normal de comida de 32 ticks; tras comer, solo en el servidor, aplica velocidad I por 400 ticks y con probabilidad 1/24 inflige 1 punto de daño mediante `DamageSource.ON_FIRE`. La probabilidad utiliza un `java.util.Random` del objeto. Las partículas e icono usan los valores predeterminados del constructor `PotionEffect`. El objeto se muestra en la pestaña agrícola; textura, modelo y claves están en la referencia. Su cultivo, semillas y su obtención todavía requieren el sistema de `BlockStakeCrop` y generación de semillas; no están incluidos en este paso. El resultado de los mecanismos de daño actuales se debe contrastar en pruebas con fuego/resistencias y multijugador, pues las API de daño cambiaron entre versiones.
