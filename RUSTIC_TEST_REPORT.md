# Informe de pruebas

Fecha: 2026-10-30. **Hay pruebas acotadas en Minecraft por CI, no una validación integral del port.**

## Pruebas ejecutadas

`python3 -m unittest discover -s tests -q`: **43 pruebas correctas localmente** (incluyen pruebas de los verificadores de informes JUnit y GameTest). La ejecución CI `40985016548`, anterior a esas dos pruebas, ejecutó 32 correctamente. Comprueban:

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

## Pruebas en Minecraft 26.3

[CI 40990048384](https://github.com/ripblackdragon-cyber/rustic/actions/runs/40990048384), commit `5ff5aac`: `./gradlew build --stacktrace --no-daemon` pasó con 43 pruebas Python, tres JUnit y tres GameTests de Minecraft, más la verificación de 403 recursos empaquetados. Se cargaron los mods desde el entorno de desarrollo, no desde el JAR distribuido:

- **Servidor GameTest:** los 8 identificadores corresponden a los objetos registrados tras inicializar el mod. Un jugador simulado intenta usar ambos alimentos con hambre llena y con hambre 10; se verifica el inicio de uso de 16 ticks y, al finalizarlo directamente, la reducción de la pila y los valores de hambre y saturación. La finalización directa no es una simulación de todos los ticks.
- **Hornos del servidor GameTest:** panal→cera, polvo de hierro→pepita y polvo de oro→lingote consumen la entrada y producen una salida correcta con carbón como combustible. No se verificaron la experiencia, desbloqueos, todos los tiempos, JEI o la configuración.
- **Cliente gráfico e integrado:** Minecraft se arrancó con Xvfb y gráficos Mesa por software. En un mundo individual se simuló la pulsación de uso para comer ambos alimentos; se comprobó la pila, hambre y saturación en el servidor integrado. También se verificó que interrumpir el uso tras 8 ticks no consuma el objeto y que con hambre llena no se consuma. El jugador es automatizado, no una persona; no se ha comparado con una ejecución del mod Forge original.

Los artefactos `rustic-minecraft-test-reports`, `rustic-client-gametest-screenshot` y `rustic-fabric-26.3-development` caducan el 6 de noviembre de 2026. La captura no se descargó ni inspeccionó visualmente debido a restricciones de red del sandbox. Para la ejecución posterior el usuario aceptó expresamente la EULA para el entorno temporal de prueba.

### Servidor dedicado y reconexión

[CI 41121118254](https://github.com/ripblackdragon-cyber/rustic/actions/runs/41121118254), commit `0c87441`, completó la compilación, los tres GameTests de servidor y el cliente automatizado. El cliente se conectó por red local a un servidor dedicado de prueba dentro del proceso de GameTest, en modo sin autenticación para localhost: comió bayas y uvas, comprobó el bloqueo de bayas con hambre llena y luego se desconectó y volvió a conectarse. El servidor y el cliente mostraron dos uvas en la mano y hambre 11 tras la reconexión. Este caso no reinicia el servidor, no verifica almacenamiento de bloques ni usa dos clientes; el JAR final tampoco se instaló. El servidor de prueba se ejecutó tras la aceptación expresa de la EULA por el usuario mediante `-PacceptMinecraftEula=true`; sin esa opción no se habilita.

Estas pruebas no demuestran que el commit sea idéntico al JAR publicado, que los recursos se vean correctamente o que todos los comportamientos se hayan identificado. Los casos de aceptación no ejecutados están en [RUSTIC_ACCEPTANCE_CASES.md](RUSTIC_ACCEPTANCE_CASES.md).

## No ejecutadas / bloqueadas

| Prueba requerida | Estado y motivo |
|---|---|
| Compilación Java, resolución Gradle, empaquetado | Correctos en CI para `5337f81`, con comprobación estática del JAR, cuatro JUnit y cuatro GameTests; el intento local falló por restricciones de red y falta de JDK completo. No se ha inspeccionado el JAR descargado localmente. |
| Cliente y servidor Fabric 26.3 | Parcial en CI: cliente singleplayer, servidor GameTest y un cliente automatizado en servidor dedicado de prueba con código de desarrollo. Sin cliente interactivo humano ni prueba del JAR instalado. |
| Todos los bloques, objetos, texturas y recetas en juego | No ejecutada en su totalidad: solo se han implementado y probado subconjuntos. |
| Agricultura, árboles, abejas, alquimia, bebidas, GUI y libro | No ejecutada: sin implementación. |
| JEI, recetas y usos, categorías y servidor | No ejecutada: sin implementación de JEI ni entorno. |
| Dos jugadores, persistencia, guardado y recarga | Parcial: reconexión de un cliente conserva pila y hambre; no se probaron dos clientes, reinicio del servidor ni persistencia de sistemas del port. |

No se ha realizado ninguna prueba de rendimiento, comparación visual ni compatibilidad con otros mods. Ningún requisito de paridad o de entrega está aprobado por estas pruebas.

### Aceitunas y pestaña agrícola parcial

[CI 41130337280](https://github.com/ripblackdragon-cyber/rustic/actions/runs/41130337280), commit `d1180e4`: 44 pruebas Python, cuatro JUnit y cuatro GameTests de servidor. Se verificaron nutrición, saturación, duración y componentes de náusea, y una muestra de 128 consumos con semilla fija que incluye casos con y sin efecto. El cliente automatizado probó consumir aceitunas, interrupción y hambre llena en servidor integrado y en servidor dedicado de prueba. Se verificaron registro e icono de la pestaña en GameTest, pero no su contenido ni su apariencia en el inventario en esta ejecución. No se instalaron los artefactos JAR ni se probaron dos jugadores o JEI.

[CI 41130679189](https://github.com/ripblackdragon-cyber/rustic/actions/runs/41130679189), commit `5337f81`: además abrió el inventario creativo en el cliente y comprobó el título inglés «Rustic Agriculture», el icono de aceitunas y la colección de nueve objetos generada para la pestaña. La prueba no hizo clic en la pestaña y la captura no se ha inspeccionado visualmente. El empaquetado verificó 405 recursos; JAR de desarrollo SHA-256 `14c16126894266cd31cdd75caf2a281938b08c2c2c010fde7936d15b3ccf185c`. No es un JAR final ni se ha probado instalado.

### Bloques cúbicos de pizarra

[CI 41133029468](https://github.com/ripblackdragon-cyber/rustic/actions/runs/41133029468), commit `e1cda81`: 46 pruebas Python, cuatro JUnit y cinco GameTests servidor. El GameTest de pizarra comprobó cinco identificadores de bloque y objeto, dureza 2, etiqueta y requisito de pico, y botín consultado con pico de madera; también cargó y evaluó las cinco recetas con el gestor de recetas real. No probó romper sin herramienta ni todos los escenarios de botín/explosión. El cliente real colocó un bloque de pizarra y lo minó con pico de madera, verificando bloque y objeto obtenido tanto en mundo integrado como en servidor dedicado de prueba. En inventario creativo comprobó presencia de los cinco objetos y nombres ingleses. La captura no se ha inspeccionado visualmente y la pestaña decorativa original aún no existe. No se ha probado generación natural, configuración, libro de recetas, JEI, dos jugadores o JAR instalado.

El JAR de desarrollo pasó comprobación de empaquetado (436 recursos); SHA-256 `d8a480850052575888391af4e461f69ff5e7068323eaf83e0f5aa6530f3dc6f0`. No constituye entrega final.

### Pilares, escaleras y losas de pizarra

[CI 41133651215](https://github.com/ripblackdragon-cyber/rustic/actions/runs/41133651215), commit `675ceac`: compilación, 47 pruebas Python, cuatro JUnit y seis GameTests de servidor. El nuevo GameTest comprobó los cinco registros de bloque y objeto, dureza, etiqueta, botín normal, botín de dos losas para estados dobles, rotación X→Z del pilar, propiedades básicas de escaleras y las cinco recetas en el gestor de recetas. El cliente abrió el inventario creativo y comprobó los diez objetos de pizarra y sus nombres ingleses. En esa ejecución no se probaron colocación interactiva ni minería para las nuevas formas, todos los ángulos, interacciones de agua o aspecto visual. La traducción del bloque doble a estado de losa moderna no convierte automáticamente mundos antiguos.

El JAR de desarrollo empaquetó 474 recursos y su SHA-256 fue `e583244c9d49dde1d04eb77584cd8934c8414459346ed1bf003a16ff6437ae1b`; no se ha probado instalado y no es una entrega final.

[CI 41134042567](https://github.com/ripblackdragon-cyber/rustic/actions/runs/41134042567), commit `1231702`: además, el cliente en mundo integrado colocó dos losas de techo y dos de ladrillo, comprobó que ambas parejas formaran sus respectivos estados dobles y las minó con pico de madera, comprobando que aparecieran o se recogieran dos losas. No se probaron estas interacciones en servidor dedicado, dos jugadores, ni losas superiores o bajo el agua.

### Chile

[CI 41134802880](https://github.com/ripblackdragon-cyber/rustic/actions/runs/41134802880), commit `a1a1bb4`: 48 pruebas Python, cinco JUnit y siete GameTests de servidor; clientes en mundos integrado y dedicado probaron consumo normal, interrupción y consumo con hambre llena, con verificación de velocidad I en servidor. El GameTest realizó 512 consumos inmediatos con jugadores simulados separados y observó casos con y sin un punto de daño, además de verificar 32 ticks y velocidad I de 400 ticks; la muestra es aleatoria, no demuestra estadísticamente la probabilidad exacta. El código conserva el sorteo `nextInt(24) == 0`; no se probaron resistencia al fuego, daño con armadura, cultivos, obtención o dos jugadores. La ejecución anterior [41134653398](https://github.com/ripblackdragon-cyber/rustic/actions/runs/41134653398) falló al compilar una prueba que intentaba acceder a un campo privado, corregido usando jugadores simulados independientes.

### Madera pintada

[CI 41235567309](https://github.com/ripblackdragon-cyber/rustic/actions/runs/41235567309), commit `9e3e472`: 49 pruebas Python, cinco JUnit y ocho GameTests de servidor. El GameTest verificó los 16 registros, dureza, etiqueta de hacha, botín, valores de inflamabilidad 5/20 y las recetas con tablones de roble y madera pintada; comprobó además harina de huesos, tinta, lapislázuli y cacao como tintes históricos. El cliente comprobó la presencia y los nombres ingleses de los 16 objetos, y colocó y minó el bloque gris claro con hacha de madera en mundos integrado y dedicado. No se probó el fuego propagándose, compatibilidad con etiquetas de otros mods, configuración, JEI, ni se inspeccionaron las capturas. La ejecución [41235203757](https://github.com/ripblackdragon-cyber/rustic/actions/runs/41235203757) falló al compilar la primera prueba por nombres Java de tintes no disponibles; se corrigió usando el registro real de objetos.

El JAR de desarrollo verificó 590 recursos, SHA-256 `2de6a1fbf51a550e4c78db208ec74c55317a907ea3c937748bab78e28c0f7aca`; no se ha instalado ni constituye una entrega final.
