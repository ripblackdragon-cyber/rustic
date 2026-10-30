# Control de correspondencia de recetas

`recipe-parity.json` es un registro inicial de **42 archivos JSON** y **123 expresiones Java fuente** extraídas de la revisión fijada. Una expresión Java no equivale necesariamente a una única receta: condiciones, bucles, clases anónimas e integraciones pueden alterar o ampliar las recetas reales. El registro **no es exhaustivo ni demuestra paridad**.

Comando de comprobación: `python3 tools/recipe_parity.py check`. Actualmente debe devolver **1**, porque las 165 entradas están sin verificar (tres en curso) y los cuatro controles de cobertura están pendientes. El comando valida los hashes de la copia original frente a los blobs upstream registrados y comprueba estructura, huellas, identificadores únicos, correspondencias y presencia de archivos citados.

Cada entrada incluye:

- `id`, `source`, `line`, `kind`, `file_sha256`, `fragment_sha256`: identidad y huellas de la referencia. No modificarlos para ocultar un cambio de fuente.
- `status`: `pending`, `in_progress` o `verified`.
- `targets`: rutas relativas a archivos existentes bajo `src/` que implementan el elemento. Puede haber varias porque una expresión puede generar varias recetas.
- `evidence`: rutas relativas a archivos existentes con resultados de pruebas relevantes. Deben describir qué se probó y cuándo; la herramienta solo verifica que los archivos existan, **no** su contenido, calidad ni autenticidad.

No se debe marcar una entrada `verified` hasta implementar y probar sus variantes, condiciones, cantidades y comportamiento real. Hay cuatro bloqueos adicionales obligatorios sobre correspondencia con el JAR publicado, cobertura/expansión de todas las fuentes, pruebas en juego y consistencia JEI/multijugador. No se pueden eliminar del manifiesto para forzar una aprobación. La comprobación automática es un filtro estructural, no puede reemplazar revisión humana ni determinar por sí sola si una prueba pasó o si una receta es correcta.

Para crear un registro inicial desde cero, `python3 tools/recipe_parity.py init` se niega a sobrescribir uno existente. No regenerar el manifiesto para borrar avances. Al ampliar el alcance del índice se debe revisar también el validador y conservar la trazabilidad de los estados anteriores.
