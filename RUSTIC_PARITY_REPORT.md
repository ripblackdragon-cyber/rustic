# Informe de paridad

**La implementación es inicial y no se ha verificado paridad funcional.** La referencia principal investigada es el commit 1.2.0 descrito en [PORT_PROGRESS.md](PORT_PROGRESS.md); su equivalencia exacta con el archivo distribuido no está verificada.

| Área | Original | Fabric 26.3 | Paridad |
|---|---|---|---|
| Bloques, objetos, variantes | Declaraciones y fuentes indexadas | Seis ingredientes compilados en CI; dos alimentos adicionales implementados sin validar compilación o juego en esta revisión. Los bloques y restantes elementos pendientes | No verificada |
| Texturas y modelos | Copia original e índice SHA-256 | 376 archivos de textura copiados sin cambios; ocho modelos de objetos adaptados; los demás modelos y renderizadores pendientes | No verificada |
| Recetas normales y especiales | JSON y puntos de registro indexados en [RUSTIC_RECIPES.md](RUSTIC_RECIPES.md) | Tres recetas de fundición escritas, sin cargar ni probar; las demás pendientes | No verificada |
| Agricultura, generación, abejas | Fuente preservada | Sin implementación | No verificada |
| Alquimia, bebidas y efectos | Fuente preservada | Sin implementación | No verificada |
| Almanaque, GUI, traducciones | Fuente y recursos preservados | 8 idiomas convertidos a JSON con claves antiguas y alias para ocho objetos; GUI y libro sin implementar | No verificada |
| JEI y otras integraciones | Implementación Forge original preservada | Sin implementación | No verificada |
| Sincronización multijugador y persistencia | Implementación Forge original preservada | Sin implementación | No verificada |

Los índices generados no prueban exhaustividad semántica ni equivalencia visual, y no se contabilizan como progreso de migración.
