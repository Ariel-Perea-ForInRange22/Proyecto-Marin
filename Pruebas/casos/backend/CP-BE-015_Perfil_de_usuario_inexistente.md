# Caso de Prueba: CP-BE-015 - Perfil de usuario inexistente

## 1. Datos generales

- ID: CP-BE-015
- Modulo: Backend
- Prioridad: Media
- Tipo: API
- Fecha de ejecucion: 2026-04-14 18:01:34
- Responsable: Nefta + GitHub Copilot
- Commit/version probado: 6ac1f73

## 2. Por que se realiza esta prueba

Validar manejo de recursos no encontrados.

## 3. Precondiciones

ID inexistente.

## 4. Pasos de ejecucion

1. Hacer GET /usuarios/{id_inexistente}.

## 5. Resultado esperado

HTTP 404.

## 6. Reaccion esperada de la aplicacion

Error controlado y sin caida.

## 7. Resultado obtenido

- GET /usuarios/99999 respondio HTTP 404.
- La API indico correctamente que el usuario no existe.

## 8. Reaccion de la aplicacion observada

- Se manejo el recurso inexistente de forma controlada.
- No hubo caida ni excepciones en el backend.

## 9. Evidencia

- Capturas:
- Logs: Pruebas/evidencias/backend/CP-BE-015/resultado.json
- Archivos de salida: Pruebas/evidencias/backend/resumen_pruebas_6_15.json
- Ruta de evidencias sugerida: Pruebas/evidencias/backend/CP-BE-015/

## 10. Estado final

- Estado: PASS
- Defecto relacionado:
- Accion siguiente: cerrar la tanda de backend o pasar a la siguiente fase.
