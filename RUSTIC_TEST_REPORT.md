# Informe de pruebas

Fecha: 2026-10-30. **Se ha compilado código inicial en CI, pero no se ha probado en Minecraft.**

## Pruebas ejecutadas

`python3 -m unittest discover -s tests -q`: **39 pruebas correctas localmente** (incluidas dos pruebas estáticas de uvas y bayas y tres del verificador de informes JUnit). La ejecución CI `40985016548`, anterior a esas dos pruebas, ejecutó 32 correctamente. Comprueban:

- Índices de archivos y recursos, validez JSON de recetas originales y extracción de expresiones Java seleccionadas.
- Correspondencia de las tres copias de referencia con los hashes Git registrados; detección de archivos modificados, añadidos y ausentes en un ejemplo sintético.
- Conversión de los ocho idiomas: contenidos originales, BOM, signos `=`, espacios significativos, duplicados, colisiones de nombre y protección frente a sobrescrituras o enlaces simbólicos.
- Copia byte a byte de los 376 archivos de textura, comprobación de firma y CRC de los 343 PNG y análisis JSON de los 32 `.mcmeta`; protección contra sobrescrituras y enlaces simbólicos en ejemplos sintéticos. Esto no es una comparación visual.
- Registro de paridad: detección de entradas omitidas, duplicadas, desconocidas o alteradas, cobertura pendiente, falta de archivos y rutas inseguras. Una prueba sintética confirma también que archivos existentes no prueban comportamiento correcto.
- Comprobaciones estáticas del wrapper, metadatos Fabric, modelos, alias y texturas de seis ingredientes y dos alimentos y campos de tres recetas de fundición. Se han añadido cuatro pruebas cruzadas: identificadores únicos y modelos/texturas, traducción inglesa de respaldo, referencias a objetos registrados y correspondencia de las tres recetas con sus llamadas originales. Se cotejan además las declaraciones de alimento, modelos y traducciones con el original. No compilan Java, no ejecutan alimentos ni cargan recetas en Minecraft.

`python3 tools/verify_upstream.py`: las tres copias coinciden con sus manifiestos de blobs. `python3 tools/migrate_languages.py` y `python3 tools/migrate_textures.py`: las conversiones y copias existentes coinciden con la referencia.

`python3 tools/recipe_parity.py check`: devuelve **1 como se espera**, con **169 sin verificar** (165 entradas de fuente —tres de ellas en curso— y cuatro bloqueos de cobertura) y cero errores estructurales. Indica que no se ha alcanzado la paridad de recetas.

Se ejecutó `JAVA_HOME=/home/user/.cache/rustic-jdk/jdk4py/java-runtime ./gradlew build --stacktrace` con un JRE temporal OpenJDK 25.0.2; **falló con código 1 antes de evaluar el proyecto**, al descargar Gradle desde `services.gradle.org`: `SSLHandshakeException: Remote host terminated the handshake`. Tampoco hay acceso directo a `maven.fabricmc.net`. El JRE no incluye `javac` ni el módulo `jdk.compiler`. Este intento no compiló el mod, no resolvió Fabric y no generó ningún JAR. Se necesitarán un JDK 25 completo y acceso autorizado a los repositorios o una caché de dependencias completa y verificada.

La [ejecución CI 40985016548](https://github.com/ripblackdragon-cyber/rustic/actions/runs/40985016548), para el commit `8b4ad59803e948c4d3542fbc01bc15ab39b7db55`, completó `./gradlew build --stacktrace --no-daemon` y subió el artefacto `rustic-fabric-26.3-development` (caduca el 6 de noviembre de 2026). El sandbox no pudo descargar el artefacto por el bloqueo del host de almacenamiento; el resultado de CI no prueba que Minecraft pueda cargarlo.

La [segunda ejecución CI](https://github.com/ripblackdragon-cyber/rustic/actions/runs/40985252713), commit `db3a25ca`, también pasó: 34 pruebas Python, compilación y comprobación del JAR. Se compararon 399 recursos y dos avisos con los archivos fuente, se comprobaron metadatos y la firma de los dos archivos `.class` principales. `rustic-fabric-26.3-dev-0.0.0-dev.jar` tiene SHA-256 `bfeb8b243bfdc1d6f5295ab874c8a8cce25b1a13297847760d472c29a0857ab1`. Ninguna comprobación carga el mod en el juego.

La [ejecución CI 40986891133](https://github.com/ripblackdragon-cyber/rustic/actions/runs/40986891133) para `935811c` compiló los dos alimentos y ejecutó tres pruebas Java con Fabric Loader JUnit. Estas comprueban los componentes construidos realmente: bayas 2 de nutrición y 2,0 de saturación; uvas 3 y 1,8; ambas no son siempre comestibles y el consumible dura 0,8 segundos (16 ticks). No inicializan el mod ni un mundo; no comprueban registro, alimentación del jugador o lobos, sonido, gráficos, inventario, servidor ni multijugador. En CI también se verificaron 403 recursos del JAR. SHA-256 `83161858e9f012ce93c003d6eb04923c38dd9d1c2fee14de037bee8d805643aa`. El verificador del informe JUnit rechazó inicialmente la ejecución `40986743999` al no reconocer el sufijo `()` en nombres XML; corregido en `935811c` y comprobado en CI.

Estas pruebas no demuestran que el commit sea idéntico al JAR publicado, que los recursos se vean correctamente o que todos los comportamientos se hayan identificado. Los casos de aceptación no ejecutados están en [RUSTIC_ACCEPTANCE_CASES.md](RUSTIC_ACCEPTANCE_CASES.md).

## No ejecutadas / bloqueadas

| Prueba requerida | Estado y motivo |
|---|---|
| Compilación Java, resolución Gradle, empaquetado | Correctos en CI para `935811c`, con comprobación estática del JAR y tres pruebas Java de componentes; el intento local falló por restricciones de red y falta de JDK completo. No se ha inspeccionado el JAR descargado localmente. |
| Cliente y servidor Fabric 26.3 | No ejecutada: no hay entorno de ejecución instalado ni JAR descargado en el sandbox. |
| Todos los bloques, objetos, texturas y recetas en juego | No ejecutada: sin implementación funcional. |
| Agricultura, árboles, abejas, alquimia, bebidas, GUI y libro | No ejecutada: sin implementación. |
| JEI, recetas y usos, categorías y servidor | No ejecutada: sin implementación de JEI ni entorno. |
| Dos jugadores, persistencia, guardado y recarga | No ejecutada: sin implementación ni servidor. |

No se ha realizado ninguna prueba de rendimiento, comparación visual ni compatibilidad con otros mods. Ningún requisito de paridad o de entrega está aprobado por estas pruebas.
