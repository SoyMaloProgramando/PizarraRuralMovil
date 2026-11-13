package com.example.pizarraruralmovil.model

/**
 * Estudiante matriculado por el colegio después de verificar sus documentos en persona (RN-19).
 * De cada estudiante se guarda nombre, avatar, curso, su acudiente y las tareas validadas;
 * los documentos se revisan en persona y no se guardan en la app (RNF-31).
 * [tareasValidadas] tiene los id de las tareas que su acudiente validó: es propio de cada estudiante (RN-12).
 */
data class Estudiante(
    val id: Int,
    val nombre: String,
    val avatar: String,
    val cursoId: Int,
    val acudienteId: Int,
    val tareasValidadas: Set<Int> = emptySet()
)
