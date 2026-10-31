#!/usr/bin/env python3
"""Rebuild the source-index reports; this is not a Java parser or a parity proof."""
from pathlib import Path
import hashlib
import json
import re
from collections import Counter

ROOT = Path(__file__).resolve().parent.parent
BASE = ROOT / 'original/rustic-pre-1.2.0'
SRC = BASE / 'src/main'
RES = SRC / 'resources'
JAVA = SRC / 'java/rustic'
NOTICE = ('Base: `original/rustic-pre-1.2.0`, commit '
          '`88597f5ca4342a6b9d4509d93967ecbef6f8bafa`, versión declarada 1.2.0. '
          'No se ha verificado que sea idéntico al JAR publicado. '
          'La paridad integral aún no está verificada; consultar el estado de cada fila y RUSTIC_TEST_REPORT.md.\n\n')


def rel(p):
    return p.relative_to(ROOT).as_posix()


def write(name, text):
    (ROOT / name).write_text(text, encoding='utf-8')


def declarations(path, prefix):
    text = path.read_text(encoding='utf-8')
    rows = []
    for no, line in enumerate(text.splitlines(), 1):
        m = re.match(r'\s*public static (?!void|final)([\w<>]+)\s+([A-Z][A-Z_0-9]*)\s*;', line)
        if m:
            rows.append(f'| `{prefix}.{m[2]}` | `{m[1]}` | `{rel(path)}:{no}` | Pendiente |')
    return rows


blocks = JAVA / 'common/blocks/ModBlocks.java'
items = JAVA / 'common/items/ModItems.java'
fluids = JAVA / 'common/blocks/fluids/ModFluids.java'
for filename, label, path, prefix in [('RUSTIC_BLOCKS.md', 'bloques', blocks, 'ModBlocks'), ('RUSTIC_ITEMS.md', 'objetos', items, 'ModItems')]:
    rows = declarations(path, prefix)
    if label == 'objetos':
        started = ('BEE', 'HONEYCOMB', 'BEESWAX', 'TALLOW', 'IRON_DUST_TINY', 'GOLD_DUST', 'WILDBERRIES', 'GRAPES', 'OLIVES', 'CHILI_PEPPER')
        rows = [row.replace('| Pendiente |', '| En curso, paridad sin verificar |')
                if any(f'`ModItems.{name}`' in row for name in started) else row for row in rows]
    else:
        started = ('SLATE', 'SLATE_ROOF', 'SLATE_TILE', 'SLATE_BRICK', 'SLATE_CHISELED',
                   'SLATE_PILLAR', 'SLATE_ROOF_STAIRS', 'SLATE_BRICK_STAIRS',
                   'SLATE_ROOF_SLAB', 'SLATE_BRICK_SLAB', 'SLATE_ROOF_SLAB_ITEM', 'SLATE_BRICK_SLAB_ITEM',
                   'SLATE_ROOF_DOUBLESLAB', 'SLATE_BRICK_DOUBLESLAB',
                   'PAINTED_WOOD_WHITE', 'PAINTED_WOOD_ORANGE', 'PAINTED_WOOD_MAGENTA', 'PAINTED_WOOD_LIGHT_BLUE', 'PAINTED_WOOD_YELLOW', 'PAINTED_WOOD_LIME', 'PAINTED_WOOD_PINK', 'PAINTED_WOOD_GRAY', 'PAINTED_WOOD_SILVER', 'PAINTED_WOOD_CYAN', 'PAINTED_WOOD_PURPLE', 'PAINTED_WOOD_BLUE', 'PAINTED_WOOD_BROWN', 'PAINTED_WOOD_GREEN', 'PAINTED_WOOD_RED', 'PAINTED_WOOD_BLACK', 'CLAY_WALL', 'CLAY_WALL_CROSS', 'CLAY_WALL_DIAG', 'LOG', 'PLANKS')
        rows = [row.replace('| Pendiente |', '| En curso, paridad sin verificar |')
                if any(f'`ModBlocks.{name}`' in row for name in started) else row for row in rows]
    body = f'# Índice de {label} originales\n\n' + NOTICE
    body += ('Este índice enumera campos declarados, **no** IDs de registro ni todas las variantes por metadatos; '
             'algunos campos son ItemBlock, estados internos u opcionales. Los registros y variantes '
             'requieren auditoría manual; hierbas, fluidos y recetas pueden declarar elementos adicionales.\n\n')
    body += f'Campos encontrados: **{len(rows)}**.\n\n| Campo | Tipo original | Fuente | Port |\n|---|---|---|---|\n' + '\n'.join(rows) + '\n'
    herb_path = JAVA / 'common/blocks/crops/Herbs.java'
    herb_rows = declarations(herb_path, 'Herbs')
    kind = 'BlockHerbBase' if label == 'bloques' else 'ItemHerbEdible'
    relevant = [row for row in herb_rows if f'`{kind}`' in row]
    body += f'\n## Campos de hierbas ({len(relevant)})\n\n| Campo | Tipo original | Fuente | Port |\n|---|---|---|---|\n' + '\n'.join(relevant) + '\n'
    if label == 'bloques':
        body += '\nOtras fuentes obligatorias: `common/blocks/fluids/ModFluids.java`, enums de variantes y registros en las propias clases.\n'
    else:
        body += '\nOtros elementos proceden de ItemBlock, fluidos y variantes con metadatos/NBT. Consultar las fuentes y `RUSTIC_BLOCKS.md`.\n'
    write(filename, body)

