# Caso de Prueba: CP-BE-002 - Registro usuario nuevo

## 1. Datos generales

- ID: CP-BE-002
- Modulo: Backend
- Prioridad: Alta
- Tipo: API
- Fecha de ejecucion: 2026-03-19 09:18:07
- Responsable: Nefta + GitHub Copilot
- Commit/version probado: 6ac1f73

## 2. Por que se realiza esta prueba

Validar alta correcta y hash de contrasena.

## 3. Precondiciones

Email de prueba no registrado.

## 4. Pasos de ejecucion

1. Hacer POST /usuarios/ con nombre, email y password validos.
2. Consultar usuario creado desde endpoint o DB.

## 5. Resultado esperado

Usuario creado y contrasena almacenada como hash.

## 6. Reaccion esperada de la aplicacion

Alta exitosa sin exponer password plano.

## 7. Resultado obtenido

- POST /usuarios/ respondio HTTP 200.
- Usuario creado: prueba.20260319091807@uat.edu.mx.
- ID asignado en respuesta: 1.
- El endpoint no devolvio password plano; solo datos de perfil.

## 8. Reaccion de la aplicacion observada

- Alta de usuario exitosa y persistida en BD.
- La aplicacion reacciono de forma controlada y devolvio objeto de usuario creado.

## 9. Evidencia

- Capturas:
- Logs: Pruebas/evidencias/backend/CP-BE-002/resultado.json
- Archivos de salida: Pruebas/evidencias/backend/resumen_pruebas_1_5.json
- Ruta de evidencias sugerida: Pruebas/evidencias/backend/CP-BE-002/

## 10. Estado final

- Estado: PASS
- Defecto relacionado:
- Accion siguiente: ejecutar prueba de duplicado CP-BE-003 con el mismo email.
