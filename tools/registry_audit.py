#!/usr/bin/env python3
"""Index selected declared fluids, effects, entity registrations and book entries."""
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
JAVA = ROOT / 'original/rustic-pre-1.2.0/src/main/java/rustic'
OUTPUT = ROOT / 'RUSTIC_REGISTERS.md'


def lines(path, regex):
    for number, line in enumerate(path.read_text(encoding='utf-8').splitlines(), 1):
        match = re.search(regex, line)
        if match:
            yield number, match


def row(path, number, fields):
    source = path.relative_to(ROOT).as_posix()
    return '| ' + ' | '.join(f'`{field}`' for field in fields) + f' | [{number}]({source}#L{number}) | Pendiente |\n'


def main():
    doc = '''# Índice adicional de registros y almanaque

Base: commit `88597f5ca4342a6b9d4509d93967ecbef6f8bafa`, versión declarada 1.2.0.
**Todos los elementos están pendientes de migrar y probar.** Índice de declaraciones y
llamadas concretas, no de variantes, asignaciones efectivas, condiciones ni páginas
completas. Consultar siempre el código, las traducciones y la configuración.

'''
    fluids = JAVA / 'common/blocks/fluids/ModFluids.java'
    entries = list(lines(fluids, r'public static (Fluid|BlockFluidRustic) ([A-Z_]+);'))
    doc += f'## Campos de fluidos y bloques de fluido ({len(entries)})\n\n| Tipo | Campo | Línea | Estado |\n|---|---|---|---|\n'
    for number, m in entries:
        doc += row(fluids, number, m.groups())
    potions = JAVA / 'common/potions/PotionsRustic.java'
    entries = list(lines(potions, r'public static final Potion ([A-Z_]+).*?"rustic:([a-z_]+)"'))
    doc += f'\n## Efectos ({len(entries)})\n\n| Campo | ID | Línea | Estado |\n|---|---|---|---|\n'
    for number, m in entries:
        doc += row(potions, number, (m[1], 'rustic:' + m[2]))
    blocks = JAVA / 'common/blocks/ModBlocks.java'
    entries = list(lines(blocks, r'registerTileEntity\((\w+)\.class,.*?"([^"]+)"'))
    doc += f'\n## Tile entities ({len(entries)})\n\n| Clase | ID original | Línea | Estado |\n|---|---|---|---|\n'
    for number, m in entries:
        doc += row(blocks, number, m.groups())
    entities = JAVA / 'common/entities/ModEntities.java'
    entries = list(lines(entities, r'registerModEntity\(new ResourceLocation\(Rustic.MODID \+ ":([^"]+)"\), ([\w.]+)\.class'))
    doc += f'\n## Entidades ({len(entries)})\n\n| ID | Clase | Línea | Estado |\n|---|---|---|---|\n'
    for number, m in entries:
        doc += row(entities, number, ('rustic:' + m[1], m[2]))
    book = JAVA / 'common/book/BookManager.java'
    entries = list(lines(book, r'new BookCategory\("([^"]+)"\)'))
    doc += f'\n## Categorías del almanaque ({len(entries)})\n\n| ID | Línea | Estado |\n|---|---|---|\n'
    for number, m in entries:
        doc += row(book, number, (m[1],))
    entries = list(lines(book, r'new BookEntry\("([^"]+)",\s*(\w+)\)'))
    doc += f'\n## Entradas del almanaque ({len(entries)})\n\n| ID | Categoría | Línea | Estado |\n|---|---|---|---|\n'
    for number, m in entries:
        doc += row(book, number, m.groups())
    doc += ('\nLas páginas, enlaces relacionados y condiciones se construyen más abajo en '
            '`BookManager.java`; sus textos y traducciones se encuentran en los archivos `.lang`. '
            'No se ha verificado que cada página tenga traducción ni adaptación moderna.\n')
    OUTPUT.write_text(doc, encoding='utf-8')
    print('Índice de registros y almanaque generado')


if __name__ == '__main__':
    main()
