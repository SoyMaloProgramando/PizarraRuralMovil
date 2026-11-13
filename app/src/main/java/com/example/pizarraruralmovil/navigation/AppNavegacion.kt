package com.example.pizarraruralmovil.navigation

import android.content.pm.ApplicationInfo
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pizarraruralmovil.R
import com.example.pizarraruralmovil.data.DatosDePrueba
import com.example.pizarraruralmovil.logic.GeneradorClave
import com.example.pizarraruralmovil.logic.ReglasTareas
import com.example.pizarraruralmovil.logic.alExito
import com.example.pizarraruralmovil.model.Acudiente
import com.example.pizarraruralmovil.model.Avatares
import com.example.pizarraruralmovil.model.Estudiante
import com.example.pizarraruralmovil.model.Rol
import com.example.pizarraruralmovil.ui.screens.colegio.AgregarDocenteScreen
import com.example.pizarraruralmovil.ui.screens.colegio.ClaveParaEntregarScreen
import com.example.pizarraruralmovil.ui.screens.colegio.DocentesScreen
import com.example.pizarraruralmovil.ui.screens.colegio.EstudiantesScreen
import com.example.pizarraruralmovil.ui.screens.colegio.FichaEstudianteScreen
import com.example.pizarraruralmovil.ui.screens.colegio.MatricularScreen
import com.example.pizarraruralmovil.ui.screens.docente.CrearCursoScreen
import com.example.pizarraruralmovil.ui.screens.docente.CursoScreen
import com.example.pizarraruralmovil.ui.screens.docente.MisCursosScreen
import com.example.pizarraruralmovil.ui.screens.docente.NuevaTareaScreen
import com.example.pizarraruralmovil.ui.screens.familia.MisHijosScreen
import com.example.pizarraruralmovil.ui.screens.familia.PizarraScreen
import com.example.pizarraruralmovil.ui.screens.inicio.AccesoDePrueba
import com.example.pizarraruralmovil.ui.screens.inicio.IngresoCuentaScreen
import com.example.pizarraruralmovil.ui.screens.inicio.IngresoFamiliaScreen
import com.example.pizarraruralmovil.ui.screens.inicio.SeleccionRolScreen
import com.example.pizarraruralmovil.ui.theme.TonoColegio
import com.example.pizarraruralmovil.ui.theme.TonoDocente
import com.example.pizarraruralmovil.viewmodel.PizarraViewModel
import kotlinx.coroutines.flow.first

/**
 * Navegación con Jetpack Compose (NavHost + NavController).
 * El grafo de navegación también es declarativo: se describe cada ruta con composable(...)
 * y la pantalla que muestra. Aquí se conecta cada pantalla con el ViewModel: las pantallas
 * reciben datos y avisan eventos, y esta función decide a dónde ir después de cada acción.
 *
 * Cada rol tiene sus propias rutas y cada ruta revisa que haya una sesión de ese rol:
 * si no la hay, vuelve a la selección de rol. Si hay sesión pero falta el dato (por ejemplo,
 * un estudiante que ya se retiró), solo vuelve a la pantalla anterior.
 *
 * Cada acción revisa primero `entrada.estaActiva`: mientras una pantalla entra o sale con su
 * animación, un segundo toque rápido no hace nada (evita volver dos veces o publicar dos veces).
 *
 * El ViewModel se crea con PizarraViewModel.Factory: así guarda los datos en el celular
 * y no se pierden al cerrar la app.
 */
