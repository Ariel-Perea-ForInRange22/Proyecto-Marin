# Ejecucion y Documentacion de Pruebas - Proyecto Marin

Fecha de creacion: 2026-03-19
Ubicacion: carpeta Pruebas
Objetivo: concentrar en un solo archivo todas las pruebas propuestas y su formato de documentacion.

## Instrucciones de uso

1. Ejecuta cada caso en el orden sugerido.
2. Completa en cada caso las secciones:
- Resultado obtenido
- Reaccion de la aplicacion
- Evidencia
- Estado final
3. Si un caso falla, registra ID de defecto y accion siguiente.

## Convenciones

- Estado: PASS | FAIL | BLOCKED
- Prioridad: Alta | Media | Baja
- Tipo: Unitario | Integracion | API | UI

---

## CP-BE-001 - Health check de API

- Modulo: Backend
- Prioridad: Alta
- Tipo: API
- Por que: confirmar disponibilidad basica del servicio.
- Precondiciones: servidor FastAPI arriba en puerto 8000.
- Pasos:
1. Hacer GET a /.
- Resultado esperado: HTTP 200 con mensaje de estado.
- Reaccion esperada de la aplicacion: respuesta inmediata sin error.
- Resultado obtenido:
- Reaccion de la aplicacion:
- Evidencia:
- Estado final:

## CP-BE-002 - Registro usuario nuevo

- Modulo: Backend
- Prioridad: Alta
- Tipo: API
- Por que: validar alta correcta y hash de contrasena.
- Precondiciones: email de prueba no registrado.
- Pasos:
1. Hacer POST /usuarios/ con nombre, email y password validos.
2. Consultar usuario creado desde endpoint o DB.
- Resultado esperado: usuario creado y contrasena almacenada como hash.
- Reaccion esperada de la aplicacion: alta exitosa sin exponer password plano.
- Resultado obtenido:
- Reaccion de la aplicacion:
- Evidencia:
- Estado final:

## CP-BE-003 - Registro duplicado

- Modulo: Backend
- Prioridad: Alta
- Tipo: API
- Por que: prevenir cuentas duplicadas por email.
- Precondiciones: email ya existente.
- Pasos:
1. Hacer POST /usuarios/ con email ya registrado.
- Resultado esperado: HTTP 400 con detalle de email duplicado.
- Reaccion esperada de la aplicacion: rechazo controlado y mensaje claro.
- Resultado obtenido:
- Reaccion de la aplicacion:
- Evidencia:
- Estado final:

## CP-BE-004 - Login exitoso

- Modulo: Backend
- Prioridad: Alta
- Tipo: API
- Por que: validar emision de JWT para clientes.
- Precondiciones: usuario valido existente.
- Pasos:
1. Hacer POST /login con username/password validos.
- Resultado esperado: HTTP 200 con access_token y token_type=bearer.
- Reaccion esperada de la aplicacion: autenticacion correcta y token usable.
- Resultado obtenido:
- Reaccion de la aplicacion:
- Evidencia:
- Estado final:

## CP-BE-005 - Login con password invalido

- Modulo: Backend
- Prioridad: Alta
- Tipo: API
- Por que: validar control de acceso.
- Precondiciones: usuario existente.
- Pasos:
1. Hacer POST /login con password incorrecto.
- Resultado esperado: HTTP 401.
- Reaccion esperada de la aplicacion: rechazo seguro sin filtrar datos sensibles.
- Resultado obtenido:
- Reaccion de la aplicacion:
- Evidencia:
- Estado final:

## CP-BE-006 - Perfil autenticado con token valido

- Modulo: Backend
- Prioridad: Alta
- Tipo: API
- Por que: asegurar lectura del perfil autenticado.
- Precondiciones: token JWT valido.
- Pasos:
1. Hacer GET /usuarios/me con header Authorization Bearer.
- Resultado esperado: HTTP 200 con datos del usuario autenticado.
- Reaccion esperada de la aplicacion: acceso autorizado al perfil propio.
- Resultado obtenido:
- Reaccion de la aplicacion:
- Evidencia:
- Estado final:

## CP-BE-007 - Perfil con token invalido o expirado

- Modulo: Backend
- Prioridad: Alta
- Tipo: API
- Por que: bloquear acceso con credenciales invalidas.
- Precondiciones: token mal formado o expirado.
- Pasos:
1. Hacer GET /usuarios/me con token invalido.
- Resultado esperado: HTTP 401.
- Reaccion esperada de la aplicacion: denegacion controlada.
- Resultado obtenido:
- Reaccion de la aplicacion:
- Evidencia:
- Estado final:

## CP-BE-008 - PATCH /usuarios/me parcial

