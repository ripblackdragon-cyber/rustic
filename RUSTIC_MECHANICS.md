# Mecánicas: investigación inicial

Base: `original/rustic-pre-1.2.0` (commit `88597f5ca4342a6b9d4509d93967ecbef6f8bafa`, versión declarada 1.2.0). **Todo está pendiente de implementación y de prueba**. Este documento resume hallazgos, no sustituye la auditoría exhaustiva de cada ruta de código.

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
