package com.example.pizarraruralmovil.navigation

/** Rutas de navegación de la app. Cada pantalla tiene un nombre único. */
object Rutas {
    // Inicio: elegir rol e ingresar (nadie se registra desde aquí)
    const val SELECCION_ROL = "seleccion_rol"
    const val INGRESO_COLEGIO = "ingreso_colegio"
    const val INGRESO_DOCENTE = "ingreso_docente"
    const val INGRESO_FAMILIA = "ingreso_familia"

    // Colegio
    const val ESTUDIANTES = "colegio/estudiantes"
    const val DOCENTES = "colegio/docentes"
    const val MATRICULAR = "colegio/matricular?acudienteId={acudienteId}"
    const val FICHA_ESTUDIANTE = "colegio/estudiante/{estudianteId}"
    const val AGREGAR_DOCENTE = "colegio/agregar_docente"
    const val CLAVE_PARA_ENTREGAR = "colegio/clave_para_entregar"

    // Docente
    const val MIS_CURSOS = "mis_cursos"
    const val CREAR_CURSO = "crear_curso"
    const val CURSO = "curso/{cursoId}"
    const val NUEVA_TAREA = "nueva_tarea/{cursoId}"
    const val EDITAR_TAREA = "editar_tarea/{tareaId}"

    // Padre, madre o acudiente
    const val PIZARRA = "pizarra"
    const val MIS_HIJOS = "mis_hijos"

    /** Nombres de los argumentos que viajan en las rutas. */
    const val ARG_CURSO_ID = "cursoId"
    const val ARG_ESTUDIANTE_ID = "estudianteId"
    const val ARG_ACUDIENTE_ID = "acudienteId"
    const val ARG_TAREA_ID = "tareaId"

    /** Valor del argumento opcional cuando se matricula a un estudiante de una familia nueva. */
    const val SIN_ACUDIENTE = -1

    fun curso(cursoId: Int) = "curso/$cursoId"
    fun nuevaTarea(cursoId: Int) = "nueva_tarea/$cursoId"
    fun editarTarea(tareaId: Int) = "editar_tarea/$tareaId"
    fun fichaEstudiante(estudianteId: Int) = "colegio/estudiante/$estudianteId"

    /** Sin acudiente: familia nueva (se genera su clave). Con acudiente: hermano o hermana (usa la misma clave). */
    fun matricular(acudienteId: Int? = null) =
        if (acudienteId == null) "colegio/matricular" else "colegio/matricular?acudienteId=$acudienteId"
}
