# Caso de Prueba: CP-BE-006 - Perfil autenticado con token valido

## 1. Datos generales

- ID: CP-BE-006
- Modulo: Backend
- Prioridad: Alta
- Tipo: API
- Fecha de ejecucion: 2026-04-14 18:01:34
- Responsable: Nefta + GitHub Copilot
- Commit/version probado: 6ac1f73

## 2. Por que se realiza esta prueba

Asegurar lectura del perfil autenticado.

## 3. Precondiciones

Token JWT valido.

## 4. Pasos de ejecucion

1. Hacer GET /usuarios/me con Authorization Bearer.

## 5. Resultado esperado

HTTP 200 con datos del usuario autenticado.

## 6. Reaccion esperada de la aplicacion

Acceso autorizado al perfil propio.

## 7. Resultado obtenido

- GET /usuarios/me respondio HTTP 200 con el perfil de prueba.normal.20260414180049@uat.edu.mx.
- El body devolvio el usuario autenticado correcto y sin errores de validacion.

## 8. Reaccion de la aplicacion observada

- La API identifico correctamente el token Bearer y regreso el usuario asociado.
- No hubo bloqueos ni errores inesperados.

## 9. Evidencia

- Capturas:
- Logs: Pruebas/evidencias/backend/CP-BE-006/resultado.json
- Archivos de salida: Pruebas/evidencias/backend/resumen_pruebas_6_15.json
- Ruta de evidencias sugerida: Pruebas/evidencias/backend/CP-BE-006/

## 10. Estado final

- Estado: PASS
- Defecto relacionado:
- Accion siguiente: validar token invalido en CP-BE-007.