- Modulo: Backend
- Prioridad: Alta
- Tipo: API
- Por que: validar actualizacion parcial sin afectar campos no enviados.
- Precondiciones: usuario autenticado.
- Pasos:
1. Hacer PATCH /usuarios/me enviando solo 1 o 2 campos.
2. Consultar perfil despues de guardar.
- Resultado esperado: solo cambian campos enviados; el resto permanece igual.
- Reaccion esperada de la aplicacion: persistencia consistente.
- Resultado obtenido:
- Reaccion de la aplicacion:
- Evidencia:
- Estado final:

## CP-BE-009 - Validacion de semestre fuera de rango

- Modulo: Backend
- Prioridad: Alta
- Tipo: API
- Por que: proteger consistencia de datos.
- Precondiciones: usuario autenticado.
- Pasos:
1. Hacer PATCH /usuarios/me con semestre=0 y luego semestre=15.
- Resultado esperado: HTTP 400 en ambos casos.
- Reaccion esperada de la aplicacion: validacion de negocio activa.
- Resultado obtenido:
- Reaccion de la aplicacion:
- Evidencia:
- Estado final:

## CP-BE-010 - Busqueda con q corta

- Modulo: Backend
- Prioridad: Media
- Tipo: API
- Por que: validar regla de longitud minima del buscador.
- Precondiciones: endpoint de busqueda habilitado.
- Pasos:
1. Hacer GET /usuarios/buscar?q=a.
- Resultado esperado: lista vacia.
- Reaccion esperada de la aplicacion: no procesa consultas demasiado cortas.
- Resultado obtenido:
- Reaccion de la aplicacion:
- Evidencia:
- Estado final:

## CP-BE-011 - Busqueda por nombre o email

- Modulo: Backend
- Prioridad: Media
- Tipo: API
- Por que: asegurar busqueda util para exploracion de perfiles.
- Precondiciones: usuarios con nombres/correos variados.
- Pasos:
1. Hacer GET /usuarios/buscar?q=termino.
- Resultado esperado: coincidencias parciales, maximo 20 resultados.
- Reaccion esperada de la aplicacion: resultados relevantes y acotados.
- Resultado obtenido:
- Reaccion de la aplicacion:
- Evidencia:
- Estado final:

## CP-BE-012 - Pendientes solo staff

- Modulo: Backend
- Prioridad: Media
- Tipo: API
- Por que: validar autorizacion de moderacion.
- Precondiciones: un usuario normal y uno staff.
- Pasos:
1. Intentar GET /comunidades/{id}/pendientes con usuario normal.
2. Repetir con usuario staff.
- Resultado esperado: normal=403, staff=200.
- Reaccion esperada de la aplicacion: control de permisos correcto.
- Resultado obtenido:
- Reaccion de la aplicacion:
- Evidencia:
- Estado final:

## CP-BE-013 - Crear publicacion usuario normal

- Modulo: Backend
- Prioridad: Media
- Tipo: API
- Por que: validar flujo de moderacion.
- Precondiciones: comunidad existente y usuario normal autenticado.
- Pasos:
1. Hacer POST /comunidades/{id}/publicaciones.
- Resultado esperado: estado inicial PENDIENTE y es_oficial=false.
- Reaccion esperada de la aplicacion: publicacion entra a cola de revision.
- Resultado obtenido:
- Reaccion de la aplicacion:
- Evidencia:
- Estado final:

## CP-BE-014 - Crear publicacion staff

- Modulo: Backend
- Prioridad: Media
- Tipo: API
- Por que: validar privilegios de staff.
- Precondiciones: comunidad existente y usuario staff autenticado.
- Pasos:
1. Hacer POST /comunidades/{id}/publicaciones con es_oficial=true.
- Resultado esperado: estado APROBADA y es_oficial segun payload.
- Reaccion esperada de la aplicacion: autopublicacion para staff.
- Resultado obtenido:
- Reaccion de la aplicacion:
- Evidencia:
- Estado final:

## CP-BE-015 - Perfil de usuario inexistente

- Modulo: Backend
- Prioridad: Media
- Tipo: API
- Por que: validar manejo de recursos no encontrados.
- Precondiciones: id inexistente.
- Pasos:
1. Hacer GET /usuarios/{id_inexistente}.
- Resultado esperado: HTTP 404.
- Reaccion esperada de la aplicacion: error controlado y sin caida.
- Resultado obtenido:
- Reaccion de la aplicacion:
- Evidencia:
- Estado final:

## CP-AND-001 - Persistencia de sesion

- Modulo: Android
- Prioridad: Alta
- Tipo: Unitario
- Por que: asegurar continuidad de usuario autenticado.
- Precondiciones: SessionManager configurado.
- Pasos:
1. Guardar token/email en SessionManager.
2. Leer token/email guardados.
- Resultado esperado: valores recuperados igual a los guardados.
- Reaccion esperada de la aplicacion: sesion persiste entre aperturas.
- Resultado obtenido:
- Reaccion de la aplicacion:
- Evidencia:
- Estado final:

