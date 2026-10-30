# Progreso de port: Rustic → Fabric 26.3

**Estado: no terminado; el código inicial compiló en CI y produjo un artefacto de desarrollo, con pruebas acotadas en Minecraft, pero sin paridad.**
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
| 3 Fabric | En curso; compilación CI correcta | [Ejecución 40985016548](https://github.com/ripblackdragon-cyber/rustic/actions/runs/40985016548), commit `8b4ad598`: Java 25, resolución, `./gradlew build` y subida del artefacto de desarrollo correctos. La compilación local sigue bloqueada por TLS y por carecer de JDK completo. Hay pruebas posteriores de arranque GameTest y cliente; atribución upstream pendiente. Ver `RUSTIC_BUILD_DIAGNOSTICS.md`. |
| 4 Registros | En curso, paridad sin verificar | Seis ingredientes (`bee`, `honeycomb`, `beeswax`, `tallow`, `dust_tiny_iron`, `dust_gold`) compilados previamente. Se añaden `wildberries` y `grapes`, con comida y 16 ticks de consumo basados en `ModItems.java:178-189`; compilados y probados parcialmente en GameTest y en un cliente individual. En desarrollo: se añaden aceitunas y una pestaña agrícola parcial con icono original; compilación y pruebas parciales superadas en CI; faltan el contenido restante y comprobaciones visuales. Faltan plantas, obtención y usos completos. |
| 5 Recursos y traducciones | En curso, sin validación en juego | 343 PNG, 32 metadatos de animación y un archivo PDN de la referencia 1.2.0 copiados sin modificar; ocho `.lang` convertidos a JSON preservando claves y valores, con alias modernos para nueve objetos y diez bloques cuando existe la traducción. Modelos de esos diecinueve preparados; faltan la mayoría de modelos, claves y renderizadores y todas las pruebas visuales. |
| 6–8, 10–11 Mecánicas, JEI y multijugador | No iniciadas | Abejas, agricultura, bebidas, alquimia, GUI, integraciones y red todavía no implementadas. |
| 9 Recetas | En curso, paridad sin verificar | Tres JSON de fundición y cinco recetas de pizarra están en curso; las fundiciones correspondientes a `Recipes.java:176,183,184` producen el resultado esperado en hornos de GameTest; faltan XP, avances, configuración, el resto de recetas y validación en JEI. `audit/recipe-parity.json` sigue con 165 fuentes y cuatro bloqueos sin verificar; no prueba paridad. |
| 12 Pruebas de juego | En curso | Seis pruebas de servidor GameTest, cliente singleplayer y un cliente automatizado conectado a servidor dedicado en CI con fuentes de desarrollo; no se probó el JAR instalado, dos clientes ni JEI. |
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

### Uvas y bayas

La [ejecución 40986891133](https://github.com/ripblackdragon-cyber/rustic/actions/runs/40986891133), commit `935811c`, compila los nuevos alimentos y ejecuta tres pruebas Fabric Loader JUnit: valores de nutrición/saturación, si permiten comer con hambre completa y duración del componente consumible. Son pruebas de componentes, sin mundo ni entidades. También compara 403 recursos en el JAR; SHA-256 `83161858e9f012ce93c003d6eb04923c38dd9d1c2fee14de037bee8d805643aa`. La ejecución anterior `40986743999` había fallado en el verificador del informe porque Gradle añadió `()` a los nombres de método; se corrigió y se añadieron pruebas de regresión al verificador. Se realizaron pruebas posteriores en cliente individual y servidor GameTest; siguen pendientes obtención, usos, pestaña original y paridad.

### Pruebas en Minecraft

[CI 40990048384](https://github.com/ripblackdragon-cyber/rustic/actions/runs/40990048384), commit `5ff5aac`: tres GameTests de servidor (registro de 8 objetos, consumo con jugador simulado y 3 hornos), y una prueba de cliente gráfico real con servidor integrado (comer ambos alimentos, interrumpir el uso, probar hambre llena y comprobar estado del servidor). 43 pruebas Python y tres JUnit también pasaron. La captura y los informes están como artefactos; no se ha revisado la captura visualmente. No se aceptó la EULA de un servidor dedicado convencional; no se ha probado multijugador ni se ha instalado el JAR construido.

### Servidor dedicado y reconexión

[CI 41121118254](https://github.com/ripblackdragon-cyber/rustic/actions/runs/41121118254), commit `0c87441`: tras la aceptación explícita de la EULA por el usuario para esta prueba temporal, el cliente automatizado se conectó a un servidor dedicado de prueba local (en el mismo proceso, conexión de red local, modo de prueba sin autenticación). Comió bayas y uvas, se verificó hambre llena para bayas y después se desconectó y reconectó; el servidor restauró una pila de dos uvas y hambre 11. No son dos jugadores, ni se reinició el servidor, ni se probó el JAR instalado. El flujo por defecto no acepta la EULA; CI usa la opción explícita `-PacceptMinecraftEula=true` para esta prueba.

## Siguientes pasos

1. Completar la revisión semántica y determinar correspondencia entre el commit de referencia y el JAR 1.2.0; comprobar licencia y atribuciones originales.
2. Preparar cliente y servidor de pruebas con Java 25 completo y acceso a dependencias; verificar también el contenido del JAR de desarrollo y sus checksums.
3. Migrar por sistemas con trazabilidad de estados, modelos y recetas, usando los mismos datos reales para ejecución y JEI; probar servidor dedicado y cliente con dos jugadores.
4. Actualizar el informe solo cuando haya evidencia verificable para cada elemento. No publicar un JAR como definitivo sin completar todas las pruebas exigidas.

### Ampliación en curso: aceitunas y pestaña agrícola

Se han preparado aceitunas con 1 hambre, saturación 0,8, consumo de 24 ticks y 95 % de probabilidad de náusea II durante 200 ticks sin partículas ni icono, siguiendo `ModItems.java`. Se ha preparado la pestaña agrícola con el icono de aceitunas y los nueve objetos ya migrados; faltan el resto de contenidos y la comprobación visual. Se han añadido pruebas de componentes, GameTest de servidor y consumo en clientes integrado y dedicado. La [CI 41130337280](https://github.com/ripblackdragon-cyber/rustic/actions/runs/41130337280) superó compilación, cuatro JUnit, cuatro GameTests de servidor y consumo de aceitunas con clientes en mundos integrado y dedicado. En la [CI 41130679189](https://github.com/ripblackdragon-cyber/rustic/actions/runs/41130679189), el cliente abrió el inventario creativo y comprobó el título inglés, el icono y los nueve elementos construidos para la pestaña. No se seleccionó la pestaña ni se inspeccionó visualmente su captura. Ni la pestaña ni el port están completos.

### Pizarra en curso

Se prepararon los cinco bloques cúbicos de pizarra, cinco recetas de fabricación, botín, etiqueta de pico y modelos originales. Por ahora aparecen en la pestaña vanilla de construcción porque la pestaña decorativa original tiene como icono el jarrón todavía no migrado. La [CI 41133029468](https://github.com/ripblackdragon-cyber/rustic/actions/runs/41133029468) compiló y probó los cinco registros, su dureza, etiquetas, botín con pico y recetas en GameTest; el cliente colocó y minó pizarra en servidor integrado y dedicado, y comprobó nombres ingleses y contenidos de la pestaña vanilla. Siguen pendientes generación natural, configuración, pilares, escaleras, losas, desbloqueos y JEI. El arbusto de bayas continúa inventariado, sin implementar: véase `RUSTIC_MECHANICS.md`.

### Formas de pizarra en desarrollo

Se han preparado pilar, escaleras y losas de pizarra de techo y de ladrillo, modelos y sus cinco recetas originales. El bloque doble antiguo se representa como estado doble de la losa moderna, con dos losas de botín. La [CI 41133651215](https://github.com/ripblackdragon-cyber/rustic/actions/runs/41133651215) pasó las pruebas de registro, propiedades, rotación de pilar, botín simple y doble y cinco recetas; el cliente comprobó las entradas y traducciones en inglés. Faltan conversión de mundos 1.12, configuración, generación, JEI y comprobación visual; ver el inventario previo en `RUSTIC_MECHANICS.md`.