@Composable
fun AppNavegacion(viewModel: PizarraViewModel = viewModel(factory = PizarraViewModel.Factory)) {
    val navController = rememberNavController()

    // Los datos de prueba solo se escriben en la primera pantalla en la versión de prueba (debug), nunca en una publicada
    val contexto = LocalContext.current
    val esVersionDePrueba = (contexto.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

    // Al volver a la app se actualiza la fecha: si pasó la medianoche, las pendientes de ayer quedan vencidas
    LifecycleResumeEffect(Unit) {
        viewModel.actualizarFecha()
        onPauseOrDispose { }
    }

    NavHost(navController = navController, startDestination = Rutas.SELECCION_ROL) {

        // ------------------------- Inicio -------------------------
        composable(Rutas.SELECCION_ROL) { entrada ->
            // Al llegar aquí (cerrar sesión o salir) se borra la sesión apenas termina la animación:
            // así la pantalla que se va no queda en blanco y no queda ninguna sesión abierta en memoria
            LaunchedEffect(entrada) {
                entrada.lifecycle.currentStateFlow.first { it == Lifecycle.State.RESUMED }
                viewModel.cerrarSesion()
            }
            SeleccionRolScreen(
                accesosDePrueba = if (!esVersionDePrueba) null
                else listOf(
                    AccesoDePrueba(Rol.COLEGIO, "${DatosDePrueba.CORREO_COLEGIO} · ${DatosDePrueba.CONTRASENA_COLEGIO}"),
                    AccesoDePrueba(Rol.DOCENTE, "${DatosDePrueba.CORREO_DOCENTE} · ${DatosDePrueba.CONTRASENA_DOCENTE}"),
                    AccesoDePrueba(Rol.ACUDIENTE, GeneradorClave.formatear(DatosDePrueba.CLAVE_FAMILIA_PEREZ))
                ),
                onContinuar = { rol ->
                    if (entrada.estaActiva) {
                        viewModel.cerrarSesion() // al elegir rol se cierra la sesión anterior, sea del rol que sea
                        val ingreso = when (rol) {
                            Rol.COLEGIO -> Rutas.INGRESO_COLEGIO
                            Rol.DOCENTE -> Rutas.INGRESO_DOCENTE
                            Rol.ACUDIENTE -> Rutas.INGRESO_FAMILIA
                        }
                        navController.navigate(ingreso)
                    }
                }
            )
        }

        composable(Rutas.INGRESO_COLEGIO) { entrada ->
            IngresoCuentaScreen(
                titulo = "Ingreso del colegio",
                descripcion = "Para la secretaría o la rectoría: matricule a los estudiantes y entregue las claves.",
                nota = "La cuenta del colegio la entrega quien administra la app.",
                icono = painterResource(R.drawable.ic_escuela),
                tono = TonoColegio,
                onIngresar = { correo, contrasena ->
                    if (!entrada.estaActiva) null
                    else viewModel.iniciarSesionColegio(correo, contrasena).alExito { navController.irAInicio(Rutas.ESTUDIANTES) }
                },
                onVolver = { if (entrada.estaActiva) navController.popBackStack() }
            )
        }

        composable(Rutas.INGRESO_DOCENTE) { entrada ->
            IngresoCuentaScreen(
                titulo = "Ingreso del docente",
                descripcion = "Inicie sesión para ver sus cursos y publicar tareas.",
                nota = "¿No tiene cuenta? La crea el colegio: pídala en la secretaría.",
                icono = rememberVectorPainter(Icons.Filled.Edit),
                tono = TonoDocente,
                onIngresar = { correo, contrasena ->
                    if (!entrada.estaActiva) null
                    else viewModel.iniciarSesionDocente(correo, contrasena).alExito { navController.irAInicio(Rutas.MIS_CURSOS) }
                },
                onVolver = { if (entrada.estaActiva) navController.popBackStack() }
            )
        }

        composable(Rutas.INGRESO_FAMILIA) { entrada ->
            IngresoFamiliaScreen(
                onEntrar = { clave ->
                    if (!entrada.estaActiva) null
                    else viewModel.ingresarConClave(clave).alExito { navController.irAInicio(Rutas.PIZARRA) }
                },
                onVolver = { if (entrada.estaActiva) navController.popBackStack() }
            )
        }

        // ------------------------- Colegio -------------------------
        composable(Rutas.ESTUDIANTES) { entrada ->
            val colegio = viewModel.colegioActual
            if (colegio == null) {
                VolverAlInicio(navController, entrada)
                return@composable
            }
            EstudiantesScreen(
                nombreColegio = colegio.nombre,
                cursos = viewModel.cursosDelColegio(),
                estudiantesDe = { cursoId -> viewModel.estudiantesDelCurso(cursoId) },
                nombreDocente = { docenteId -> viewModel.nombreDocente(docenteId) },
                nombreAcudiente = { acudienteId -> viewModel.buscarAcudiente(acudienteId)?.nombre.orEmpty() },
                onAbrirEstudiante = { estudianteId ->
                    if (entrada.estaActiva) navController.navigate(Rutas.fichaEstudiante(estudianteId))
                },
                onMatricular = { if (entrada.estaActiva) navController.navigate(Rutas.matricular()) },
                onIrADocentes = { if (entrada.estaActiva) navController.navigate(Rutas.DOCENTES) { launchSingleTop = true } },
                // La sesión se borra al llegar a la selección de rol (así esta pantalla no queda en blanco mientras se va)
                onCerrarSesion = { if (entrada.estaActiva) navController.irASeleccionRol() }
            )
        }

        composable(
            route = Rutas.MATRICULAR,
            arguments = listOf(navArgument(Rutas.ARG_ACUDIENTE_ID) {
                type = NavType.IntType
                defaultValue = Rutas.SIN_ACUDIENTE
            })
        ) { entrada ->
            val acudienteId = entrada.arguments?.getInt(Rutas.ARG_ACUDIENTE_ID)?.takeIf { it != Rutas.SIN_ACUDIENTE }
            val acudienteFijo = acudienteId?.let { viewModel.buscarAcudiente(it) }
            if (viewModel.colegioActual == null || (acudienteId != null && acudienteFijo == null)) {
                VolverAlInicio(navController, entrada, haySesion = viewModel.colegioActual != null)
                return@composable
            }
            MatricularScreen(
                cursos = viewModel.cursosDelColegio(),
                nombreDocente = { docenteId -> viewModel.nombreDocente(docenteId) },
                avatares = Avatares.lista,
                acudienteFijo = acudienteFijo?.nombre,
                onMatricular = { nombre, cursoId, avatar, nombreAcudiente, verificado ->
                    if (!entrada.estaActiva) null
                    else viewModel.matricular(nombre, cursoId, avatar, acudienteId, nombreAcudiente, verificado)
                        .alExito { estudiante ->
                            if (acudienteFijo == null) {
                                // Familia nueva: se muestra su clave una sola vez y luego se vuelve a la lista
                                navController.navigate(Rutas.CLAVE_PARA_ENTREGAR) { popUpTo(Rutas.ESTUDIANTES) }
                            } else {
                                // Hermano o hermana: ya tiene clave; su ficha reemplaza a la del hermano
                                navController.navigate(Rutas.fichaEstudiante(estudiante.id)) {
                                    popUpTo(Rutas.FICHA_ESTUDIANTE) { inclusive = true }
                                }
                            }
                        }
                },
                onVolver = { if (entrada.estaActiva) navController.popBackStack() }
            )
        }

        composable(
            route = Rutas.FICHA_ESTUDIANTE,
            arguments = listOf(navArgument(Rutas.ARG_ESTUDIANTE_ID) { type = NavType.IntType })
        ) { entrada ->
            val estudianteId = entrada.arguments?.getInt(Rutas.ARG_ESTUDIANTE_ID) ?: return@composable
            val estudianteActual = viewModel.buscarEstudiante(estudianteId)
            val acudienteActual = estudianteActual?.let { viewModel.buscarAcudiente(it.acudienteId) }

            // Lo último que mostró la ficha: si el estudiante se retira, sigue visible mientras la pantalla se va
            var ultimaFicha by remember { mutableStateOf<Pair<Estudiante, Acudiente>?>(null) }
            if (estudianteActual != null && acudienteActual != null) {
                SideEffect { ultimaFicha = estudianteActual to acudienteActual }
            }
            val datos = if (estudianteActual != null && acudienteActual != null) estudianteActual to acudienteActual else ultimaFicha
            if (datos == null || viewModel.colegioActual == null) {
                VolverAlInicio(navController, entrada, haySesion = viewModel.colegioActual != null)
                return@composable
            }
            val (estudiante, acudiente) = datos
            FichaEstudianteScreen(
                estudiante = estudiante,
                tituloCurso = viewModel.buscarCurso(estudiante.cursoId)?.titulo.orEmpty(),
                acudiente = acudiente,
                hermanos = viewModel.hermanosDe(estudiante),
                tituloCursoDe = { cursoId -> viewModel.buscarCurso(cursoId)?.titulo.orEmpty() },
                // La ficha del hermano reemplaza a esta: así nunca quedan fichas viejas apiladas
                onAbrirEstudiante = { id ->
                    if (entrada.estaActiva) {
                        navController.navigate(Rutas.fichaEstudiante(id)) { popUpTo(Rutas.FICHA_ESTUDIANTE) { inclusive = true } }
                    }
                },
                onGenerarClave = {
                    if (entrada.estaActiva) {
                        viewModel.generarNuevaClave(acudiente.id).alExito { navController.navigate(Rutas.CLAVE_PARA_ENTREGAR) }
                    }
                },
                onMatricularHermano = { if (entrada.estaActiva) navController.navigate(Rutas.matricular(acudiente.id)) },
                onRetirar = {
                    if (entrada.estaActiva) {
                        navController.popBackStack(Rutas.ESTUDIANTES, inclusive = false)
                        viewModel.retirarEstudiante(estudiante.id)
                    }
                },
                onVolver = { if (entrada.estaActiva) navController.popBackStack() }
            )
        }

        composable(Rutas.CLAVE_PARA_ENTREGAR) { entrada ->
            // La pantalla recuerda la clave que le tocó mostrar: así sigue visible mientras se va,
            // aunque el ViewModel ya la haya olvidado al tocar "Listo"
            val clave = remember { viewModel.claveParaEntregar }
            if (clave == null || viewModel.colegioActual == null) {
                VolverAlInicio(navController, entrada, haySesion = viewModel.colegioActual != null)
                return@composable
            }
            ClaveParaEntregarScreen(
                clave = clave,
                onListo = {
                    if (entrada.estaActiva) {
                        viewModel.descartarClave()
                        navController.popBackStack()
                    }
                }
            )
        }

        composable(Rutas.DOCENTES) { entrada ->
            if (viewModel.colegioActual == null) {
                VolverAlInicio(navController, entrada)
                return@composable
            }
            DocentesScreen(
                docentes = viewModel.docentesDelColegio(),
                cantidadCursos = { docenteId -> viewModel.cantidadCursos(docenteId) },
                onAgregarDocente = { if (entrada.estaActiva) navController.navigate(Rutas.AGREGAR_DOCENTE) },
                onNuevaContrasena = { docenteId ->
                    if (entrada.estaActiva) {
                        viewModel.generarNuevaContrasena(docenteId).alExito { navController.navigate(Rutas.CLAVE_PARA_ENTREGAR) }
                    }
                },
                onIrAEstudiantes = { if (entrada.estaActiva) navController.popBackStack(Rutas.ESTUDIANTES, inclusive = false) },
                onCerrarSesion = { if (entrada.estaActiva) navController.irASeleccionRol() }
            )
        }

        composable(Rutas.AGREGAR_DOCENTE) { entrada ->
            if (viewModel.colegioActual == null) {
                VolverAlInicio(navController, entrada)
                return@composable
            }
            AgregarDocenteScreen(
                onCrear = { nombre, correo ->
                    if (!entrada.estaActiva) null
                    else viewModel.crearCuentaDocente(nombre, correo).alExito {
                        // Muestra la contraseña; al terminar vuelve a la lista de docentes
                        navController.navigate(Rutas.CLAVE_PARA_ENTREGAR) { popUpTo(Rutas.DOCENTES) }
                    }
                },
                onVolver = { if (entrada.estaActiva) navController.popBackStack() }
            )
        }

        // ------------------------- Docente -------------------------
        composable(Rutas.MIS_CURSOS) { entrada ->
            val docente = viewModel.docenteActual
            if (docente == null) {
                VolverAlInicio(navController, entrada)
                return@composable
            }
            val cursos = viewModel.cursosDelDocente()
            MisCursosScreen(
                nombreDocente = docente.nombre,
                cursos = cursos,
                tareasPorCurso = cursos.associate { it.id to viewModel.tareasDelCurso(it.id).size },
                estudiantesPorCurso = cursos.associate { it.id to viewModel.estudiantesDelCurso(it.id).size },
                onAbrirCurso = { cursoId -> if (entrada.estaActiva) navController.navigate(Rutas.curso(cursoId)) },
                onCrearCurso = { if (entrada.estaActiva) navController.navigate(Rutas.CREAR_CURSO) },
                // La sesión se borra al llegar a la selección de rol (así esta pantalla no queda en blanco mientras se va)
                onCerrarSesion = { if (entrada.estaActiva) navController.irASeleccionRol() }
            )
        }

        composable(Rutas.CREAR_CURSO) { entrada ->
            if (viewModel.docenteActual == null) {
                VolverAlInicio(navController, entrada)
                return@composable
            }
            CrearCursoScreen(
                onGuardar = { grado, nombre ->
                    if (!entrada.estaActiva) null
                    else viewModel.crearCurso(grado, nombre).alExito { curso ->
                        // Muestra el curso recién creado; al volver, regresa a "Mis cursos"
                        navController.navigate(Rutas.curso(curso.id)) { popUpTo(Rutas.MIS_CURSOS) }
                    }
                },
                onVolver = { if (entrada.estaActiva) navController.popBackStack() }
            )
        }

        composable(
            route = Rutas.CURSO,
            arguments = listOf(navArgument(Rutas.ARG_CURSO_ID) { type = NavType.IntType })
        ) { entrada ->
            val cursoId = entrada.arguments?.getInt(Rutas.ARG_CURSO_ID) ?: return@composable
            val curso = viewModel.buscarCurso(cursoId)
            if (curso == null || curso.docenteId != viewModel.docenteActual?.id) {
                VolverAlInicio(navController, entrada, haySesion = viewModel.docenteActual != null)
                return@composable
            }
            CursoScreen(
                curso = curso,
                tareas = viewModel.tareasDelCurso(cursoId),
                estudiantes = viewModel.estudiantesDelCurso(cursoId),
                validaciones = viewModel.validacionesDelCurso(cursoId),
                hoy = viewModel.hoy,
                onNuevaTarea = { if (entrada.estaActiva) navController.navigate(Rutas.nuevaTarea(cursoId)) },
                onEditarTarea = { tareaId -> if (entrada.estaActiva) navController.navigate(Rutas.editarTarea(tareaId)) },
                // La pantalla ya pidió confirmación: aquí solo se avisa al ViewModel
                onEliminarTarea = { tareaId -> viewModel.eliminarTarea(tareaId) },
                onVolver = { if (entrada.estaActiva) navController.popBackStack() }
            )
        }

        composable(
            route = Rutas.EDITAR_TAREA,
            arguments = listOf(navArgument(Rutas.ARG_TAREA_ID) { type = NavType.IntType })
        ) { entrada ->
            val tareaId = entrada.arguments?.getInt(Rutas.ARG_TAREA_ID) ?: return@composable
            // Solo una tarea de un curso propio (RN-03); si ya no existe, vuelve al curso
            val tarea = viewModel.buscarTareaDelDocente(tareaId)
            val curso = tarea?.let { viewModel.buscarCurso(it.cursoId) }
            if (tarea == null || curso == null) {
                VolverAlInicio(navController, entrada, haySesion = viewModel.docenteActual != null)
                return@composable
            }
            // La misma pantalla de "Nueva tarea", pero llena con lo que ya tenía la tarea
            NuevaTareaScreen(
                tituloCurso = curso.titulo,
                hoy = viewModel.hoy,
                titulo = "Editar tarea",
                textoBoton = "Guardar cambios",
                textoInicial = tarea.texto,
                fechaInicial = tarea.fechaLimite,
                nota = "Las entregas que ya validaron las familias se conservan.",
                onPublicar = { texto, fechaLimite ->
                    if (!entrada.estaActiva) null
                    else viewModel.editarTarea(tareaId, texto, fechaLimite).alExito { navController.popBackStack() }
                },
                onVolver = { if (entrada.estaActiva) navController.popBackStack() }
            )
        }

        composable(
            route = Rutas.NUEVA_TAREA,
            arguments = listOf(navArgument(Rutas.ARG_CURSO_ID) { type = NavType.IntType })
        ) { entrada ->
            val cursoId = entrada.arguments?.getInt(Rutas.ARG_CURSO_ID) ?: return@composable
            val curso = viewModel.buscarCurso(cursoId)
            if (curso == null || curso.docenteId != viewModel.docenteActual?.id) {
                VolverAlInicio(navController, entrada, haySesion = viewModel.docenteActual != null)
                return@composable
            }
            NuevaTareaScreen(
                tituloCurso = curso.titulo,
                hoy = viewModel.hoy,
                onPublicar = { texto, fechaLimite ->
                    if (!entrada.estaActiva) null
                    else viewModel.publicarTarea(cursoId, texto, fechaLimite).alExito { navController.popBackStack() }
                },
                onVolver = { if (entrada.estaActiva) navController.popBackStack() }
            )
        }

        // ------------------------- Padre, madre o acudiente -------------------------
        composable(Rutas.PIZARRA) { entrada ->
            val hijo = viewModel.hijoSeleccionado
            if (hijo == null) {
                VolverAlInicio(navController, entrada)
                return@composable
            }
            val tareas = viewModel.pizarraDe(hijo)
            PizarraScreen(
                hijo = hijo,
                tituloCurso = viewModel.buscarCurso(hijo.cursoId)?.titulo.orEmpty(),
                tareas = tareas,
                resumen = ReglasTareas.resumen(tareas),
                hoy = viewModel.hoy,
                onCambiarValidacion = { tareaId, validada -> viewModel.cambiarValidacion(hijo.id, tareaId, validada) },
                onIrAMisHijos = {
                    if (entrada.estaActiva) navController.navigate(Rutas.MIS_HIJOS) { launchSingleTop = true }
                },
                // Salir desde la misma pizarra: la sesión se borra al llegar a la selección de rol
                onSalir = { if (entrada.estaActiva) navController.irASeleccionRol() }
            )
        }

        composable(Rutas.MIS_HIJOS) { entrada ->
            val acudiente = viewModel.acudienteActual
            val hijos = viewModel.hijos
            if (acudiente == null || hijos.isEmpty()) {
                VolverAlInicio(navController, entrada)
                return@composable
            }
            MisHijosScreen(
                nombreAcudiente = acudiente.nombre,
                hijos = hijos,
                hijoSeleccionadoId = viewModel.hijoSeleccionado?.id,
                tituloCurso = { cursoId -> viewModel.buscarCurso(cursoId)?.titulo.orEmpty() },
                onSeleccionarHijo = { hijoId ->
                    if (entrada.estaActiva) {
                        viewModel.seleccionarHijo(hijoId)
                        navController.popBackStack(Rutas.PIZARRA, inclusive = false)
                    }
                },
                onIrAPizarra = { if (entrada.estaActiva) navController.popBackStack(Rutas.PIZARRA, inclusive = false) },
                onSalir = { if (entrada.estaActiva) navController.irASeleccionRol() }
            )
        }
    }
}

// ------------------------- Ayudas de navegación -------------------------

/**
 * true cuando la pantalla ya terminó de aparecer y es la que el usuario ve.
 * Durante la animación de entrada o salida es false, y así se ignoran los toques repetidos.
 */
private val NavBackStackEntry.estaActiva: Boolean
    get() = lifecycle.currentState == Lifecycle.State.RESUMED

/**
 * Si no hay sesión (por ejemplo, Android cerró la app en segundo plano y se borró la memoria),
 * vuelve a la selección de rol en lugar de dejar una pantalla en blanco. Si hay sesión pero falta
 * el dato que la pantalla necesita ([haySesion]), solo vuelve a la pantalla anterior.
 * Solo actúa si esta pantalla es la actual, para no navegar dos veces.
 */
@Composable
private fun VolverAlInicio(navController: NavController, entrada: NavBackStackEntry, haySesion: Boolean = false) {
    LaunchedEffect(entrada.id) {
        if (navController.currentBackStackEntry?.id != entrada.id) return@LaunchedEffect
        if (haySesion && navController.previousBackStackEntry != null) navController.popBackStack()
        else navController.irASeleccionRol()
    }
}

/** Entra al inicio de un rol y borra el historial: con "atrás" se sale de la app en vez de volver al ingreso. */
private fun NavController.irAInicio(ruta: String) =
    navigate(ruta) { popUpTo(graph.id) { inclusive = true } }

/** Vuelve a la selección de rol (cerrar sesión o cambiar de rol). */
private fun NavController.irASeleccionRol() =
    navigate(Rutas.SELECCION_ROL) { popUpTo(graph.id) { inclusive = true } }