## CP-AND-002 - Logout

- Modulo: Android
- Prioridad: Media
- Tipo: Unitario
- Por que: evitar acceso posterior tras cierre de sesion.
- Precondiciones: sesion activa en almacenamiento local.
- Pasos:
1. Ejecutar logout.
2. Verificar que token/email ya no existan.
- Resultado esperado: almacenamiento limpio y flujo vuelve a login.
- Reaccion esperada de la aplicacion: cierre de sesion completo.
- Resultado obtenido:
- Reaccion de la aplicacion:
- Evidencia:
- Estado final:

## CP-AND-003 - Parseo de respuesta login

- Modulo: Android
- Prioridad: Media
- Tipo: Unitario
- Por que: prevenir crasheos por cambios de contrato API.
- Precondiciones: DTOs y ApiService activos.
- Pasos:
1. Simular respuesta de login exitosa.
2. Simular respuesta con error de autenticacion.
- Resultado esperado: parseo correcto en ambos escenarios.
- Reaccion esperada de la aplicacion: maneja datos y errores sin crash.
- Resultado obtenido:
- Reaccion de la aplicacion:
- Evidencia:
- Estado final:

## CP-AND-004 - Validacion local de email institucional

- Modulo: Android
- Prioridad: Media
- Tipo: Unitario
- Por que: reducir solicitudes invalidas al backend.
- Precondiciones: formulario de registro/login disponible.
- Pasos:
1. Probar correos validos @uat.edu.mx.
2. Probar correos invalidos de otros dominios.
- Resultado esperado: solo acepta correos permitidos segun regla vigente.
- Reaccion esperada de la aplicacion: feedback inmediato al usuario.
- Resultado obtenido:
- Reaccion de la aplicacion:
- Evidencia:
- Estado final:

## CP-AND-005 - Manejo de error de red

- Modulo: Android
- Prioridad: Alta
- Tipo: Integracion
- Por que: mejorar resiliencia de UX.
- Precondiciones: app en dispositivo/emulador; backend apagado o red inestable.
- Pasos:
1. Ejecutar accion que consuma API sin conectividad.
- Resultado esperado: no hay crash, se muestra mensaje controlado.
- Reaccion esperada de la aplicacion: manejo de excepcion y recuperacion.
- Resultado obtenido:
- Reaccion de la aplicacion:
- Evidencia:
- Estado final:

## CP-WEB-001 - Login desde js/api.js

- Modulo: Web Legacy
- Prioridad: Media
- Tipo: Integracion
- Por que: validar integracion minima del prototipo web con API.
- Precondiciones: backend disponible, pagina login funcional.
- Pasos:
1. Intentar login correcto e incorrecto desde la web.
- Resultado esperado: request correcto, token en exito y manejo de error en fallo.
- Reaccion esperada de la aplicacion: flujo coherente con backend.
- Resultado obtenido:
- Reaccion de la aplicacion:
- Evidencia:
- Estado final:

## CP-WEB-002 - Render de pantallas principales

- Modulo: Web Legacy
- Prioridad: Baja
- Tipo: UI
- Por que: detectar regresiones visuales basicas.
- Precondiciones: abrir archivos HTML principales.
- Pasos:
1. Cargar login, register y home.
2. Revisar consola del navegador.
- Resultado esperado: carga sin errores JS bloqueantes.
- Reaccion esperada de la aplicacion: interfaz usable sin fallos graves.
- Resultado obtenido:
- Reaccion de la aplicacion:
- Evidencia:
- Estado final:

## CP-WEB-003 - Flujo de formulario de registro

- Modulo: Web Legacy
- Prioridad: Baja
- Tipo: UI
- Por que: prevenir envio de datos invalidos.
- Precondiciones: pagina register disponible.
- Pasos:
1. Enviar formulario vacio.
2. Enviar con datos invalidos.
3. Enviar con datos validos.
- Resultado esperado: validaciones activas y mensajes claros.
- Reaccion esperada de la aplicacion: guia al usuario antes de enviar al backend.
- Resultado obtenido:
- Reaccion de la aplicacion:
- Evidencia:
- Estado final:

---

## Orden sugerido de ejecucion

1. Alta prioridad: CP-BE-001 a CP-BE-009, CP-AND-001, CP-AND-005.
2. Media prioridad: CP-BE-010 a CP-BE-015, CP-AND-002 a CP-AND-004, CP-WEB-001.
3. Baja prioridad: CP-WEB-002, CP-WEB-003.

## Criterio de cierre sugerido

- 100% de pruebas de prioridad Alta en PASS.
- Al menos 80% de prioridad Media en PASS.
- Ningun FAIL critico abierto en autenticacion, autorizacion o persistencia de datos.
