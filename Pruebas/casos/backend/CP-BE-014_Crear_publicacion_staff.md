# Caso de Prueba: CP-BE-014 - Crear publicacion staff

## 1. Datos generales

- ID: CP-BE-014
- Modulo: Backend
- Prioridad: Media
- Tipo: API
- Fecha de ejecucion: 2026-04-14 18:01:34
- Responsable: Nefta + GitHub Copilot
- Commit/version probado: 6ac1f73

## 2. Por que se realiza esta prueba

Validar privilegios de staff.

## 3. Precondiciones

Comunidad existente y usuario staff autenticado.

## 4. Pasos de ejecucion

1. Hacer POST /comunidades/{id}/publicaciones con es_oficial=true.

## 5. Resultado esperado

Estado APROBADA y es_oficial segun payload.

## 6. Reaccion esperada de la aplicacion

Autopublicacion para staff.

## 7. Resultado obtenido

- POST /comunidades/1/publicaciones respondio HTTP 200.
- La publicacion creada quedo con estado APROBADA y es_oficial=true.

## 8. Reaccion de la aplicacion observada

- El usuario staff obtuvo autopublicacion correcta.
- La API respeto el privilegio especial para staff.

## 9. Evidencia

- Capturas:
- Logs: Pruebas/evidencias/backend/CP-BE-014/resultado.json
- Archivos de salida: Pruebas/evidencias/backend/resumen_pruebas_6_15.json
- Ruta de evidencias sugerida: Pruebas/evidencias/backend/CP-BE-014/

## 10. Estado final

- Estado: PASS
- Defecto relacionado:
- Accion siguiente: validar recurso inexistente en CP-BE-015.
