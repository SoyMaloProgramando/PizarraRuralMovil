package com.example.pizarraruralmovil.model

import java.time.LocalDate

/** Tarea publicada por el docente en un curso (ReqF-8). */
data class Tarea(
    val id: Int,
    val cursoId: Int,
    val texto: String,            // de 1 a 120 caracteres, solo texto (RN-10)
    val fechaLimite: LocalDate    // sin hora, en la zona horaria de Colombia (RN-18)
)
