# Caso de Prueba: CP-BE-008 - PATCH /usuarios/me parcial

## 1. Datos generales

- ID: CP-BE-008
- Modulo: Backend
- Prioridad: Alta
- Tipo: API
- Fecha de ejecucion: 2026-04-14 18:01:34
- Responsable: Nefta + GitHub Copilot
- Commit/version probado: 6ac1f73

## 2. Por que se realiza esta prueba

Validar actualizacion parcial sin afectar campos no enviados.

## 3. Precondiciones

Usuario autenticado.

## 4. Pasos de ejecucion

1. Hacer PATCH /usuarios/me enviando solo 1 o 2 campos.
2. Consultar perfil despues de guardar.

## 5. Resultado esperado

Solo cambian campos enviados; el resto permanece igual.

## 6. Reaccion esperada de la aplicacion

Persistencia consistente.

## 7. Resultado obtenido

- PATCH /usuarios/me respondio HTTP 200.
- Se actualizaron correo_recuperacion, fecha_nacimiento, semestre, grupo y huella_habilitada.

## 8. Reaccion de la aplicacion observada

- La aplicacion permitio un PATCH parcial sin afectar otros campos del usuario.
- La persistencia fue correcta en la BD.

## 9. Evidencia

- Capturas:
- Logs: Pruebas/evidencias/backend/CP-BE-008/resultado.json
- Archivos de salida: Pruebas/evidencias/backend/resumen_pruebas_6_15.json
- Ruta de evidencias sugerida: Pruebas/evidencias/backend/CP-BE-008/

## 10. Estado final

- Estado: PASS
- Defecto relacionado:
- Accion siguiente: validar rango invalido de semestre en CP-BE-009.