recipe_dir = RES / 'assets/rustic/recipes'
recipes = sorted(recipe_dir.glob('*.json'))
body = '# Índice y comparación de recetas\n\n' + NOTICE
body += ('La columna original enlaza al JSON íntegro; aún no se han creado equivalentes para esos JSON. '
         'Las llamadas Java se indexan por línea como pistas, no como recetas expandidas: '
         'bucles, condiciones, recetas especiales, integraciones y metadatos precisan revisión manual. '
         'Véase además [expresiones Java con argumentos completos](RUSTIC_RECIPES_JAVA.md) y '
         '[registro de paridad y sus límites](audit/README.md).\n\n')
body += f'## JSON ({len(recipes)} archivos)\n\n| Original | Tipo | Resultado original | Migrada | Estado |\n|---|---|---|---|---|\n'
for p in recipes:
    obj = json.loads(p.read_text(encoding='utf-8'))
    result = json.dumps(obj.get('result', '(especial/no indicado)'), ensure_ascii=False, sort_keys=True)
    body += f'| [{p.stem}]({rel(p)}) | `{obj.get("type", "no indicado")}` | `{result}` | — | Pendiente |\n'
manifest_path = ROOT / 'audit/recipe-parity.json'
mapping = {}
if manifest_path.exists():
    manifest = json.loads(manifest_path.read_text(encoding='utf-8'))
    mapping = {entry['id']: entry for entry in manifest['entries']}
    mapped = [entry for entry in manifest['entries'] if entry.get('targets')]
    body += '\n## Correspondencias iniciadas, no verificadas\n\n| Original | Archivo de destino | Estado |\n|---|---|---|\n'
    for entry in mapped:
        destinations = ', '.join(f'[{Path(target).name}]({target})' for target in entry['targets'])
        body += f'| `{entry["id"]}` | {destinations} | `{entry["status"]}` |\n'
body += '\n## Puntos de registro Java\n\n| Fuente | Declaración / llamada (primera línea) | Migrada | Estado |\n|---|---|---|---|\n'
java_rec = JAVA / 'common/crafting/Recipes.java'
for i, line in enumerate(java_rec.read_text(encoding='utf-8').splitlines(), 1):
    if re.search(r'GameRegistry\.add(?:Smelting|ShapedRecipe|ShapelessRecipe)|new (?:CrushingTubRecipe|EvaporatingBasinRecipe|BasicCondenserRecipe|AdvancedCondenserRecipe|BrewingBarrelRecipe|ShapedOreRecipe)|RecipeSorter\.register|\.setRegistryName\(', line):
        line = line.strip().replace('|', '\\|').replace('`', "'")
        record = mapping.get(f'java:{i}', {})
        targets = ', '.join(f'[{Path(target).name}]({target})' for target in record.get('targets', [])) or '—'
        state = {'in_progress': 'En curso, paridad sin verificar', 'verified': 'Verificada'}.get(record.get('status'), 'Pendiente')
        body += f'| `{rel(java_rec)}:{i}` | `{line}` | {targets} | {state} |\n'
