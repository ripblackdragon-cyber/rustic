# Informe de pruebas

Fecha: 2026-10-30. **No se ha probado un port de Minecraft.**

## Pruebas ejecutadas

`python3 -m unittest discover -s tests -v`: **32 pruebas correctas**. Comprueban:

- Índices de archivos y recursos, validez JSON de recetas originales y extracción de expresiones Java seleccionadas.
- Correspondencia de las tres copias de referencia con los hashes Git registrados; detección de archivos modificados, añadidos y ausentes en un ejemplo sintético.
- Conversión de los ocho idiomas: contenidos originales, BOM, signos `=`, espacios significativos, duplicados, colisiones de nombre y protección frente a sobrescrituras o enlaces simbólicos.
- Copia byte a byte de los 376 archivos de textura, comprobación de firma y CRC de los 343 PNG y análisis JSON de los 32 `.mcmeta`; protección contra sobrescrituras y enlaces simbólicos en ejemplos sintéticos. Esto no es una comparación visual.
- Registro de paridad: detección de entradas omitidas, duplicadas, desconocidas o alteradas, cobertura pendiente, falta de archivos y rutas inseguras. Una prueba sintética confirma también que archivos existentes no prueban comportamiento correcto.
- Comprobaciones estáticas del wrapper, metadatos Fabric, modelos, alias y texturas de seis ingredientes y campos de tres recetas de fundición. Se han añadido cuatro pruebas cruzadas: identificadores únicos y modelos/texturas, traducción inglesa de respaldo, referencias a objetos registrados y correspondencia de las tres recetas con sus llamadas originales. No compilan Java ni cargan las recetas en Minecraft.

`python3 tools/verify_upstream.py`: las tres copias coinciden con sus manifiestos de blobs. `python3 tools/migrate_languages.py` y `python3 tools/migrate_textures.py`: las conversiones y copias existentes coinciden con la referencia.

`python3 tools/recipe_parity.py check`: devuelve **1 como se espera**, con **169 sin verificar** (165 entradas de fuente —tres de ellas en curso— y cuatro bloqueos de cobertura) y cero errores estructurales. Indica que no se ha alcanzado la paridad de recetas.

Se ejecutó `JAVA_HOME=/home/user/.cache/rustic-jdk/jdk4py/java-runtime ./gradlew build --stacktrace` con un JRE temporal OpenJDK 25.0.2; **falló con código 1 antes de evaluar el proyecto**, al descargar Gradle desde `services.gradle.org`: `SSLHandshakeException: Remote host terminated the handshake`. Tampoco hay acceso directo a `maven.fabricmc.net`. El JRE no incluye `javac` ni el módulo `jdk.compiler`. Este intento no compiló el mod, no resolvió Fabric y no generó ningún JAR. Se necesitarán un JDK 25 completo y acceso autorizado a los repositorios o una caché de dependencias completa y verificada.

Estas pruebas no demuestran que el commit sea idéntico al JAR publicado, que los recursos se vean correctamente o que todos los comportamientos se hayan identificado. Los casos de aceptación no ejecutados están en [RUSTIC_ACCEPTANCE_CASES.md](RUSTIC_ACCEPTANCE_CASES.md).

## No ejecutadas / bloqueadas

| Prueba requerida | Estado y motivo |
|---|---|
| Compilación Java, resolución Gradle, empaquetado | No ejecutada: el wrapper no pudo descargar Gradle; solo se dispone de un JRE temporal, sin compilador. Los hosts de dependencias están fuera del acceso de red permitido. |
| Cliente y servidor Fabric 26.3 | No ejecutada: no existe JAR del port ni entorno de ejecución instalado. |
| Todos los bloques, objetos, texturas y recetas en juego | No ejecutada: sin implementación funcional. |
| Agricultura, árboles, abejas, alquimia, bebidas, GUI y libro | No ejecutada: sin implementación. |
| JEI, recetas y usos, categorías y servidor | No ejecutada: sin implementación de JEI ni entorno. |
| Dos jugadores, persistencia, guardado y recarga | No ejecutada: sin implementación ni servidor. |

No se ha realizado ninguna prueba de rendimiento, comparación visual ni compatibilidad con otros mods. Ningún requisito de paridad o de entrega está aprobado por estas pruebas.
