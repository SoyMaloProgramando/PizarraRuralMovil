package com.example.pizarraruralmovil.data

import com.example.pizarraruralmovil.logic.Huella
import com.example.pizarraruralmovil.model.Acudiente
import com.example.pizarraruralmovil.model.CuentaColegio
import com.example.pizarraruralmovil.model.Curso
import com.example.pizarraruralmovil.model.Docente
import com.example.pizarraruralmovil.model.Estudiante
import com.example.pizarraruralmovil.model.Tarea
import java.time.LocalDate

/**
 * Datos de ejemplo para probar la interfaz sin servidor. La app arranca con ellos la primera vez;
 * después usa lo que quedó guardado en el celular (ver AlmacenEnArchivo).
 * En el siguiente sprint estos datos llegarán de la API (Node.js + Express + SQLite).
 */
object DatosDePrueba {

    const val CORREO_COLEGIO = "colegio@pizarra.co"
    const val CONTRASENA_COLEGIO = "colegio123"
    const val CORREO_DOCENTE = "docente@pizarra.co"
    const val CONTRASENA_DOCENTE = "pizarra123"

    /** Clave familiar de María Pérez, acudiente de Sofía (3°) y de Juan (5°). */
    const val CLAVE_FAMILIA_PEREZ = "MESA3482"

    /** Clave familiar de Andrés Rojas, acudiente de Valentina (3°). */
    const val CLAVE_FAMILIA_ROJAS = "RANA5927"

    /** Clave familiar de Carmen Díaz, acudiente de Mateo (3°). */
    const val CLAVE_FAMILIA_DIAZ = "TREN6843"

    val colegios = listOf(
        CuentaColegio(id = 1, nombre = "Secretaría · Sede Centro", correo = CORREO_COLEGIO, contrasena = CONTRASENA_COLEGIO)
    )

    // by lazy: las huellas solo se calculan si de verdad se usan estos datos (la primera vez que se abre la app)
    val docentes: List<Docente> by lazy {
        listOf(Docente(id = 1, nombre = "Profe Demo", correo = CORREO_DOCENTE, huellaContrasena = Huella.deContrasena(CONTRASENA_DOCENTE)))
    }

    val cursos = listOf(
        Curso(id = 1, docenteId = 1, grado = 3, nombre = "Sede Centro, jornada mañana"),
        Curso(id = 2, docenteId = 1, grado = 5, nombre = "Sede Centro, jornada mañana")
    )

    val acudientes: List<Acudiente> by lazy {
        listOf(
            Acudiente(id = 1, nombre = "María Pérez", huellaClave = Huella.deClave(CLAVE_FAMILIA_PEREZ)),
            Acudiente(id = 2, nombre = "Andrés Rojas", huellaClave = Huella.deClave(CLAVE_FAMILIA_ROJAS)),
            Acudiente(id = 3, nombre = "Carmen Díaz", huellaClave = Huella.deClave(CLAVE_FAMILIA_DIAZ))
        )
    }

    /** Estudiantes que el colegio ya matriculó, con algunas entregas validadas por sus acudientes. */
    val estudiantes = listOf(
        Estudiante(id = 1, nombre = "Sofía Pérez", avatar = "🐰", cursoId = 1, acudienteId = 1, tareasValidadas = setOf(3)),
        Estudiante(id = 2, nombre = "Juan Pérez", avatar = "🦊", cursoId = 2, acudienteId = 1),
        Estudiante(id = 3, nombre = "Valentina Rojas", avatar = "🐼", cursoId = 1, acudienteId = 2, tareasValidadas = setOf(1, 3)),
        Estudiante(id = 4, nombre = "Mateo Díaz", avatar = "🐸", cursoId = 1, acudienteId = 3)
    )

    /** Las fechas se calculan desde hoy para que siempre haya tareas pendientes y vencidas. */
    fun tareas(hoy: LocalDate) = listOf(
        Tarea(id = 1, cursoId = 1, texto = "Repasar las tablas de multiplicar del 2 al 5", fechaLimite = hoy.minusDays(2)),
        Tarea(id = 2, cursoId = 1, texto = "Escribir 5 palabras que tengan la letra ñ", fechaLimite = hoy),
        Tarea(id = 3, cursoId = 1, texto = "Leer el cuento de la página 20 y dibujar el final", fechaLimite = hoy.plusDays(1)),
        Tarea(id = 4, cursoId = 1, texto = "Traer una hoja seca para la clase de ciencias", fechaLimite = hoy.plusDays(4)),
        Tarea(id = 5, cursoId = 2, texto = "Resolver los ejercicios de fracciones de la página 34", fechaLimite = hoy.plusDays(2)),
        Tarea(id = 6, cursoId = 2, texto = "Preguntar en casa qué cultivos hay en la vereda", fechaLimite = hoy.plusDays(6))
    )
}
