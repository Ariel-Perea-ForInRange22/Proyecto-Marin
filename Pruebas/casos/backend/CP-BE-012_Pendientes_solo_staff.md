# Caso de Prueba: CP-BE-012 - Pendientes solo staff

## 1. Datos generales

- ID: CP-BE-012
- Modulo: Backend
- Prioridad: Media
- Tipo: API
- Fecha de ejecucion: 2026-04-14 18:01:34
- Responsable: Nefta + GitHub Copilot
- Commit/version probado: 6ac1f73

## 2. Por que se realiza esta prueba

Validar autorizacion de moderacion.

## 3. Precondiciones

Un usuario normal y uno staff.

## 4. Pasos de ejecucion

1. Intentar GET /comunidades/{id}/pendientes con usuario normal.
2. Repetir con usuario staff.

## 5. Resultado esperado

Normal=403, staff=200.

## 6. Reaccion esperada de la aplicacion

Control de permisos correcto.

## 7. Resultado obtenido

- El usuario normal recibio HTTP 403 con "No tienes permisos para moderar".
- El usuario staff recibio HTTP 200 y vio 1 publicacion pendiente creada durante la prueba.

## 8. Reaccion de la aplicacion observada

- La moderacion quedo restringida correctamente a staff.
- La publicacion pendiente se mantuvo en estado PENDIENTE y fue visible solo para el usuario autorizado.

## 9. Evidencia

- Capturas:
- Logs: Pruebas/evidencias/backend/CP-BE-012/resultado.json
- Archivos de salida: Pruebas/evidencias/backend/resumen_pruebas_6_15.json
- Ruta de evidencias sugerida: Pruebas/evidencias/backend/CP-BE-012/

## 10. Estado final

- Estado: PASS
- Defecto relacionado:
- Accion siguiente: validar creacion de publicacion por usuario normal en CP-BE-013.
