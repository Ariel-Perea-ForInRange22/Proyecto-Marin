# Caso de Prueba: CP-BE-009 - Validacion de semestre fuera de rango

## 1. Datos generales

- ID: CP-BE-009
- Modulo: Backend
- Prioridad: Alta
- Tipo: API
- Fecha de ejecucion: 2026-04-14 18:01:34
- Responsable: Nefta + GitHub Copilot
- Commit/version probado: 6ac1f73

## 2. Por que se realiza esta prueba

Proteger consistencia de datos.

## 3. Precondiciones

Usuario autenticado.

## 4. Pasos de ejecucion

1. Hacer PATCH /usuarios/me con semestre=0 y luego semestre=15.

## 5. Resultado esperado

HTTP 400 en ambos casos.

## 6. Reaccion esperada de la aplicacion

Validacion de negocio activa.

## 7. Resultado obtenido

- PATCH /usuarios/me con semestre=15 respondio HTTP 400.
- La API rechazo el valor fuera de rango con el mensaje de validacion esperado.

## 8. Reaccion de la aplicacion observada

- La validacion de negocio se comporto correctamente.
- No se altero el semestre del usuario.

## 9. Evidencia

- Capturas:
- Logs: Pruebas/evidencias/backend/CP-BE-009/resultado.json
- Archivos de salida: Pruebas/evidencias/backend/resumen_pruebas_6_15.json
- Ruta de evidencias sugerida: Pruebas/evidencias/backend/CP-BE-009/

## 10. Estado final

- Estado: PASS
- Defecto relacionado:
- Accion siguiente: validar busqueda con q corta en CP-BE-010.
