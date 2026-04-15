# Caso de Prueba: CP-BE-013 - Crear publicacion usuario normal

## 1. Datos generales

- ID: CP-BE-013
- Modulo: Backend
- Prioridad: Media
- Tipo: API
- Fecha de ejecucion: 2026-04-14 18:01:34
- Responsable: Nefta + GitHub Copilot
- Commit/version probado: 6ac1f73

## 2. Por que se realiza esta prueba

Validar flujo de moderacion.

## 3. Precondiciones

Comunidad existente y usuario normal autenticado.

## 4. Pasos de ejecucion

1. Hacer POST /comunidades/{id}/publicaciones.

## 5. Resultado esperado

Estado inicial PENDIENTE y es_oficial=false.

## 6. Reaccion esperada de la aplicacion

Publicacion entra a cola de revision.

## 7. Resultado obtenido

- POST /comunidades/1/publicaciones respondio HTTP 200.
- La publicacion creada quedo con estado PENDIENTE y es_oficial=false.

## 8. Reaccion de la aplicacion observada

- El flujo de moderacion para usuarios normales funciono como se espera.
- El contenido no se aprobo automaticamente.

## 9. Evidencia

- Capturas:
- Logs: Pruebas/evidencias/backend/CP-BE-013/resultado.json
- Archivos de salida: Pruebas/evidencias/backend/resumen_pruebas_6_15.json
- Ruta de evidencias sugerida: Pruebas/evidencias/backend/CP-BE-013/

## 10. Estado final

- Estado: PASS
- Defecto relacionado:
- Accion siguiente: validar creacion por staff en CP-BE-014.
