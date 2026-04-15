# Caso de Prueba: CP-BE-011 - Busqueda por nombre o email

## 1. Datos generales

- ID: CP-BE-011
- Modulo: Backend
- Prioridad: Media
- Tipo: API
- Fecha de ejecucion: 2026-04-14 18:01:34
- Responsable: Nefta + GitHub Copilot
- Commit/version probado: 6ac1f73

## 2. Por que se realiza esta prueba

Asegurar busqueda util para exploracion de perfiles.

## 3. Precondiciones

Usuarios con nombres/correos variados.

## 4. Pasos de ejecucion

1. Hacer GET /usuarios/buscar?q=termino.

## 5. Resultado esperado

Coincidencias parciales, maximo 20 resultados.

## 6. Reaccion esperada de la aplicacion

Resultados relevantes y acotados.

## 7. Resultado obtenido

- GET /usuarios/buscar?q=Prueba respondio HTTP 200.
- Se obtuvieron 2 coincidencias, dentro del limite maximo de 20 resultados.

## 8. Reaccion de la aplicacion observada

- El buscador devolvio resultados relevantes para usuarios con nombre/correo de prueba.
- La respuesta fue estable y acotada.

## 9. Evidencia

- Capturas:
- Logs: Pruebas/evidencias/backend/CP-BE-011/resultado.json
- Archivos de salida: Pruebas/evidencias/backend/resumen_pruebas_6_15.json
- Ruta de evidencias sugerida: Pruebas/evidencias/backend/CP-BE-011/

## 10. Estado final

- Estado: PASS
- Defecto relacionado:
- Accion siguiente: validar permisos de moderacion en CP-BE-012.
