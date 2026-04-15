# Caso de Prueba: CP-BE-005 - Login con password invalido

## 1. Datos generales

- ID: CP-BE-005
- Modulo: Backend
- Prioridad: Alta
- Tipo: API
- Fecha de ejecucion: 2026-03-19 09:18:29
- Responsable: Nefta + GitHub Copilot
- Commit/version probado: 6ac1f73

## 2. Por que se realiza esta prueba

Validar control de acceso.

## 3. Precondiciones

Usuario existente.

## 4. Pasos de ejecucion

1. Hacer POST /login con password incorrecto.

## 5. Resultado esperado

HTTP 401.

## 6. Reaccion esperada de la aplicacion

Rechazo seguro sin filtrar datos sensibles.

## 7. Resultado obtenido

- POST /login con usuario prueba.20260319091807@uat.edu.mx y password invalido.
- La API respondio HTTP 401.
- Mensaje recibido: "Incorrect email or password".

## 8. Reaccion de la aplicacion observada

- Rechazo de autenticacion controlado.
- No se emitio token y no hubo exposicion de informacion sensible.

## 9. Evidencia

- Capturas:
- Logs: Pruebas/evidencias/backend/CP-BE-005/resultado.json
- Archivos de salida: Pruebas/evidencias/backend/resumen_pruebas_1_5.json
- Ruta de evidencias sugerida: Pruebas/evidencias/backend/CP-BE-005/

## 10. Estado final

- Estado: PASS
- Defecto relacionado:
- Accion siguiente: continuar con CP-BE-006 (perfil autenticado).
