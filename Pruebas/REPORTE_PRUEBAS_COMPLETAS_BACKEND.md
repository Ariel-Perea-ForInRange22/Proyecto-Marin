# Reporte Completo de Pruebas Backend - Proyecto Marin

Fecha del reporte: 2026-04-14
Alcance: pruebas backend ejecutadas y documentadas en la carpeta Pruebas
Version/commit de referencia: 6ac1f73

## Resumen Ejecutivo

- Casos ejecutados: 15
- Casos aprobados: 15
- Casos fallidos: 0
- Casos bloqueados: 0

## Detalle de Pruebas

| ID | Caso | Fecha y hora | Estado | Resultado resumido | Evidencia |
|---|---|---|---|---|---|
| CP-BE-001 | Health check de API | 2026-03-19 09:18:07 | PASS | GET / respondio HTTP 200 y el servicio quedo disponible. | [resultado.json](evidencias/backend/CP-BE-001/resultado.json) |
| CP-BE-002 | Registro usuario nuevo | 2026-03-19 09:18:07 | PASS | POST /usuarios/ creo el usuario de prueba correctamente. | [resultado.json](evidencias/backend/CP-BE-002/resultado.json) |
| CP-BE-003 | Registro duplicado | 2026-03-19 09:18:07 | PASS | POST /usuarios/ rechazo el correo repetido con HTTP 400. | [resultado.json](evidencias/backend/CP-BE-003/resultado.json) |
| CP-BE-004 | Login exitoso | 2026-03-19 09:18:29 | PASS | POST /login devolvio token_type=bearer y access_token valido. | [resultado.json](evidencias/backend/CP-BE-004/resultado.json) |
| CP-BE-005 | Login con password invalido | 2026-03-19 09:18:29 | PASS | POST /login rechazo credenciales invalidas con HTTP 401. | [resultado.json](evidencias/backend/CP-BE-005/resultado.json) |
| CP-BE-006 | Perfil autenticado con token valido | 2026-04-14 18:01:34 | PASS | GET /usuarios/me devolvio el perfil correcto del usuario autenticado. | [resultado.json](evidencias/backend/CP-BE-006/resultado.json) |
| CP-BE-007 | Perfil con token invalido o expirado | 2026-04-14 18:01:34 | PASS | GET /usuarios/me rechazo el token manipulado con HTTP 401. | [resultado.json](evidencias/backend/CP-BE-007/resultado.json) |
| CP-BE-008 | PATCH /usuarios/me parcial | 2026-04-14 18:01:34 | PASS | PATCH /usuarios/me actualizo solo los campos enviados y persistio los cambios. | [resultado.json](evidencias/backend/CP-BE-008/resultado.json) |
| CP-BE-009 | Validacion de semestre fuera de rango | 2026-04-14 18:01:34 | PASS | PATCH /usuarios/me rechazo semestre=15 con HTTP 400. | [resultado.json](evidencias/backend/CP-BE-009/resultado.json) |
| CP-BE-010 | Busqueda con q corta | 2026-04-14 18:01:34 | PASS | GET /usuarios/buscar?q=a devolvio lista vacia. | [resultado.json](evidencias/backend/CP-BE-010/resultado.json) |
| CP-BE-011 | Busqueda por nombre o email | 2026-04-14 18:01:34 | PASS | GET /usuarios/buscar?q=Prueba devolvio 2 coincidencias. | [resultado.json](evidencias/backend/CP-BE-011/resultado.json) |
| CP-BE-012 | Pendientes solo staff | 2026-04-14 18:01:34 | PASS | Usuario normal recibio 403 y staff pudo ver 1 publicacion pendiente. | [resultado.json](evidencias/backend/CP-BE-012/resultado.json) |
| CP-BE-013 | Crear publicacion usuario normal | 2026-04-14 18:01:34 | PASS | POST /comunidades/1/publicaciones dejo la publicacion en PENDIENTE. | [resultado.json](evidencias/backend/CP-BE-013/resultado.json) |
| CP-BE-014 | Crear publicacion staff | 2026-04-14 18:01:34 | PASS | POST /comunidades/1/publicaciones aprobo automaticamente la publicacion staff. | [resultado.json](evidencias/backend/CP-BE-014/resultado.json) |
| CP-BE-015 | Perfil de usuario inexistente | 2026-04-14 18:01:34 | PASS | GET /usuarios/99999 devolvio HTTP 404. | [resultado.json](evidencias/backend/CP-BE-015/resultado.json) |

## Observaciones Generales

- Las 15 pruebas backend ejecutadas pasaron.
- Los casos de error esperado tambien se documentaron como PASS porque la aplicacion respondio correctamente con HTTP 400, 401, 403 y 404 segun el escenario.
- La base de datos local y la API quedaron alineadas durante la ejecucion, incluyendo datos de prueba para comunidad, usuario normal y usuario staff.

## Evidencia Consolidada

- [Resumen de las primeras 5 pruebas](evidencias/backend/resumen_pruebas_1_5.json)
- [Resumen de las pruebas 6 a 15](evidencias/backend/resumen_pruebas_6_15.json)

## Estado Final

- Backend validado en su bloque actual.
- Pendiente de extender este reporte a Android y Web Legacy cuando esas pruebas se ejecuten.
