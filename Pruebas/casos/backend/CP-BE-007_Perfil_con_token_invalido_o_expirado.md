# Caso de Prueba: CP-BE-007 - Perfil con token invalido o expirado

## 1. Datos generales

- ID: CP-BE-007
- Modulo: Backend
- Prioridad: Alta
- Tipo: API
- Fecha de ejecucion: 2026-04-14 18:01:34
- Responsable: Nefta + GitHub Copilot
- Commit/version probado: 6ac1f73

## 2. Por que se realiza esta prueba

Bloquear acceso con credenciales invalidas.

## 3. Precondiciones

Token mal formado o expirado.

## 4. Pasos de ejecucion

1. Hacer GET /usuarios/me con token invalido.

## 5. Resultado esperado

HTTP 401.

## 6. Reaccion esperada de la aplicacion

Denegacion controlada.

## 7. Resultado obtenido

- GET /usuarios/me con token manipulado respondio HTTP 401.
- La API rechazo el token y no devolvio informacion del perfil.

## 8. Reaccion de la aplicacion observada

- La autenticacion fallo de forma controlada.
- Se confirma que un token invalido no da acceso al endpoint protegido.

## 9. Evidencia

- Capturas:
- Logs: Pruebas/evidencias/backend/CP-BE-007/resultado.json
- Archivos de salida: Pruebas/evidencias/backend/resumen_pruebas_6_15.json
- Ruta de evidencias sugerida: Pruebas/evidencias/backend/CP-BE-007/

## 10. Estado final

- Estado: PASS
- Defecto relacionado:
- Accion siguiente: probar actualizacion parcial del perfil en CP-BE-008.
