# Diagnóstico de compilación y pruebas

Fecha: 2026-10-30. Rama local `arena/f8eaa5d8-rustic`. **El port no está terminado. CI compiló y ejecutó pruebas limitadas de Minecraft; aún no hay paridad.**

## 1. Fallo observado: descarga de Gradle

Comando ejecutado:

```sh
JAVA_HOME=/home/user/.cache/rustic-jdk/jdk4py/java-runtime ./gradlew build --stacktrace
```

Salida relevante:

```text
Downloading https://services.gradle.org/distributions/gradle-9.7.1-bin.zip
Attempt 1/1 failed. Reason: Remote host terminated the handshake
javax.net.ssl.SSLHandshakeException: Remote host terminated the handshake
```

El proceso terminó con código 1 **antes de evaluar el build**. Este intento local no aporta evidencia sobre el código Java; posteriormente se compiló en CI, como se indica abajo. Las conexiones directas a `services.gradle.org` y `maven.fabricmc.net` también fallaron; estos hosts están fuera de los permitidos en el sandbox. Este intento local no generó ningún JAR del mod.

## Resultado posterior: compilación remota

La [ejecución 40985016548](https://github.com/ripblackdragon-cyber/rustic/actions/runs/40985016548), commit `8b4ad59803e948c4d3542fbc01bc15ab39b7db55`, terminó correctamente: Java 25, copias originales y 32 pruebas Python, `./gradlew build --stacktrace --no-daemon`, y subida del artefacto `rustic-fabric-26.3-development`. El artefacto expira el 6 de noviembre de 2026. El intento de descargarlo al sandbox falló porque el almacenamiento de GitHub Actions está fuera de los hosts permitidos; no se ha inspeccionado aquí el JAR; Minecraft se ejecutó posteriormente en CI. CI informó además de avisos de acciones antiguas; las acciones del flujo se actualizaron y se verificaron en la segunda ejecución.

Una segunda [ejecución 40985252713](https://github.com/ripblackdragon-cyber/rustic/actions/runs/40985252713) del commit `db3a25ca`, con acciones actualizadas, también terminó correctamente. Pasaron las 34 pruebas Python y la verificación de empaquetado: los dos archivos de clase comienzan con la firma Java, los metadatos apuntan a 26.3, y 399 recursos y dos avisos coinciden byte a byte con las fuentes. JAR `rustic-fabric-26.3-dev-0.0.0-dev.jar`, SHA-256 `bfeb8b243bfdc1d6f5295ab874c8a8cce25b1a13297847760d472c29a0857ab1`. Esto no prueba carga ni jugabilidad. El aviso de acciones Node 20 y el de setup-java v4 ya no aparecen; permanece un aviso informativo de migración de `ubuntu-latest`. El artefacto está disponible en la ejecución hasta el 6 de noviembre de 2026; el sandbox no puede descargarlo.

La [ejecución 40986891133](https://github.com/ripblackdragon-cyber/rustic/actions/runs/40986891133) compiló la revisión `935811c` con uvas y bayas, pasó tres pruebas Java de componentes mediante Fabric Loader JUnit, verificó 403 recursos y subió el artefacto de desarrollo. SHA-256 del JAR: `83161858e9f012ce93c003d6eb04923c38dd9d1c2fee14de037bee8d805643aa`. No se ejecutó un mundo ni se validó el registro o consumo dentro del juego. La ejecución `40986743999` compiló y ejecutó Gradle, pero el paso que exigía los tres casos JUnit falló: los nombres XML terminaban en `()`. Se normalizó ese sufijo y la ejecución posterior lo confirmó; hay pruebas Python para sufijos, casos ausentes, duplicados y omitidos o fallidos.

## Cliente gráfico y pruebas de servidor

[CI 40990048384](https://github.com/ripblackdragon-cyber/rustic/actions/runs/40990048384), commit `5ff5aac`, pasó tres GameTests en el servidor de pruebas y un cliente gráfico automatizado de Minecraft 26.3 con un servidor integrado. El JAR se compiló, pero los GameTests usan fuentes de desarrollo: no se probó la instalación del JAR. La ejecución incluye artefactos con informes y captura.

Los primeros intentos de cliente, [40988933608](https://github.com/ripblackdragon-cyber/rustic/actions/runs/40988933608) y [40989383286](https://github.com/ripblackdragon-cyber/rustic/actions/runs/40989383286), fallaron al crear OpenGL bajo Xvfb (`Couldn't find matching GLX visual`) y Vulkan sin `VK_KHR_surface`; no llegaron a la prueba. Añadir Mesa EGL y Vulkan y activar EGL permitió que [40989840643](https://github.com/ripblackdragon-cyber/rustic/actions/runs/40989840643) y la ejecución posterior pasaran. El primer intento de cliente `40988296170` quedó largo tiempo en curso; no se pudo cancelar por HTTP 403 de permisos de la integración. El flujo posterior limita la duración del cliente.

La [ejecución 41121118254](https://github.com/ripblackdragon-cyber/rustic/actions/runs/41121118254) también ejecutó un cliente conectado a un servidor dedicado de prueba y comprobó reconexión con conservación de pila y hambre. Se usó `-PacceptMinecraftEula=true` solo después de que el usuario aceptara expresamente la EULA para el entorno temporal de CI. Es una prueba con un cliente, no con dos; no se reinició el servidor ni se instaló el JAR.

## 2. JDK incompleto

El JRE temporal de `jdk4py` ejecuta OpenJDK 25.0.2, pero no incluye `javac` ni `jdk.compiler`. Por sí solo no sirve para compilar el código. La ruta en caché es específica de este entorno y puede desaparecer.

## 3. Alcance funcional y pruebas

Las 43 pruebas Python pasan localmente en la revisión actual. Incluyen validaciones estáticas de referencias entre los seis ingredientes y dos alimentos, modelos, texturas y traducciones, y correspondencias de tres recetas de fundición. **No ejecutan Java, Minecraft, Fabric ni JEI**; no comprueban el comportamiento de juego. El control de recetas devuelve 1 intencionadamente: 165 entradas no verificadas, incluyendo tres en curso, y cuatro controles de cobertura pendientes.

La mayoría de bloques, objetos, mecánicas, recetas, interfaces, mundo, sincronización e integración JEI aún no están implementados. La pestaña original agrícola y sus iconos también están pendientes: los seis ingredientes y dos alimentos se enumeran por ahora en pestañas vanilla. Las tres recetas se ejercitaron en hornos GameTest; no se han verificado XP ni JEI. Los modelos no se han comparado visualmente. La atribución y la licencia del original necesitan verificación antes de distribuir.

## 4. Para desbloquear pruebas reales

Se requiere un **JDK 25 completo**, acceso autorizado a los repositorios de Gradle, Fabric, Minecraft y JEI, o una caché offline completa y verificable. En ese entorno:

1. En un entorno local preparado, confirmar `java -version` y `javac -version` con Java 25 y ejecutar `./gradlew build --stacktrace`.
2. Comprobar el contenido del JAR y corregir los errores que aparezcan; el build exitoso en CI no verifica jugabilidad.
3. Ejecutar cliente y servidor dedicados Fabric 26.3, probar los seis ingredientes, dos alimentos y tres recetas ya iniciados, y registrar evidencias reproducibles.
4. Migrar el resto de contenido, pruebas y JEI antes de declarar el mod instalable o completo.

Las rutas y comandos de Gradle son de desarrollo; no hay un `rustic-fabric-26.3.jar` definitivo.
