# Informe de pruebas

Fecha: 2026-10-30. **Se ha compilado código inicial en CI, pero no se ha probado en Minecraft.**

## Pruebas ejecutadas

`python3 -m unittest discover -s tests -q`: **34 pruebas correctas localmente** (incluidas dos pruebas sintéticas adicionales del verificador de JAR). La ejecución CI `40985016548`, anterior a esas dos pruebas, ejecutó 32 correctamente. Comprueban:

- Índices de archivos y recursos, validez JSON de recetas originales y extracción de expresiones Java seleccionadas.
- Correspondencia de las tres copias de referencia con los hashes Git registrados; detección de archivos modificados, añadidos y ausentes en un ejemplo sintético.
- Conversión de los ocho idiomas: contenidos originales, BOM, signos `=`, espacios significativos, duplicados, colisiones de nombre y protección frente a sobrescrituras o enlaces simbólicos.
- Copia byte a byte de los 376 archivos de textura, comprobación de firma y CRC de los 343 PNG y análisis JSON de los 32 `.mcmeta`; protección contra sobrescrituras y enlaces simbólicos en ejemplos sintéticos. Esto no es una comparación visual.
- Registro de paridad: detección de entradas omitidas, duplicadas, desconocidas o alteradas, cobertura pendiente, falta de archivos y rutas inseguras. Una prueba sintética confirma también que archivos existentes no prueban comportamiento correcto.
- Comprobaciones estáticas del wrapper, metadatos Fabric, modelos, alias y texturas de seis ingredientes y campos de tres recetas de fundición. Se han añadido cuatro pruebas cruzadas: identificadores únicos y modelos/texturas, traducción inglesa de respaldo, referencias a objetos registrados y correspondencia de las tres recetas con sus llamadas originales. No compilan Java ni cargan las recetas en Minecraft.

`python3 tools/verify_upstream.py`: las tres copias coinciden con sus manifiestos de blobs. `python3 tools/migrate_languages.py` y `python3 tools/migrate_textures.py`: las conversiones y copias existentes coinciden con la referencia.

`python3 tools/recipe_parity.py check`: devuelve **1 como se espera**, con **169 sin verificar** (165 entradas de fuente —tres de ellas en curso— y cuatro bloqueos de cobertura) y cero errores estructurales. Indica que no se ha alcanzado la paridad de recetas.

Se ejecutó `JAVA_HOME=/home/user/.cache/rustic-jdk/jdk4py/java-runtime ./gradlew build --stacktrace` con un JRE temporal OpenJDK 25.0.2; **falló con código 1 antes de evaluar el proyecto**, al descargar Gradle desde `services.gradle.org`: `SSLHandshakeException: Remote host terminated the handshake`. Tampoco hay acceso directo a `maven.fabricmc.net`. El JRE no incluye `javac` ni el módulo `jdk.compiler`. Este intento no compiló el mod, no resolvió Fabric y no generó ningún JAR. Se necesitarán un JDK 25 completo y acceso autorizado a los repositorios o una caché de dependencias completa y verificada.

La [ejecución CI 40985016548](https://github.com/ripblackdragon-cyber/rustic/actions/runs/40985016548), para el commit `8b4ad59803e948c4d3542fbc01bc15ab39b7db55`, completó `./gradlew build --stacktrace --no-daemon` y subió el artefacto `rustic-fabric-26.3-development` (caduca el 6 de noviembre de 2026). El sandbox no pudo descargar el artefacto por el bloqueo del host de almacenamiento; el resultado de CI no prueba que Minecraft pueda cargarlo.

Estas pruebas no demuestran que el commit sea idéntico al JAR publicado, que los recursos se vean correctamente o que todos los comportamientos se hayan identificado. Los casos de aceptación no ejecutados están en [RUSTIC_ACCEPTANCE_CASES.md](RUSTIC_ACCEPTANCE_CASES.md).

## No ejecutadas / bloqueadas

| Prueba requerida | Estado y motivo |
|---|---|
| Compilación Java, resolución Gradle, empaquetado | Correctos en CI para `8b4ad598`; el intento local falló por restricciones de red y falta de JDK completo. No se ha inspeccionado el JAR descargado localmente. |
| Cliente y servidor Fabric 26.3 | No ejecutada: no hay entorno de ejecución instalado ni JAR descargado en el sandbox. |
| Todos los bloques, objetos, texturas y recetas en juego | No ejecutada: sin implementación funcional. |
| Agricultura, árboles, abejas, alquimia, bebidas, GUI y libro | No ejecutada: sin implementación. |
| JEI, recetas y usos, categorías y servidor | No ejecutada: sin implementación de JEI ni entorno. |
| Dos jugadores, persistencia, guardado y recarga | No ejecutada: sin implementación ni servidor. |

No se ha realizado ninguna prueba de rendimiento, comparación visual ni compatibilidad con otros mods. Ningún requisito de paridad o de entrega está aprobado por estas pruebas.
