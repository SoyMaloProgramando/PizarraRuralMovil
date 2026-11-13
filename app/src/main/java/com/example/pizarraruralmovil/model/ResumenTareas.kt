package com.example.pizarraruralmovil.model

/** Conteo que se muestra arriba de la pizarra (ReqF-16). */
data class ResumenTareas(
    val pendientes: Int,  // todas las no validadas, incluidas las vencidas
    val validadas: Int,
    val vencidas: Int
)
