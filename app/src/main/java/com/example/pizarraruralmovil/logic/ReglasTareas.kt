package com.example.pizarraruralmovil.logic

import com.example.pizarraruralmovil.model.EstadoTarea
import com.example.pizarraruralmovil.model.Estudiante
import com.example.pizarraruralmovil.model.ResumenTareas
import com.example.pizarraruralmovil.model.Tarea
import com.example.pizarraruralmovil.model.TareaConEstado
import java.time.LocalDate

/** Reglas de negocio de las tareas: estado, orden, ventana visible, resumen y validaciones. */
object ReglasTareas {

    /** Se muestran las tareas con fecha límite de los últimos 14 días en adelante (RN-14). */
    const val DIAS_VISIBLES_ATRAS = 14L

    /** Máximo de tareas vigentes (fecha límite de hoy en adelante) por curso (RN-10). */
    const val MAX_TAREAS_VIGENTES = 50

    /**
     * Estado de una tarea (RN-11):
     * Validada si el acudiente confirmó la entrega (sin importar la fecha), Vencida si no está
     * validada y su fecha ya pasó, y Pendiente en los demás casos. La fecha límite vence a las 11:59 p. m.
     */
    fun calcularEstado(fechaLimite: LocalDate, validada: Boolean, hoy: LocalDate): EstadoTarea = when {
        validada -> EstadoTarea.VALIDADA
        fechaVencida(fechaLimite, hoy) -> EstadoTarea.VENCIDA
        else -> EstadoTarea.PENDIENTE
    }

    /** La fecha límite ya pasó (vence a las 11:59 p. m. de ese día). */
    fun fechaVencida(fechaLimite: LocalDate, hoy: LocalDate): Boolean = fechaLimite.isBefore(hoy)

    fun esVisible(tarea: Tarea, hoy: LocalDate): Boolean =
        !tarea.fechaLimite.isBefore(hoy.minusDays(DIAS_VISIBLES_ATRAS))

    fun contarVigentes(tareas: List<Tarea>, hoy: LocalDate): Int =
        tareas.count { !it.fechaLimite.isBefore(hoy) }

    /** Lista del docente: todas por fecha límite ascendente (RN-13). */
    fun listaDocente(tareas: List<Tarea>, hoy: LocalDate): List<Tarea> =
        tareas.filter { esVisible(it, hoy) }
            .sortedWith(compareBy<Tarea> { it.fechaLimite }.thenBy { it.id })

    /**
     * Pizarra de un estudiante (RN-13): primero las no validadas por fecha límite
     * (así las vencidas quedan arriba) y al final las validadas.
     */
    fun pizarra(tareas: List<Tarea>, tareasValidadas: Set<Int>, hoy: LocalDate): List<TareaConEstado> =
        tareas.filter { esVisible(it, hoy) }
            .map { TareaConEstado(it, calcularEstado(it.fechaLimite, it.id in tareasValidadas, hoy)) }
            .sortedWith(
                compareBy<TareaConEstado> { it.estado == EstadoTarea.VALIDADA }
                    .thenBy { it.tarea.fechaLimite }
                    .thenBy { it.tarea.id }
            )

    /** Resumen de la pizarra (ReqF-16): "pendientes" cuenta todas las no validadas, incluidas las vencidas. */
    fun resumen(tareas: List<TareaConEstado>): ResumenTareas = ResumenTareas(
        pendientes = tareas.count { it.estado != EstadoTarea.VALIDADA },
        validadas = tareas.count { it.estado == EstadoTarea.VALIDADA },
        vencidas = tareas.count { it.estado == EstadoTarea.VENCIDA }
    )

    /** Cuántos estudiantes del curso tienen la entrega validada por su acudiente (lo ve el docente, RN-22). */
    fun contarValidadas(tareaId: Int, estudiantesDelCurso: List<Estudiante>): Int =
        estudiantesDelCurso.count { tareaId in it.tareasValidadas }
}
