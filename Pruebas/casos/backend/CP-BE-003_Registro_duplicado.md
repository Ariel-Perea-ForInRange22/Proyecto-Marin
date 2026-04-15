# Caso de Prueba: CP-BE-003 - Registro duplicado

## 1. Datos generales

- ID: CP-BE-003
- Modulo: Backend
- Prioridad: Alta
- Tipo: API
- Fecha de ejecucion: 2026-03-19 09:18:07
- Responsable: Nefta + GitHub Copilot
- Commit/version probado: 6ac1f73

## 2. Por que se realiza esta prueba

Prevenir cuentas duplicadas por email.

## 3. Precondiciones

Email ya existente.

## 4. Pasos de ejecucion

1. Hacer POST /usuarios/ con email ya registrado.

## 5. Resultado esperado

HTTP 400 con detalle de email duplicado.

## 6. Reaccion esperada de la aplicacion

Rechazo controlado y mensaje claro.

## 7. Resultado obtenido

- Reintento de POST /usuarios/ con el mismo email de CP-BE-002.
- La API respondio HTTP 400.
- Mensaje recibido: "El email ya está registrado".

## 8. Reaccion de la aplicacion observada

- Rechazo controlado del registro duplicado.
- No se genero un segundo usuario con el mismo correo.

## 9. Evidencia

- Capturas:
- Logs: Pruebas/evidencias/backend/CP-BE-003/resultado.json
- Archivos de salida: Pruebas/evidencias/backend/resumen_pruebas_1_5.json
- Ruta de evidencias sugerida: Pruebas/evidencias/backend/CP-BE-003/

## 10. Estado final

- Estado: PASS
- Defecto relacionado:
- Accion siguiente: validar login exitoso con las credenciales creadas.
