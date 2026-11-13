package com.example.pizarraruralmovil.model

/**
 * Curso creado por un docente (ReqF-2). Agrupa las tareas.
 * Ya no tiene código para compartir: los estudiantes entran al curso solo cuando
 * el colegio los matricula (RN-19).
 */
data class Curso(
    val id: Int,
    val docenteId: Int,
    val grado: Int,       // de 1 a 5 (RN-04)
    val nombre: String    // de 3 a 40 caracteres, por ejemplo "Sede Centro, jornada mañana"
) {
    /** Texto corto para mostrar el curso, por ejemplo "3° · Sede Centro, jornada mañana". */
    val titulo: String get() = "$grado° · $nombre"
}
