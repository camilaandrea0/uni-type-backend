# Cambios en la parte web

Solo se modificaron estos 5 archivos. El resto del proyecto (vista, controlador,
servicio, modelo, repositorio, dto, mapeador, config, persistencia) NO se tocó.

| Archivo | Qué cambió |
|---|---|
| `src/edu/unimag/web/ServidorWeb.java` | Manejo de excepciones, rutas nuevas (activar/desactivar/eliminar), conversión segura de ids |
| `web/js/modulo.js` | Columna ID, botones por fila, validación propia |
| `web/css/modulo.css` | Estilos de la columna ID y de los botones |
| `web/index.html`, `web/modulos.html` | Se quitó un `}` suelto y se corrigió `img\logo.png` por `img/logo.png` |

## Servidor (ServidorWeb)
- **Excepciones**: antes solo se atrapaba `IOException`; cualquier `IllegalArgumentException`
  / `IllegalStateException` cortaba la conexión y la página decía "No se pudo conectar".
  Ahora responden JSON `{"error":"mensaje"}` con código 400 (errores inesperados: 500 y
  se imprime el stack trace en la consola de NetBeans).
- **Nuevas rutas**
  - `POST /api/{modulo}/{id}/activar` y `/desactivar` -> usan el método del servicio de cada
    módulo (activar/inactivar, reactivar/suspender, reactivar/retirarProfesor; profesores usa
    `actualizar` con el estado, porque su servicio no tiene activar/inactivar).
  - `DELETE /api/{modulo}/{id}` -> `CursoPeriodoServicio.eliminar` y
    `FormacionProfesorServicio.eliminarFormacion`. Los demás módulos no borran.
- Los ids numéricos / UUID / fechas mal escritos devuelven un mensaje claro en vez de un error interno.
- `abrirNavegador()` ya no falla si no hay entorno gráfico.

## Página
- Todos los módulos muestran la columna **ID** (UUID incluidos). La asignación no tiene ID propio
  (su llave es curso + profesor).
- En cada lista hay botones **Activar/Desactivar** (Reactivar/Retirar en asignaciones,
  Activar/Inactivar en planes) y **Eliminar** (cursos por período y formación).
- Celular/teléfono: se reemplazó el `minlength` nativo del navegador por una validación propia
  (misma regla del servidor: 7 o más caracteres, sin contar espacios de los extremos) que muestra
  la cantidad real escrita.
