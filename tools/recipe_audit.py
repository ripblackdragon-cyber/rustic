#!/usr/bin/env python3
"""Index selected Java recipe expressions verbatim; deliberately not a Java interpreter."""
from pathlib import Path
import json
import re
from collections import Counter

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / 'original/rustic-pre-1.2.0/src/main/java/rustic/common/crafting/Recipes.java'
OUTPUT = ROOT / 'RUSTIC_RECIPES_JAVA.md'

# Enumerating the intended symbols explicitly avoids treating arbitrary constructors as recipes.
PATTERN = re.compile(
    r'\b(?:GameRegistry\.(?:addSmelting|addShapedRecipe|addShapelessRecipe)'
    r'|new\s+(?:ShapedOreRecipe|RecipeNonIngredientReturn|RecipeCabinet|RecipeOliveOil|RecipeVantaOil|'
    r'CrushingTubRecipe|EvaporatingBasinRecipe|BasicCondenserRecipe|AdvancedCondenserRecipe|BrewingBarrelRecipe))\s*\('
)


def code_mask(text):
    """Hide strings, chars and comments while preserving offsets and newlines."""
    result = list(text)
    state = 'code'
    i = 0
    while i < len(text):
        c = text[i]
        nxt = text[i + 1] if i + 1 < len(text) else ''
        if state == 'code':
            if c == '/' and nxt in ('/', '*'):
                state = 'line' if nxt == '/' else 'block'
                result[i] = result[i + 1] = ' '
                i += 2
                continue
            if c in ('"', "'"):
                state = c
                result[i] = ' '
        elif state == 'line':
            if c == '\n':
                state = 'code'
            else:
                result[i] = ' '
        elif state == 'block':
            if c == '*' and nxt == '/':
                result[i] = result[i + 1] = ' '
                state = 'code'
                i += 2
                continue
            if c != '\n':
                result[i] = ' '
        else:
            if c == '\\':
                result[i] = ' '
                if i + 1 < len(text):
                    if text[i + 1] != '\n':
                        result[i + 1] = ' '
                    i += 2
                    continue
            if c == state:
                state = 'code'
            if c != '\n':
                result[i] = ' '
        i += 1
    return ''.join(result)


def extract(text):
    masked = code_mask(text)
    found = []
    for match in PATTERN.finditer(masked):
        start = match.start()
        open_paren = match.end() - 1
        depth = 0
        for end in range(open_paren, len(masked)):
            if masked[end] == '(':
                depth += 1
            elif masked[end] == ')':
                depth -= 1
                if depth == 0:
                    break
        else:
            raise ValueError(f'unclosed expression at line {text.count(chr(10), 0, start) + 1}')
        expression = text[start:end + 1]
        kind = re.sub(r'\s+', ' ', text[start:open_paren].strip())
        found.append((text.count('\n', 0, start) + 1, kind, expression))
    return found


def main():
    records = extract(SOURCE.read_text(encoding='utf-8'))
    counts = Counter(kind for _, kind, _ in records)
    rel = SOURCE.relative_to(ROOT).as_posix()
    manifest_path = ROOT / 'audit/recipe-parity.json'
    statuses = {}
    if manifest_path.exists():
        manifest = json.loads(manifest_path.read_text(encoding='utf-8'))
        statuses = {entry['id']: entry.get('status') for entry in manifest['entries']}
    text = '''# Expresiones Java de recetas de Rustic 1.2.0

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
'''
    for kind, count in sorted(counts.items()):
        text += f'| `{kind}` | {count} | Sin verificar |\n'
    text += f'\nTotal: **{len(records)} expresiones fuente**, no recetas expandidas.\n\n## Expresiones originales\n\n'
    for line, kind, expr in records:
        text += f'### {kind} — línea {line}\n\n'
        status = {'in_progress': 'en curso, paridad sin verificar', 'verified': 'verificada'}.get(statuses.get(f'java:{line}'), 'pendiente')
        text += f'[Abrir fuente completa]({rel}#L{line}) · Fabric: **{status}**\n\n```java\n{expr}\n```\n\n'
    OUTPUT.write_text(text, encoding='utf-8')
    print(f'{len(records)} expresiones indexadas; {len(counts)} categorías')


if __name__ == '__main__':
    main()
