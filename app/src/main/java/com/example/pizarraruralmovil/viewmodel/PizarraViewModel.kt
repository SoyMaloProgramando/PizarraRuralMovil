package com.example.pizarraruralmovil.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.pizarraruralmovil.data.Almacen
import com.example.pizarraruralmovil.data.AlmacenEnArchivo
import com.example.pizarraruralmovil.data.DatosDePrueba
import com.example.pizarraruralmovil.data.DatosGuardados
import com.example.pizarraruralmovil.logic.GeneradorClave
import com.example.pizarraruralmovil.logic.Huella
import com.example.pizarraruralmovil.logic.LimiteIntentos
import com.example.pizarraruralmovil.logic.ReglasTareas
import com.example.pizarraruralmovil.logic.Resultado
import com.example.pizarraruralmovil.logic.Validaciones
import com.example.pizarraruralmovil.model.Acudiente
import com.example.pizarraruralmovil.model.ClaveParaEntregar
import com.example.pizarraruralmovil.model.CuentaColegio
import com.example.pizarraruralmovil.model.Curso
import com.example.pizarraruralmovil.model.Docente
import com.example.pizarraruralmovil.model.Estudiante
import com.example.pizarraruralmovil.model.Tarea
import com.example.pizarraruralmovil.model.TareaConEstado
import com.example.pizarraruralmovil.model.TipoClave
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Estado y lógica de negocio de la app.
 *
 * Elevación de estados: el estado vive aquí arriba y baja a las pantallas como valores;
 * las pantallas avisan lo que pasó llamando a estas funciones (eventos) y nunca cambian
 * los datos por su cuenta. Así todas las pantallas ven la misma información.
 *
 * Como la interfaz es declarativa, nadie "repinta" nada a mano: al cambiar un mutableStateOf
 * o una mutableStateListOf, Compose vuelve a ejecutar solo las funciones que leen ese dato
 * (recomposición).
 *
 * Reparto de responsabilidades después de la revisión del profesor:
 * - El colegio verifica a los estudiantes en persona, los matricula y crea todas las cuentas (RN-19, RN-23).
 * - El acudiente no se registra: entra con la clave familiar que le entrega el colegio (RN-20)
 *   y es quien valida que cada tarea se entregó (RN-22). Nunca ve a otros estudiantes (RN-21).
 * - El docente publica, corrige y elimina las tareas de sus cursos, y ve cuántas entregas validaron las familias.
 *
 * Los datos se guardan en el celular con [almacen] después de cada cambio: así lo que crea el colegio
 * (estudiantes, docentes) o el docente (cursos, tareas) sigue ahí al cerrar y abrir la app.
 * Las sesiones no se guardan. En el siguiente sprint el almacén será la API.
 */
