# Rustic: investigación para port Fabric 26.3

**Proyecto incompleto: no es una versión jugable.** CI ha compilado un JAR de desarrollo, pero no se ha verificado su arranque ni su funcionamiento en juego. No poner las copias Forge de `original/` en la carpeta de mods de 26.3. Hay configuración Fabric y código inicial para seis ingredientes y tres recetas de fundición, además de texturas originales e idiomas convertidos; todo sigue pendiente de pruebas y de la mayoría de la funcionalidad.

- [Progreso y procedencia](PORT_PROGRESS.md)
- [Características](RUSTIC_FEATURES.md), [objetos](RUSTIC_ITEMS.md), [bloques](RUSTIC_BLOCKS.md), [registros y almanaque](RUSTIC_REGISTERS.md)
- [Recetas](RUSTIC_RECIPES.md), [expresiones Java de recetas](RUSTIC_RECIPES_JAVA.md), [mecánicas](RUSTIC_MECHANICS.md), [recursos](RUSTIC_ASSETS.md)
- [Paridad](RUSTIC_PARITY_REPORT.md), [pruebas](RUSTIC_TEST_REPORT.md), [diagnóstico de compilación](RUSTIC_BUILD_DIAGNOSTICS.md) y [casos de aceptación pendientes](RUSTIC_ACCEPTANCE_CASES.md)

`original/rustic-pre-1.2.0` y `original/rustic` conservan revisiones upstream distintas; `original/wiki` contiene la wiki upstream. No son código Fabric. Las condiciones de atribución y licencia deben confirmarse antes de distribuir el port; véase [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) y el progreso.

La configuración de desarrollo apunta a Java 25, Minecraft 26.3 y Fabric. GitHub Actions ejecutó `./gradlew build` correctamente para el commit `8b4ad598` y subió un [artefacto de desarrollo](https://github.com/ripblackdragon-cyber/rustic/actions/runs/40985016548), con retención hasta el 6 de noviembre de 2026. El sandbox no pudo descargarlo por restricciones de red; no se ha probado en Minecraft. No es una entrega final. Para compilar localmente se necesitan JDK 25 completo y acceso a los repositorios de dependencias.

Para regenerar los índices se necesita Python 3: `python3 tools/inventory.py`, `python3 tools/recipe_audit.py` y `python3 tools/registry_audit.py`. `python3 tools/migrate_languages.py` y `python3 tools/migrate_textures.py` comprueban las copias; con `--write` crean solo archivos que falten y no sobrescriben diferencias. `python3 tools/verify_upstream.py` verifica las referencias; `python3 -m unittest discover -s tests -v` prueba las herramientas. El [control de recetas](audit/README.md) devuelve fallo mientras existan entradas pendientes. Estas comprobaciones no prueban cobertura completa ni funcionamiento en Minecraft.