body += '\nLas clases `RecipeCabinet`, `RecipeNonIngredientReturn`, `RecipeOliveOil` y `RecipeVantaOil` implementan lógica especial. Revisar registros externos, condiciones y clases completas antes de afirmar paridad.\n'
write('RUSTIC_RECIPES.md', body)

files = sorted(p for p in RES.rglob('*') if p.is_file())
counts = Counter(p.suffix.lower() or '(sin extensión)' for p in files)
body = '# Inventario de recursos originales\n\n' + NOTICE
body += ('SHA-256 de cada archivo en el árbol de recursos original, sin transformaciones. '
         'Las 376 texturas y metadatos de la referencia 1.2.0 también están copiados sin cambios en '
         '`src/main/resources/assets/rustic/textures`; ocho archivos de idioma se han convertido a JSON '
         'preservando las claves originales y añadiendo alias para diez objetos y treinta y tres bloques. Son pasos parciales: aún faltan adaptación de modelos, claves y '
         'renderizadores, y pruebas visuales en 26.3. Las filas siguen pendientes de integración y prueba. '
         'No se ha auditado visualmente cada imagen ni validado la compatibilidad de cada modelo.\n\n')
body += 'Extensiones: ' + ', '.join(f'`{k}`: {v}' for k, v in sorted(counts.items())) + '.\n\n'
body += '| Archivo original | SHA-256 | Estado |\n|---|---|---|\n'
for p in files:
    body += f'| [{p.relative_to(RES).as_posix()}]({rel(p)}) | `{hashlib.sha256(p.read_bytes()).hexdigest()}` | Pendiente |\n'
write('RUSTIC_ASSETS.md', body)

java = sorted(JAVA.rglob('*.java'))
wiki = sorted((ROOT / 'original/wiki').glob('*.md'))
body = '# Catálogo de características y fuentes\n\n' + NOTICE
body += ('Índice mecánico de archivos y documentación, no una auditoría semántica completa. '
         'No equivale a que todos los comportamientos, entradas, metadatos o combinaciones hayan sido identificados.\n\n')
body += '## Sistemas identificados\n\n'
for desc, sources in [
    ('Decoración y construcción, variantes, colocación y redstone', 'common/blocks, blockstates, models'),
    ('Almacenamiento y fluidos', 'common/tileentity, common/inventory, common/blocks/fluids'),
    ('Cultivos, abejas como objeto, colmenas y apiarios', 'common/blocks/crops, BlockBeehive, TileEntityApiary'),
    ('Árboles, hierbas, pizarra y generación', 'common/world, common/blocks/crops, common/Config.java'),
    ('Fermentación, calidad y bebidas', 'BrewingBarrelRecipe, TileEntityBrewingBarrel, FluidBooze, ModFluids'),
    ('Alquimia y efectos', 'common/crafting, common/potions, common/util/ElixirUtils.java'),
    ('Almanaque con páginas y traducciones', 'common/book, client/gui/book, assets/rustic/lang'),
    ('Integraciones opcionales JEI, CraftTweaker, Dynamic Trees y Forestry', 'compat, Recipes.java, Config.java'),
    ('HUD, renderizadores, avances, eventos y red', 'client, common/advancements, common/network, EventHandlerCommon')]:
    body += f'- **{desc}** — `{sources}` — pendiente.\n'
body += f'\n## Código Java ({len(java)} archivos)\n\n' + ''.join(f'- [{p.relative_to(JAVA).as_posix()}]({rel(p)}) — pendiente\n' for p in java)
body += f'\n## Wiki ({len(wiki)} páginas)\n\n' + ''.join(f'- [{p.stem}]({rel(p)}) — referencia, pendiente de contraste\n' for p in wiki)
write('RUSTIC_FEATURES.md', body)
print(f'{len(java)} Java, {len(files)} recursos, {len(recipes)} JSON de recetas; informes generados')

if __name__ == '__main__':
    pass