class PizarraViewModel(
    private val almacen: Almacen = Almacen.SoloEnMemoria,
    colaDeGuardado: CoroutineDispatcher = Dispatchers.IO.limitedParallelism(1)
) : ViewModel() {

    // ---------------- Estado observable ----------------

    /** Fecha de hoy en la zona horaria de Colombia (RN-18). Se actualiza al volver a la app (ReqF-15). */
    var hoy: LocalDate by mutableStateOf(LocalDate.now(ZONA_COLOMBIA))
        private set

    // Lo que quedó guardado en el celular; la primera vez no hay nada y se usan los datos de prueba
    private val guardados: DatosGuardados? = almacen.cargar()

    private val colegios = DatosDePrueba.colegios
    private val docentes = mutableStateListOf<Docente>().apply { addAll(guardados?.docentes ?: DatosDePrueba.docentes) }
    private val cursos = mutableStateListOf<Curso>().apply { addAll(guardados?.cursos ?: DatosDePrueba.cursos) }
    private val tareas = mutableStateListOf<Tarea>().apply { addAll(guardados?.tareas ?: DatosDePrueba.tareas(hoy)) }
    private val acudientes = mutableStateListOf<Acudiente>().apply { addAll(guardados?.acudientes ?: DatosDePrueba.acudientes) }
    private val estudiantes = mutableStateListOf<Estudiante>().apply { addAll(guardados?.estudiantes ?: DatosDePrueba.estudiantes) }

    // Los id no se reutilizan aunque se retire a alguien o se elimine una tarea:
    // una pantalla vieja nunca muestra a otra persona y una tarea nueva nunca hereda validaciones viejas
    private var proximoIdEstudiante = guardados?.proximoIdEstudiante ?: siguienteId(estudiantes.map { it.id })
    private var proximoIdAcudiente = guardados?.proximoIdAcudiente ?: siguienteId(acudientes.map { it.id })
    private var proximoIdTarea = guardados?.proximoIdTarea ?: siguienteId(tareas.map { it.id })

    // Las escrituras van en segundo plano y una detrás de otra (cola de un solo hilo), para no trabar la pantalla.
    // No se cancela al cerrar la pantalla: así termina de escribir el último cambio.
    private val alcanceGuardado = CoroutineScope(SupervisorJob() + colaDeGuardado)

    // ---------------- Sesión (solo una a la vez) ----------------

    var colegioActual by mutableStateOf<CuentaColegio?>(null)
        private set

    /** La sesión del docente recuerda la huella de su contraseña: si el colegio genera otra, la sesión se acaba (RN-23). */
    private var sesionDocente by mutableStateOf<Pair<Int, String>?>(null)

    val docenteActual: Docente?
        get() = sesionDocente?.let { (id, huella) -> docentes.find { it.id == id && it.huellaContrasena == huella } }

    /** La sesión de la familia es la huella de la clave con la que entró: si el colegio la cambia, la sesión se acaba (RN-20). */
    private var huellaDeLaSesion by mutableStateOf<String?>(null)

    val acudienteActual: Acudiente?
        get() = huellaDeLaSesion?.let { huella -> acudientes.find { it.huellaClave == huella } }

    /** Clave recién generada que el colegio debe entregar en persona. Se muestra una sola vez y no se guarda (RN-20 y RN-23). */
    var claveParaEntregar by mutableStateOf<ClaveParaEntregar?>(null)
        private set

    private var hijoSeleccionadoId by mutableStateOf<Int?>(null)

    // Un ingreso correcto NO borra los fallos anteriores: así nadie puede intercalar su propia clave
    // para seguir probando claves ajenas sin que se active el bloqueo (RN-06)
    private val limiteColegio = LimiteIntentos()
    private val limiteDocente = LimiteIntentos()
    private val limiteClaves = LimiteIntentos()

    /** Al pasar la medianoche, las pendientes de ayer pasan a vencidas al volver a la app (ReqF-15). */
    fun actualizarFecha() {
        hoy = LocalDate.now(ZONA_COLOMBIA)
    }

    /** Cierra la sesión que esté abierta (colegio, docente o familia). */
    fun cerrarSesion() {
        colegioActual = null
        sesionDocente = null
        huellaDeLaSesion = null
        hijoSeleccionadoId = null
        claveParaEntregar = null
    }

    /**
     * Guarda en el celular una foto de los datos. Se llama después de cada cambio.
     * La foto se toma aquí, en el hilo de la pantalla, y se escribe en segundo plano.
     */
    private fun guardar() {
        val foto = DatosGuardados(
            docentes = docentes.toList(),
            cursos = cursos.toList(),
            tareas = tareas.toList(),
            acudientes = acudientes.toList(),
            estudiantes = estudiantes.toList(),
            proximoIdEstudiante = proximoIdEstudiante,
            proximoIdAcudiente = proximoIdAcudiente,
            proximoIdTarea = proximoIdTarea
        )
        alcanceGuardado.launch { almacen.guardar(foto) }
    }

    fun buscarCurso(cursoId: Int): Curso? = cursos.find { it.id == cursoId }

    fun nombreDocente(docenteId: Int): String = docentes.find { it.id == docenteId }?.nombre.orEmpty()

    /**
     * Estudiantes de un curso, ordenados por nombre. Solo los ven el colegio y el docente dueño del curso:
     * un acudiente nunca ve quiénes están en el curso (RN-21).
     */
    fun estudiantesDelCurso(cursoId: Int): List<Estudiante> {
        val curso = buscarCurso(cursoId) ?: return emptyList()
        val autorizado = colegioActual != null || docenteActual?.id == curso.docenteId
        if (!autorizado) return emptyList()
        return estudiantes.filter { it.cursoId == cursoId }.sortedBy { it.nombre }
    }

    // ======================= Colegio =======================

    fun iniciarSesionColegio(correo: String, contrasena: String): Resultado<CuentaColegio> {
        if (!limiteColegio.puedeIntentar()) return Resultado.Error(MENSAJE_DEMASIADOS_INTENTOS)
        val correoNormalizado = correo.trim().lowercase()
        val cuenta = colegios.find { it.correo == correoNormalizado && it.contrasena == contrasena }
        if (cuenta == null) {
            limiteColegio.registrarFallo()
            return Resultado.Error("Correo o contraseña incorrectos") // mensaje genérico (ReqF-1)
        }
        cerrarSesion()
        colegioActual = cuenta
        return Resultado.Exito(cuenta)
    }

    /** Todos los cursos del colegio, por grado y nombre. */
    fun cursosDelColegio(): List<Curso> =
        if (colegioActual == null) emptyList()
        else cursos.sortedWith(compareBy<Curso> { it.grado }.thenBy { it.nombre }.thenBy { it.id })

    fun buscarEstudiante(estudianteId: Int): Estudiante? =
        if (colegioActual == null) null else estudiantes.find { it.id == estudianteId }

    fun buscarAcudiente(acudienteId: Int): Acudiente? =
        if (colegioActual == null) null else acudientes.find { it.id == acudienteId }

    /** Otros estudiantes con el mismo acudiente: entran con la misma clave familiar. */
    fun hermanosDe(estudiante: Estudiante): List<Estudiante> =
        if (colegioActual == null) emptyList()
        else estudiantes.filter { it.acudienteId == estudiante.acudienteId && it.id != estudiante.id }.sortedBy { it.nombre }

    /**
     * Matricula a un estudiante (RN-19). Solo el colegio puede hacerlo, después de revisar en persona
     * los documentos del estudiante y la identidad del acudiente ([verificado]).
     * Si [acudienteId] es null se crea el acudiente con una clave familiar nueva, que queda en
     * [claveParaEntregar]; si no, el hermano o la hermana usa la clave que la familia ya tiene.
     */
    fun matricular(
        nombre: String,
        cursoId: Int?,
        avatar: String?,
        acudienteId: Int?,
        nombreAcudiente: String,
        verificado: Boolean
    ): Resultado<Estudiante> {
        if (colegioActual == null) return Resultado.Error("Inicie sesión de nuevo")
        if (!verificado) return Resultado.Error("Primero revise los documentos en persona")
        Validaciones.validarNombrePersona(nombre)?.let { return Resultado.Error("Nombre del estudiante: ${it.enMinuscula()}") }
        val curso = cursoId?.let { buscarCurso(it) } ?: return Resultado.Error("Elija el curso")
        if (avatar == null) return Resultado.Error("Elija un dibujo para el estudiante")

        val nombreLimpio = Validaciones.limpiarTexto(nombre)
        if (estudiantes.any { it.cursoId == curso.id && it.nombre.equals(nombreLimpio, ignoreCase = true) }) {
            return Resultado.Error("Ya hay un estudiante con ese nombre en ${curso.titulo}")
        }

        // El acudiente que ya tiene clave (hermanos) o uno nuevo
        val existente = acudienteId?.let { id -> acudientes.find { it.id == id } }
        if (acudienteId != null && existente == null) return Resultado.Error("No se encontró el acudiente")
        if (existente != null && estudiantes.count { it.acudienteId == existente.id } >= MAX_HIJOS) {
            return Resultado.Error("Un acudiente puede tener máximo $MAX_HIJOS estudiantes") // RN-09
        }
        if (existente != null && estudiantes.any { it.acudienteId == existente.id && it.nombre.equals(nombreLimpio, ignoreCase = true) }) {
            return Resultado.Error("$nombreLimpio ya está matriculado(a) con ${existente.nombre}")
        }
        // Dos acudientes distintos pueden llamarse igual: el nombre no identifica a una persona.
        // Por eso un acudiente nuevo siempre recibe su propia clave; los hermanos se matriculan desde la ficha.
        var claveNueva: String? = null
        val acudiente = existente ?: run {
            Validaciones.validarNombrePersona(nombreAcudiente)?.let { return Resultado.Error("Nombre del acudiente: ${it.enMinuscula()}") }
            val (clave, huella) = nuevaClaveFamiliar()
            claveNueva = clave
            Acudiente(
                id = proximoIdAcudiente++,
                nombre = Validaciones.limpiarTexto(nombreAcudiente),
                huellaClave = huella
            ).also { acudientes.add(it) }
        }

        val estudiante = Estudiante(
            id = proximoIdEstudiante++,
            nombre = nombreLimpio,
            avatar = avatar,
            cursoId = curso.id,
            acudienteId = acudiente.id
        )
        estudiantes.add(estudiante)
        guardar()
        claveNueva?.let { clave ->
            claveParaEntregar = ClaveParaEntregar(
                tipo = TipoClave.FAMILIA_NUEVA,
                nombre = acudiente.nombre,
                detalle = "Acudiente de ${estudiante.nombre}",
                clave = clave,
                estudiantes = listOf(describir(estudiante))
            )
        }
        return Resultado.Exito(estudiante)
    }

    /** Genera una clave familiar nueva: la anterior deja de funcionar en todos los celulares (RN-20). */
    fun generarNuevaClave(acudienteId: Int): Resultado<ClaveParaEntregar> {
        if (colegioActual == null) return Resultado.Error("Inicie sesión de nuevo")
        val indice = acudientes.indexOfFirst { it.id == acudienteId }
        if (indice == -1) return Resultado.Error("No se encontró el acudiente")

        val (clave, huella) = nuevaClaveFamiliar()
        val acudiente = acudientes[indice].copy(huellaClave = huella)
        acudientes[indice] = acudiente
        guardar()
        val hijos = estudiantes.filter { it.acudienteId == acudienteId }.sortedBy { it.id }
        val claveNueva = ClaveParaEntregar(
            tipo = TipoClave.CLAVE_CAMBIADA,
            nombre = acudiente.nombre,
            detalle = "Acudiente de ${unirNombres(hijos.map { it.nombre })}",
            clave = clave,
            estudiantes = hijos.map { describir(it) }
        )
        claveParaEntregar = claveNueva
        return Resultado.Exito(claveNueva)
    }

    /**
     * Retira a un estudiante: deja de aparecer en su curso y en la app de su familia.
     * Si su acudiente se queda sin estudiantes, también se borra y su clave deja de funcionar.
     */
    fun retirarEstudiante(estudianteId: Int) {
        if (colegioActual == null) return
        val estudiante = estudiantes.find { it.id == estudianteId } ?: return
        estudiantes.remove(estudiante)
        if (estudiantes.none { it.acudienteId == estudiante.acudienteId }) {
            acudientes.removeAll { it.id == estudiante.acudienteId }
        }
        guardar()
    }

    fun docentesDelColegio(): List<Docente> =
        if (colegioActual == null) emptyList() else docentes.sortedBy { it.nombre }

    fun cantidadCursos(docenteId: Int): Int = cursos.count { it.docenteId == docenteId }

    /**
     * Crea la cuenta de un docente (RN-23): ya nadie se registra solo.
     * Su contraseña queda en [claveParaEntregar] para entregarla en persona; la app solo guarda su huella.
     */
    fun crearCuentaDocente(nombre: String, correo: String): Resultado<Docente> {
        if (colegioActual == null) return Resultado.Error("Inicie sesión de nuevo")
        val error = Validaciones.validarNombrePersona(nombre) ?: Validaciones.validarCorreo(correo)
        if (error != null) return Resultado.Error(error)

        val correoNormalizado = correo.trim().lowercase() // RN-01: correo único sin distinguir mayúsculas
        if (docentes.any { it.correo == correoNormalizado } || colegios.any { it.correo == correoNormalizado }) {
            return Resultado.Error("Este correo ya tiene una cuenta")
        }

        val contrasena = GeneradorClave.generar(emptyList()) // 8 caracteres con letras y números (RN-02)
        val docente = Docente(
            id = siguienteId(docentes.map { it.id }),
            nombre = Validaciones.limpiarTexto(nombre),
            correo = correoNormalizado,
            huellaContrasena = Huella.deContrasena(contrasena)
        )
        docentes.add(docente)
        guardar()
        claveParaEntregar = ClaveParaEntregar(
            tipo = TipoClave.DOCENTE_NUEVO,
            nombre = docente.nombre,
            detalle = docente.correo,
            clave = contrasena
        )
        return Resultado.Exito(docente)
    }

    /**
     * Genera una contraseña nueva para un docente (RN-23): la anterior deja de funcionar y su sesión se cierra.
     * Sirve si la olvidó y también si ya no trabaja en el colegio (en ese caso, no se le entrega).
     */
    fun generarNuevaContrasena(docenteId: Int): Resultado<ClaveParaEntregar> {
        if (colegioActual == null) return Resultado.Error("Inicie sesión de nuevo")
        val indice = docentes.indexOfFirst { it.id == docenteId }
        if (indice == -1) return Resultado.Error("No se encontró el docente")

        val contrasena = GeneradorClave.generar(emptyList())
        val docente = docentes[indice].copy(huellaContrasena = Huella.deContrasena(contrasena))
        docentes[indice] = docente
        guardar()
        val clave = ClaveParaEntregar(
            tipo = TipoClave.CONTRASENA_CAMBIADA,
            nombre = docente.nombre,
            detalle = docente.correo,
            clave = contrasena
        )
        claveParaEntregar = clave
        return Resultado.Exito(clave)
    }

    /** El colegio ya anotó o entregó la clave: la app no la vuelve a mostrar. */
    fun descartarClave() {
        claveParaEntregar = null
    }

    /** Clave familiar nueva que no tenga nadie, con su huella (lo único que se guarda). */
    private fun nuevaClaveFamiliar(): Pair<String, String> {
        val huellasEnUso = acudientes.map { it.huellaClave }.toSet()
        var huella = ""
        // generar() pregunta por la clave que va a devolver justo antes de devolverla: esa es la huella que queda
        val clave = GeneradorClave.generar { candidata -> Huella.deClave(candidata).also { huella = it } in huellasEnUso }
        return clave to huella
    }

    /** "Sofía Pérez · 3° · Sede Centro, jornada mañana": para el texto que se copia con la clave. */
    private fun describir(estudiante: Estudiante): String {
        val curso = buscarCurso(estudiante.cursoId)?.titulo
        return if (curso == null) estudiante.nombre else "${estudiante.nombre} · $curso"
    }

    // ======================= Docente =======================

    fun iniciarSesionDocente(correo: String, contrasena: String): Resultado<Docente> {
        if (!limiteDocente.puedeIntentar()) return Resultado.Error(MENSAJE_DEMASIADOS_INTENTOS)
        val correoNormalizado = correo.trim().lowercase()
        val huella = Huella.deContrasena(contrasena)
        val docente = docentes.find { it.correo == correoNormalizado && it.huellaContrasena == huella }
        if (docente == null) {
            limiteDocente.registrarFallo()
            return Resultado.Error("Correo o contraseña incorrectos") // mensaje genérico (ReqF-1)
        }
        cerrarSesion()
        sesionDocente = docente.id to docente.huellaContrasena
        return Resultado.Exito(docente)
    }

    /** Un docente solo ve y gestiona sus propios cursos (RN-03). */
    fun cursosDelDocente(): List<Curso> = cursos.filter { it.docenteId == docenteActual?.id }

    fun crearCurso(grado: Int?, nombre: String): Resultado<Curso> {
        val docente = docenteActual ?: return Resultado.Error("Inicie sesión de nuevo")
        val error = Validaciones.validarGrado(grado) ?: Validaciones.validarNombreCurso(nombre)
        if (error != null || grado == null) return Resultado.Error(error ?: "Elija el grado")

        val nombreLimpio = nombre.trim()
        val propios = cursosDelDocente()
        if (propios.size >= MAX_CURSOS) return Resultado.Error("Puede tener máximo $MAX_CURSOS cursos")
        if (propios.any { it.grado == grado && it.nombre.equals(nombreLimpio, ignoreCase = true) }) {
            return Resultado.Error("Ya tiene un curso de $grado° con ese nombre")
        }

        val curso = Curso(
            id = siguienteId(cursos.map { it.id }),
            docenteId = docente.id,
            grado = grado,
            nombre = nombreLimpio
        )
        cursos.add(curso)
        guardar()
        return Resultado.Exito(curso)
    }

    /** Tareas de un curso para su docente (RN-03); a nadie más le responde. */
    fun tareasDelCurso(cursoId: Int): List<Tarea> {
        val curso = buscarCurso(cursoId)
        if (curso == null || curso.docenteId != docenteActual?.id) return emptyList()
        return ReglasTareas.listaDocente(tareas.filter { it.cursoId == cursoId }, hoy)
    }

    /** Cuántas entregas validaron las familias en cada tarea del curso: id de la tarea → cantidad (RN-22). */
    fun validacionesDelCurso(cursoId: Int): Map<Int, Int> {
        val curso = buscarCurso(cursoId)
        if (curso == null || curso.docenteId != docenteActual?.id) return emptyMap()
        val delCurso = estudiantesDelCurso(cursoId)
        return tareas.filter { it.cursoId == cursoId }
            .associate { it.id to ReglasTareas.contarValidadas(it.id, delCurso) }
    }

    fun publicarTarea(cursoId: Int, texto: String, fechaLimite: LocalDate?): Resultado<Tarea> {
        val curso = buscarCurso(cursoId)
        if (curso == null || curso.docenteId != docenteActual?.id) {
            return Resultado.Error("No puede publicar en este curso") // RN-03
        }
        val error = Validaciones.validarTextoTarea(texto) ?: Validaciones.validarFechaLimite(fechaLimite, hoy)
        if (error != null || fechaLimite == null) return Resultado.Error(error ?: "Elija la fecha límite")

        val delCurso = tareas.filter { it.cursoId == cursoId }
        if (ReglasTareas.contarVigentes(delCurso, hoy) >= ReglasTareas.MAX_TAREAS_VIGENTES) {
            return Resultado.Error("El curso ya tiene ${ReglasTareas.MAX_TAREAS_VIGENTES} tareas vigentes")
        }

        val tarea = Tarea(
            id = proximoIdTarea++,
            cursoId = cursoId,
            texto = Validaciones.limpiarTexto(texto),
            fechaLimite = fechaLimite
        )
        tareas.add(tarea)
        guardar()
        return Resultado.Exito(tarea)
    }

    /** Una tarea de un curso del docente con sesión, para editarla o eliminarla. De otro docente: null (RN-03). */
    fun buscarTareaDelDocente(tareaId: Int): Tarea? {
        val docente = docenteActual ?: return null
        val tarea = tareas.find { it.id == tareaId } ?: return null
        return if (buscarCurso(tarea.cursoId)?.docenteId == docente.id) tarea else null
    }

    /**
     * El docente corrige el texto o cambia la fecha límite de una tarea (RN-24, con RN-03 y RN-10).
     * La fecha solo se revisa si cambió: así se puede corregir el texto de una tarea que ya venció.
     * Las entregas que ya validaron las familias se conservan.
     */
    fun editarTarea(tareaId: Int, texto: String, fechaLimite: LocalDate?): Resultado<Tarea> {
        val tarea = buscarTareaDelDocente(tareaId) ?: return Resultado.Error("No puede editar esta tarea")
        Validaciones.validarTextoTarea(texto)?.let { return Resultado.Error(it) }
        if (fechaLimite == null) return Resultado.Error("Elija la fecha límite")
        if (fechaLimite != tarea.fechaLimite) {
            Validaciones.validarFechaLimite(fechaLimite, hoy)?.let { return Resultado.Error(it) }
            // Una tarea vencida que vuelve a estar vigente cuenta para el máximo del curso (RN-10)
            val vuelveAEstarVigente = ReglasTareas.fechaVencida(tarea.fechaLimite, hoy)
            val vigentes = ReglasTareas.contarVigentes(tareas.filter { it.cursoId == tarea.cursoId }, hoy)
            if (vuelveAEstarVigente && vigentes >= ReglasTareas.MAX_TAREAS_VIGENTES) {
                return Resultado.Error("El curso ya tiene ${ReglasTareas.MAX_TAREAS_VIGENTES} tareas vigentes")
            }
        }

        val editada = tarea.copy(texto = Validaciones.limpiarTexto(texto), fechaLimite = fechaLimite)
        tareas[tareas.indexOfFirst { it.id == tareaId }] = editada
        guardar()
        return Resultado.Exito(editada)
    }

    /**
     * El docente elimina una tarea de su curso (RN-24): las familias dejan de verla y se borran sus validaciones.
     * Su id no se vuelve a usar, así una tarea nueva nunca aparece validada por error.
     */
    fun eliminarTarea(tareaId: Int): Resultado<Tarea> {
        val tarea = buscarTareaDelDocente(tareaId) ?: return Resultado.Error("No puede eliminar esta tarea")
        tareas.removeAll { it.id == tareaId }
        for (indice in estudiantes.indices) {
            val estudiante = estudiantes[indice]
            if (tareaId in estudiante.tareasValidadas) {
                estudiantes[indice] = estudiante.copy(tareasValidadas = estudiante.tareasValidadas - tareaId)
            }
        }
        guardar()
        return Resultado.Exito(tarea)
    }

    // ======================= Acudiente =======================

    /**
     * Ingreso de la familia: solo la clave que entregó el colegio, sin registro (RN-20).
     * El mismo mensaje sirve para una clave que no existe y para una que ya no sirve,
     * para no darle pistas a quien intente adivinar claves (RN-06).
     */
    fun ingresarConClave(texto: String): Resultado<Acudiente> {
        val clave = GeneradorClave.normalizar(texto)
        Validaciones.validarFormatoClave(clave)?.let { return Resultado.Error(it) }
        if (!limiteClaves.puedeIntentar()) return Resultado.Error(MENSAJE_DEMASIADOS_INTENTOS)

        val huella = Huella.deClave(clave)
        val acudiente = acudientes.find { it.huellaClave == huella }
        if (acudiente == null || estudiantes.none { it.acudienteId == acudiente.id }) {
            limiteClaves.registrarFallo()
            return Resultado.Error("Esa clave no funciona. Revísela o pida una nueva en el colegio")
        }
        cerrarSesion()
        huellaDeLaSesion = huella
        return Resultado.Exito(acudiente)
    }

    /** Hijos del acudiente con sesión, en el orden en que los matriculó el colegio. Solo los suyos: nunca la lista del curso (RN-21). */
    val hijos: List<Estudiante>
        get() {
            val acudiente = acudienteActual ?: return emptyList()
            return estudiantes.filter { it.acudienteId == acudiente.id }.sortedBy { it.id }
        }

    /** Hijo cuya pizarra se muestra. Si no se ha elegido ninguno, el primero (ReqF-6). */
    val hijoSeleccionado: Estudiante?
        get() = hijos.let { lista -> lista.find { it.id == hijoSeleccionadoId } ?: lista.firstOrNull() }

    fun seleccionarHijo(hijoId: Int) {
        if (hijos.any { it.id == hijoId }) hijoSeleccionadoId = hijoId
    }

    /** Tareas del curso del hijo con su estado, en el orden de la pizarra (ReqF-13 y ReqF-15). */
    fun pizarraDe(hijo: Estudiante): List<TareaConEstado> {
        if (hijo.acudienteId != acudienteActual?.id) return emptyList() // solo de los propios hijos (RN-21)
        return ReglasTareas.pizarra(tareas.filter { it.cursoId == hijo.cursoId }, hijo.tareasValidadas, hoy)
    }

    /**
     * El acudiente valida que su hijo entregó una tarea, o quita esa validación (RN-22).
     * Solo puede hacerlo con sus propios hijos y con tareas del curso de cada uno (RN-12).
     */
    fun cambiarValidacion(hijoId: Int, tareaId: Int, validada: Boolean) {
        val acudiente = acudienteActual ?: return
        val indice = estudiantes.indexOfFirst { it.id == hijoId && it.acudienteId == acudiente.id }
        if (indice == -1) return
        val hijo = estudiantes[indice]
        if (tareas.none { it.id == tareaId && it.cursoId == hijo.cursoId }) return
        val nuevas = if (validada) hijo.tareasValidadas + tareaId else hijo.tareasValidadas - tareaId
        estudiantes[indice] = hijo.copy(tareasValidadas = nuevas)
        guardar()
    }

    companion object {
        val ZONA_COLOMBIA: ZoneId = ZoneId.of("America/Bogota")
        const val MAX_CURSOS = 10   // RN-04
        const val MAX_HIJOS = 6     // RN-09: estudiantes por acudiente
        const val MENSAJE_DEMASIADOS_INTENTOS = "Demasiados intentos. Espere un minuto"

        /**
         * Crea el ViewModel de la app guardando los datos en la memoria interna del celular.
         * Lo usa AppNavegacion con viewModel(factory = PizarraViewModel.Factory).
         */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val aplicacion = checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                PizarraViewModel(almacen = AlmacenEnArchivo(AlmacenEnArchivo.archivoEn(aplicacion.filesDir)))
            }
        }

        private fun siguienteId(usados: List<Int>): Int = (usados.maxOrNull() ?: 0) + 1

        /** "Sofía", "Sofía y Juan", "Sofía, Juan y Ana". */
        private fun unirNombres(nombres: List<String>): String = when (nombres.size) {
            0 -> ""
            1 -> nombres.first()
            else -> nombres.dropLast(1).joinToString(", ") + " y " + nombres.last()
        }

        private fun String.enMinuscula(): String = replaceFirstChar { it.lowercase() }
    }
}
