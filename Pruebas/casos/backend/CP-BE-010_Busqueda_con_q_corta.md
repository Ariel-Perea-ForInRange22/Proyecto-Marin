# Caso de Prueba: CP-BE-010 - Busqueda con q corta

## 1. Datos generales

- ID: CP-BE-010
- Modulo: Backend
- Prioridad: Media
- Tipo: API
- Fecha de ejecucion: 2026-04-14 18:01:34
- Responsable: Nefta + GitHub Copilot
- Commit/version probado: 6ac1f73

## 2. Por que se realiza esta prueba

Validar regla de longitud minima del buscador.

## 3. Precondiciones

Endpoint de busqueda habilitado.

## 4. Pasos de ejecucion

1. Hacer GET /usuarios/buscar?q=a.

## 5. Resultado esperado

Lista vacia.

## 6. Reaccion esperada de la aplicacion

No procesa consultas demasiado cortas.

## 7. Resultado obtenido

- GET /usuarios/buscar?q=a respondio HTTP 200 con lista vacia.
- La longitud minima de busqueda se aplico correctamente.

## 8. Reaccion de la aplicacion observada

- La aplicacion no intento procesar una consulta demasiado corta.
- Se mantuvo una respuesta controlada y predecible.

## 9. Evidencia

- Capturas:
- Logs: Pruebas/evidencias/backend/CP-BE-010/resultado.json
- Archivos de salida: Pruebas/evidencias/backend/resumen_pruebas_6_15.json
- Ruta de evidencias sugerida: Pruebas/evidencias/backend/CP-BE-010/

## 10. Estado final

- Estado: PASS
- Defecto relacionado:
- Accion siguiente: validar busqueda util con coincidencias en CP-BE-011.
