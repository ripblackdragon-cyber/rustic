# Casos de aceptación pendientes

Estos son **casos de aceptación aún no completados**; algunos tienen pruebas parciales descritas en [RUSTIC_TEST_REPORT.md](RUSTIC_TEST_REPORT.md). La fuente de referencia es el commit 1.2.0 de [PORT_PROGRESS.md](PORT_PROGRESS.md). Ejecutarlos contra el mod original y el port cuando existan entornos; documentar resultados observados y diferencias. Las comprobaciones deben ejecutarse tanto en servidor dedicado como en cliente donde corresponda.

| ID | Preparación y acción | Resultado o propiedad a verificar | Estado |
|---|---|---|---|
| FOOD-01 | Consumir uvas y bayas silvestres con hambre variable en cliente y servidor; repetir con hambre completa. | Comparar duración de 16 ticks, puntos de hambre, saturación, bloqueo con hambre completa y reducción de pila; verificar que no puedan alimentar lobos. Comparar con Forge original. | Parcial en CI; falta comparación y lobos |
| BEE-01 | Insertar 1, 20 y 64 abejas en apiarios separados, multiplicadores en 1. | Intervalos y límites según fórmula de `TileEntityApiary`; panal y reproducción independientes. | Pendiente |
| BEE-02 | Automatizar inserción por arriba/lateral y extracción por abajo; romper bloque. | Las caras exponen los manejadores originales; objetos expulsados una sola vez. | Pendiente |
| BEE-03 | Guardar/recargar antes de completar reproducción. | Comparar contadores y pilas con original: la revisión no guarda contadores. | Pendiente |
| BREW-01 | Fermentar entrada válida, sin cultivo, 12000 ticks por defecto. | Igual volumen dentro del espacio libre; calidad 0,05…0,75 inclusive y tipo correcto. | Pendiente |
| BREW-02 | Añadir cultivo igual con calidad y cambiar el líquido durante el proceso. | Cambiar fluido reinicia tiempo; cultivo no se consume; calidad truncada y limitada según código y configuración. | Pendiente |
| BREW-03 | Fermentar con salida parcialmente ocupada del mismo fluido con calidad conocida. | Se mantiene la calidad de la salida existente y se respeta capacidad. | Pendiente |
| BREW-04 | Usar ambrosía con y sin cultivo, y probar calidades <0,5, 0,5 y >0,99. | Comparar distribución específica, daño y duración de `UNDYING`; no aplicar reglas genéricas de receta. | Pendiente |
| BREW-05 | Dos jugadores y automatización interactúan con las tres entradas mientras se completa una tanda y se recarga el mundo. | Sin duplicación/pérdida; cantidades y estado idénticos en servidor y clientes. | Pendiente |
| ALC-01 | Condensador simple y avanzado con ingredientes válidos, agua y botella. | Comprobar 125 mB, tiempos 400/300, consumos y efecto, usando cada receta. | Pendiente |
| ALC-02 | Variar orden, metadatos, NBT, modificador y botella; repetir con cantidad insuficiente de agua. | Coincidencia y rechazo como en el original; investigar especialmente la comparación NBT. | Pendiente |
| REC-01 | Para cada receta JSON y expresión auditada, ejercitar cuadrícula o estación, incluyendo configuración e integraciones. | Comparar ingredientes, cantidades, metadatos, residuos, condiciones y resultado; ninguna receta simulada. | Pendiente |
| JEI-01 | Consultar ingredientes, usos y procesos en cliente conectado a servidor. | Resultados idénticos a la colección de recetas efectiva del servidor, incluso después de recarga. | Pendiente |
| ASSET-01 | Inspeccionar cada variante y animación frente a imágenes 1.12.2 en condiciones comparables. | Misma apariencia, dimensiones, transparencia y orientación. El hash del archivo no demuestra este resultado. | Pendiente |
