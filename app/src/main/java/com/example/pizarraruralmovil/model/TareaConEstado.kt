package com.example.pizarraruralmovil.model

/** Una tarea tal como se ve en la pizarra de un estudiante: la tarea más su estado calculado. */
data class TareaConEstado(
    val tarea: Tarea,
    val estado: EstadoTarea
)
