# Caso de Prueba: CP-BE-001 - Health check de API

## 1. Datos generales

- ID: CP-BE-001
- Modulo: Backend
- Prioridad: Alta
- Tipo: API
- Fecha de ejecucion: 2026-03-19 09:18:07
- Responsable: Nefta + GitHub Copilot
- Commit/version probado: 6ac1f73

## 2. Por que se realiza esta prueba

Confirmar disponibilidad basica del servicio.

## 3. Precondiciones

Servidor FastAPI arriba en puerto 8000.

## 4. Pasos de ejecucion

1. Hacer GET a /.

## 5. Resultado esperado

HTTP 200 con mensaje de estado.

## 6. Reaccion esperada de la aplicacion

Respuesta inmediata sin error.

## 7. Resultado obtenido

- GET / respondio HTTP 200.
- Body recibido: {"status":"En desarrollo","message":"API de la Comunidad Universitaria funcionando."}

## 8. Reaccion de la aplicacion observada

- La API respondio de forma inmediata, sin errores de transporte ni excepciones.
- Se confirma disponibilidad basica del servicio.

## 9. Evidencia

- Capturas:
- Logs: Pruebas/evidencias/backend/CP-BE-001/resultado.json
- Archivos de salida: Pruebas/evidencias/backend/resumen_pruebas_1_5.json
- Ruta de evidencias sugerida: Pruebas/evidencias/backend/CP-BE-001/

## 10. Estado final

- Estado: PASS
- Defecto relacionado:
- Accion siguiente: continuar con CP-BE-002.
