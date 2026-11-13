package com.example.pizarraruralmovil.logic

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/** Fechas fáciles de leer en lugar de formatos numéricos (RNF-13). */
object FechasLegibles {

    private val DIAS = listOf("lunes", "martes", "miércoles", "jueves", "viernes", "sábado", "domingo")
    private val MESES = listOf(
        "enero", "febrero", "marzo", "abril", "mayo", "junio",
        "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
    )

    private fun nombreDia(fecha: LocalDate) = DIAS[fecha.dayOfWeek.value - 1]

    /** Ejemplo: "lunes 2 de noviembre". */
    fun fechaLarga(fecha: LocalDate): String =
        "${nombreDia(fecha)} ${fecha.dayOfMonth} de ${MESES[fecha.monthValue - 1]}"

    /** Igual que [fechaLarga] pero con mayúscula inicial, para títulos: "Lunes 2 de noviembre". */
    fun fechaTitulo(fecha: LocalDate): String = fechaLarga(fecha).replaceFirstChar { it.uppercase() }

    /** Ejemplos: "Vence hoy", "Vence mañana", "Vence el lunes 2", "Venció ayer". */
    fun vencimiento(fechaLimite: LocalDate, hoy: LocalDate): String {
        val dias = ChronoUnit.DAYS.between(hoy, fechaLimite)
        return when {
            dias == 0L -> "Vence hoy"
            dias == 1L -> "Vence mañana"
            dias in 2L..6L -> "Vence el ${nombreDia(fechaLimite)} ${fechaLimite.dayOfMonth}"
            dias >= 7L -> "Vence el ${fechaLarga(fechaLimite)}"
            dias == -1L -> "Venció ayer"
            else -> "Venció el ${nombreDia(fechaLimite)} ${fechaLimite.dayOfMonth}"
        }
    }
}
