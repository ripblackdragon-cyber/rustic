# Informe de paridad

**La implementación es inicial y no se ha verificado paridad funcional.** La referencia principal investigada es el commit 1.2.0 descrito en [PORT_PROGRESS.md](PORT_PROGRESS.md); su equivalencia exacta con el archivo distribuido no está verificada.

| Área | Original | Fabric 26.3 | Paridad |
|---|---|---|---|
| Bloques, objetos, variantes | Declaraciones y fuentes indexadas | Diez registros de objetos probados en servidor GameTest; cuatro alimentos probados con un cliente automatizado, en singleplayer y servidor dedicado de prueba. Paridad, obtención y usos completos pendientes. Diez bloques de pizarra, 16 de madera pintada y tres paredes de arcilla con sus recetas probados parcialmente; los demás bloques y elementos pendientes | No verificada |
| Texturas y modelos | Copia original e índice SHA-256 | 376 archivos de textura copiados sin cambios; diez modelos de objetos y veintinueve bloques con modelos adaptados; los demás modelos y renderizadores pendientes | No verificada |
| Recetas normales y especiales | JSON y puntos de registro indexados en [RUSTIC_RECIPES.md](RUSTIC_RECIPES.md) | Tres recetas producen el resultado previsto en hornos GameTest; XP, desbloqueos, JEI y las demás recetas pendientes | No verificada |
| Agricultura, generación, abejas | Fuente preservada | Generación de pizarra predeterminada en overworld preparada y comprobada en un mundo normal; agricultura, abejas y paridad de generación pendientes | No verificada |
| Alquimia, bebidas y efectos | Fuente preservada | Sin implementación | No verificada |
| Almanaque, GUI, traducciones | Fuente y recursos preservados | 8 idiomas convertidos a JSON con claves antiguas y alias para diez objetos y veintinueve bloques; pestaña agrícola parcial registrada y contenido comprobado en cliente, GUI y libro sin implementar | No verificada |
| JEI y otras integraciones | Implementación Forge original preservada | Sin implementación | No verificada |
| Sincronización multijugador y persistencia | Implementación Forge original preservada | Sin implementación | No verificada |

Los índices generados no prueban exhaustividad semántica ni equivalencia visual, y no se contabilizan como progreso de migración.
