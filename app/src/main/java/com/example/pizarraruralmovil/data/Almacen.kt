package com.example.pizarraruralmovil.data

import com.example.pizarraruralmovil.model.Acudiente
import com.example.pizarraruralmovil.model.Curso
import com.example.pizarraruralmovil.model.Docente
import com.example.pizarraruralmovil.model.Estudiante
import com.example.pizarraruralmovil.model.Tarea

/**
 * Todo lo que la app guarda en el celular para no perderlo al cerrarla:
 * docentes, cursos, tareas, acudientes, estudiantes con sus entregas validadas
 * y los contadores de id (los id no se reutilizan aunque se borre algo).
 * Las sesiones no se guardan: al abrir la app de nuevo, cada quien vuelve a entrar.
 */
data class DatosGuardados(
    val docentes: List<Docente>,
    val cursos: List<Curso>,
    val tareas: List<Tarea>,
    val acudientes: List<Acudiente>,
    val estudiantes: List<Estudiante>,
    val proximoIdEstudiante: Int,
    val proximoIdAcudiente: Int,
    val proximoIdTarea: Int
)

/**
 * Dónde se guardan los datos. El ViewModel no sabe si es un archivo, una base de datos o la API:
 * solo pide [cargar] al empezar y [guardar] después de cada cambio. Así, cuando llegue el servidor,
 * se cambia esta pieza y las pantallas siguen igual.
 */
interface Almacen {

    /** Los datos guardados, o null si todavía no hay nada (la primera vez se usan los de prueba). */
    fun cargar(): DatosGuardados?

    fun guardar(datos: DatosGuardados)

    /** No guarda nada: lo usan las pruebas y las vistas previas. */
    object SoloEnMemoria : Almacen {
        override fun cargar(): DatosGuardados? = null
        override fun guardar(datos: DatosGuardados) = Unit
    }
}
