# Matriz Inicial de Casos de Prueba

## Backend (FastAPI)

1. CP-BE-001 - Health check de API
Por que: confirmar disponibilidad basica del servicio.
Esperado: GET / responde 200 con mensaje de estado.

2. CP-BE-002 - Registro usuario nuevo
Por que: validar alta correcta y hash de contrasena.
Esperado: POST /usuarios/ crea usuario y no guarda password plano.

3. CP-BE-003 - Registro duplicado
Por que: prevenir cuentas duplicadas por email.
Esperado: POST /usuarios/ devuelve 400 con mensaje de email ya registrado.

4. CP-BE-004 - Login exitoso
Por que: validar emision de JWT para app cliente.
Esperado: POST /login devuelve access_token y token_type.

5. CP-BE-005 - Login con password invalido
Por que: validar control de acceso.
Esperado: 401 con detalle de credenciales invalidas.

6. CP-BE-006 - Perfil autenticado con token valido
Por que: asegurar lectura de perfil del usuario logueado.
Esperado: GET /usuarios/me devuelve usuario correcto.

7. CP-BE-007 - Perfil autenticado con token invalido/expirado
Por que: evitar acceso con credenciales comprometidas.
Esperado: 401 en /usuarios/me.

8. CP-BE-008 - PATCH /usuarios/me parcial
Por que: confirmar actualizacion selectiva sin romper otros campos.
Esperado: solo cambian campos enviados.

9. CP-BE-009 - Validacion de semestre fuera de rango
Por que: proteger consistencia de datos.
Esperado: semestre <1 o >9 devuelve 400.

10. CP-BE-010 - Busqueda de usuarios con q corta
Por que: validar regla de negocio de longitud minima.
Esperado: /usuarios/buscar?q=a devuelve lista vacia.

11. CP-BE-011 - Busqueda de usuarios por nombre/email
Por que: habilitar exploracion de perfiles.
Esperado: coincidencias parciales, maximo 20 resultados.

12. CP-BE-012 - Publicaciones pendientes solo staff
Por que: validar autorizacion de moderacion.
Esperado: usuario normal recibe 403 en pendientes.

13. CP-BE-013 - Crear publicacion usuario normal
Por que: validar flujo de moderacion.
Esperado: estado inicial PENDIENTE y es_oficial=false.

14. CP-BE-014 - Crear publicacion staff
Por que: validar privilegios de staff.
Esperado: estado APROBADA y respeto de es_oficial.

15. CP-BE-015 - Perfil de usuario inexistente
Por que: validar manejo de recursos no encontrados.
Esperado: GET /usuarios/{id} inexistente devuelve 404.

## Android (Kotlin)

16. CP-AND-001 - Persistencia de sesion
Por que: asegurar continuidad de usuario autenticado.
Esperado: SessionManager guarda y recupera token/email correctamente.

17. CP-AND-002 - Logout
Por que: prevenir acceso posterior a cierre de sesion.
Esperado: al cerrar sesion se limpia almacenamiento local.

18. CP-AND-003 - Parseo de respuesta login
Por que: evitar crasheos por contratos API.
Esperado: ApiService/DTO parsean access_token y errores esperados.

19. CP-AND-004 - Validacion local de email institucional
Por que: reducir solicitudes invalidas al backend.
Esperado: solo permite formato @uat.edu.mx cuando aplique.

20. CP-AND-005 - Manejo de error de red
Por que: mejorar resiliencia UX.
Esperado: timeout o sin internet no crashea y muestra mensaje controlado.

## Web Legacy (HTML/JS)

21. CP-WEB-001 - Llamada API de login desde js/api.js
Por que: validar integracion minima del prototipo.
Esperado: request correcto, manejo de token y errores.

22. CP-WEB-002 - Render de pantallas principales
Por que: detectar regresiones visuales basicas.
Esperado: home/login/register cargan sin errores de consola.

23. CP-WEB-003 - Flujo de formulario de registro
Por que: prevenir envios con datos invalidos.
Esperado: validaciones basicas activas y mensajes claros.

## Prioridad sugerida

- Alta: CP-BE-001 a CP-BE-009, CP-AND-001, CP-AND-005
- Media: CP-BE-010 a CP-BE-015, CP-AND-002 a CP-AND-004, CP-WEB-001
- Baja: CP-WEB-002, CP-WEB-003
