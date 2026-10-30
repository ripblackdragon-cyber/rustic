# Progreso de port: Rustic → Fabric 26.3

**Estado: no terminado; el código inicial compiló en CI y produjo un artefacto de desarrollo, sin pruebas de arranque ni paridad.**
Actualizado: 2026-10-30. La investigación está iniciada, no completada.

## Referencias preservadas

- Rustic oficial: https://github.com/cadaverous-eris/Rustic. `original/rustic-pre-1.2.0` es el commit `88597f5ca4342a6b9d4509d93967ecbef6f8bafa` (2024-12-24), el último commit anterior a la publicación 1.2.0 localizado; `build.gradle` declara 1.2.0. No se ha comparado con el binario publicado, por tanto no se garantiza identidad con él.
- `original/rustic` es `master` en `f40bd6f7c6db3cb6441db78cc11f7e6df2d52bd0` (2025-09-12), que declara 1.3.0: incluye cambios posteriores como antorcha de hierro, etapas de bayas y balance de bebidas. Se conserva separada, no se mezcla silenciosamente con 1.2.0.
- `original/wiki` corresponde a `e7b5286cdc3e2b1bc061bf4e146f20f340080dbd`. Su contenido no está necesariamente sincronizado con ninguna versión y se debe contrastar con código.
- `provenance/*.json` registra los hashes Git de los blobs obtenidos de los árboles de esas revisiones. `python3 tools/verify_upstream.py` verifica las tres copias completas sin red. Esto verifica correspondencia con los árboles consultados, no autenticidad criptográfica de autores ni equivalencia con el JAR publicado.
- El repositorio original no incluye archivo LICENSE en estas revisiones. La ficha del proyecto en [CurseForge](https://www.curseforge.com/minecraft/mc-mods/rustic) declara MIT; no se dispone aquí del texto original ni del titular del copyright. Resolver atribuciones y términos aplicables antes de distribuir un port o recursos. Las copias permanecen intactas como material de referencia, sin afirmar que se ha completado la revisión legal.

## Fases

| Fase | Estado | Evidencia / trabajo pendiente |
|---|---|---|
| 1 Investigación | En curso | Dos revisiones y wiki preservadas; índice de archivos, lectura inicial de registros y mecánicas. Falta examinar semánticamente todos los archivos y cotejar binario y changelog. |
| 2 Auditoría | En curso | Índices reproducibles de declaraciones, hierbas, registros, entradas del almanaque y recursos; 42 recetas JSON y 123 expresiones Java con argumentos íntegros en `RUSTIC_RECIPES_JAVA.md`. Se verificaron las copias frente a los blobs de los árboles upstream. Faltan variantes metadata/NBT, llamadas dinámicas, dependencias condicionales y revisión visual. |
| 3 Fabric | En curso; compilación CI correcta | [Ejecución 40985016548](https://github.com/ripblackdragon-cyber/rustic/actions/runs/40985016548), commit `8b4ad598`: Java 25, resolución, `./gradlew build` y subida del artefacto de desarrollo correctos. La compilación local sigue bloqueada por TLS y por carecer de JDK completo. Sin pruebas de arranque y con atribución upstream pendiente. Ver `RUSTIC_BUILD_DIAGNOSTICS.md`. |
| 4 Registros | En curso, sin probar | Código inicial para seis ingredientes (`bee`, `honeycomb`, `beeswax`, `tallow`, `dust_tiny_iron`, `dust_gold`). Compilados en CI; sin confirmación de ejecución. Se muestran en la pestaña vanilla de ingredientes como desviación de desarrollo; hay que sustituirla por la pestaña agrícola original con su icono de aceitunas. No están implementados sus usos completos. |
| 5 Recursos y traducciones | En curso, sin validación en juego | 343 PNG, 32 metadatos de animación y un archivo PDN de la referencia 1.2.0 copiados sin modificar; ocho `.lang` convertidos a JSON preservando claves y valores, con alias modernos para seis ingredientes cuando existe la traducción. Modelos de los seis preparados; faltan la mayoría de modelos, claves y renderizadores y todas las pruebas visuales. |
| 6–8, 10–11 Mecánicas, JEI y multijugador | No iniciadas | Abejas, agricultura, bebidas, alquimia, GUI, integraciones y red todavía no implementadas. |
| 9 Recetas | En curso, sin probar | Tres JSON de fundición correspondientes a `Recipes.java:176,183,184`; faltan avances, configuración, el resto de recetas y validación en juego/JEI. `audit/recipe-parity.json` sigue con 165 fuentes y cuatro bloqueos sin verificar; no prueba paridad. |
| 12 Pruebas de juego | No iniciadas | No se ha ejecutado cliente ni servidor con el JAR de desarrollo. |
| 13 JAR definitivo | No iniciado | No existe `rustic-fabric-26.3.jar`. |

## Dependencias utilizadas en la compilación de desarrollo

La configuración actual fija Minecraft 26.3, Java 25, Fabric Loader 0.20.1, Fabric API 0.165.0+26.3, Loom 1.18.4 y Gradle 9.7.1. Una [configuración externa para 26.3](https://github.com/kous500/CurveBuilding/pull/63) documenta esta combinación de Loom, Gradle, Loader y API; el build de CI confirma la compilación de este código inicial, no su comportamiento. Se usa la variante Loom para el juego no ofuscado, sin mapeos antiguos. El wrapper y sus scripts se obtuvieron del árbol oficial Gradle `v9.7.1`: blobs Git `eddabd2eef8d94a5437d6168ff9c87a78ff725b3` (JAR), `203529cbc6f40b75a82fe2b6a6e3f8d5bc2efd55` (Unix) y `7e60b7292031b071dcd74102d12ed7abfc196791` (Windows); el wrapper fija el SHA-256 de la distribución. Su primera descarga falló por TLS al intentar acceder a `services.gradle.org`.

- [Mojang: Minecraft Java 26.3](https://www.minecraft.net/en-us/article/minecraft-java-edition-26-3) y [Fabric para 26.3](https://fabricmc.net/2026/09/15/263.html): Fabric recomienda en su anuncio Loom 1.17, Gradle 9.6.0 y Loader 0.19.5.
- [JEI rama 26.3, gradle.properties](https://github.com/mezz/JustEnoughItems/blob/5b4707e0d0b2179c28be2f1e8f08d4a31b01e5f1/gradle.properties): Java 25, Loader 0.19.5 y Fabric API 0.161.0+26.3. Esto demuestra una configuración usada por JEI, no una combinación probada para este port.
- [JEI Fabric 26.3](https://www.curseforge.com/minecraft/mc-mods/jei/files/8895176): existe un archivo beta 31.0.0.3; no se ha comprobado su API ni instalado. Seleccionar una versión estable únicamente tras comprobar su publicación y artefactos.
- [Ejemplo oficial Fabric rama 26.3](https://github.com/FabricMC/fabric-example-mod/tree/91f19010a4e764f193fe50acfe2c8ee68f1f15cd) usa Loader 0.20.1, API 0.165.0+26.3, Loom 1.19-SNAPSHOT y Gradle 9.8.2. Es una configuración distinta y Loom es snapshot; no se debe mezclar arbitrariamente con la del anuncio.

Los repositorios de dependencias no están entre los hosts permitidos para conexiones locales del sandbox. La resolución y compilación se efectuaron en GitHub Actions, no en este entorno; no se ha intentado eludir la restricción.

## Comprobación adicional de CI

La [ejecución 40985252713](https://github.com/ripblackdragon-cyber/rustic/actions/runs/40985252713) del commit `db3a25ca` compiló, ejecutó 34 pruebas Python, verificó metadatos, firmas de clase, 399 recursos y dos avisos en el JAR de desarrollo y subió el artefacto. SHA-256 del JAR: `bfeb8b243bfdc1d6f5295ab874c8a8cce25b1a13297847760d472c29a0857ab1`. No sustituye pruebas de juego ni resuelve la atribución.

## Siguientes pasos

1. Completar la revisión semántica y determinar correspondencia entre el commit de referencia y el JAR 1.2.0; comprobar licencia y atribuciones originales.
2. Preparar cliente y servidor de pruebas con Java 25 completo y acceso a dependencias; verificar también el contenido del JAR de desarrollo y sus checksums.
3. Migrar por sistemas con trazabilidad de estados, modelos y recetas, usando los mismos datos reales para ejecución y JEI; probar servidor dedicado y cliente con dos jugadores.
4. Actualizar el informe solo cuando haya evidencia verificable para cada elemento. No publicar un JAR como definitivo sin completar todas las pruebas exigidas.
