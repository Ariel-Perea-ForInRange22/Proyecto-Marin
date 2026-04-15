# Caso de Prueba: CP-BE-004 - Login exitoso

## 1. Datos generales

- ID: CP-BE-004
- Modulo: Backend
- Prioridad: Alta
- Tipo: API
- Fecha de ejecucion: 2026-03-19 09:18:29
- Responsable: Nefta + GitHub Copilot
- Commit/version probado: 6ac1f73

## 2. Por que se realiza esta prueba

Validar emision de JWT para clientes.

## 3. Precondiciones

Usuario valido existente.

## 4. Pasos de ejecucion

1. Hacer POST /login con username/password validos.

## 5. Resultado esperado

HTTP 200 con access_token y token_type=bearer.

## 6. Reaccion esperada de la aplicacion

Autenticacion correcta y token usable.

## 7. Resultado obtenido

- POST /login con usuario prueba.20260319091807@uat.edu.mx y password valido.
- La API respondio HTTP 200.
- Se recibio token_type=bearer y access_token (registrado como preview en evidencia).

## 8. Reaccion de la aplicacion observada

- Autenticacion correcta y emision de JWT utilizable para endpoints protegidos.
- Flujo de login se comporto de forma estable y sin errores.

## 9. Evidencia

- Capturas:
- Logs: Pruebas/evidencias/backend/CP-BE-004/resultado.json
- Archivos de salida: Pruebas/evidencias/backend/resumen_pruebas_1_5.json
- Ruta de evidencias sugerida: Pruebas/evidencias/backend/CP-BE-004/

## 10. Estado final

- Estado: PASS
- Defecto relacionado:
- Accion siguiente: ejecutar login con password invalido (CP-BE-005).
