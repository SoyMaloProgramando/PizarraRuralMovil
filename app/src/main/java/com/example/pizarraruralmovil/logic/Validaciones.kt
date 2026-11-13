package com.example.pizarraruralmovil.logic

import java.time.LocalDate

/**
 * Reglas de validación de lo que escribe el usuario (reglas de negocio RN del documento).
 * Cada función devuelve null si el dato es válido, o el mensaje que verá el usuario.
 */
object Validaciones {

    const val MAX_TEXTO_TAREA = 120         // RN-10
    const val DIAS_MAXIMOS_FECHA = 90L      // RN-10
    const val MAX_NOMBRE_CURSO = 40         // RN-04
    const val MAX_NOMBRE_PERSONA = 50       // RN-19

    private val FORMATO_CORREO = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
    private val FORMATO_NOMBRE = Regex("^\\p{L}[\\p{L} .'-]*$") // letras con tildes y ñ, espacios, punto, apóstrofo y guion

    // ---------- Cuentas: correo y contraseña (RN-01 y RN-02) ----------

    fun validarCorreo(correo: String): String? {
        val limpio = correo.trim()
        return when {
            limpio.isEmpty() -> "Escriba el correo"
            limpio.length > 100 || !FORMATO_CORREO.matches(limpio) -> "Revise el correo, por ejemplo: nombre@correo.com"
            else -> null
        }
    }

    fun validarContrasena(contrasena: String): String? = when {
        contrasena.length < 8 -> "La contraseña necesita mínimo 8 caracteres"
        contrasena.length > 64 -> "La contraseña puede tener máximo 64 caracteres"
        contrasena.none { it.isLetter() } -> "La contraseña necesita al menos una letra"
        contrasena.none { it.isDigit() } -> "La contraseña necesita al menos un número"
        else -> null
    }

    // ---------- Personas: estudiante, acudiente y docente (RN-19) ----------

    /** Aviso cuando falta el apellido: se ve debajo del campo y también al intentar guardar. */
    const val MENSAJE_NOMBRE_COMPLETO = "Escriba el nombre completo, con nombre y apellido. Por ejemplo: María Pérez"

    /**
     * Nombre completo (nombre y apellido), para que el colegio y el docente no confundan a dos personas.
     * Un solo nombre, como "María", no basta.
     */
    fun validarNombrePersona(nombre: String): String? {
        val limpio = limpiarTexto(nombre)
        return when {
            limpio.isEmpty() -> MENSAJE_NOMBRE_COMPLETO
            limpio.length > MAX_NOMBRE_PERSONA -> "Máximo $MAX_NOMBRE_PERSONA caracteres"
            !FORMATO_NOMBRE.matches(limpio) -> "Use solo letras y espacios"
            limpio.split(" ").count { palabra -> palabra.count { it.isLetter() } >= 2 } < 2 -> MENSAJE_NOMBRE_COMPLETO
            else -> null
        }
    }

    // ---------- Curso (RN-04) ----------

    fun validarGrado(grado: Int?): String? =
        if (grado == null || grado !in 1..5) "Elija el grado (de 1° a 5°)" else null

    fun validarNombreCurso(nombre: String): String? =
        if (nombre.trim().length !in 3..MAX_NOMBRE_CURSO) "El nombre del curso debe tener entre 3 y $MAX_NOMBRE_CURSO caracteres" else null

    // ---------- Clave familiar (RN-20) ----------

    /** Recibe la clave ya normalizada con [GeneradorClave.normalizar]. */
    fun validarFormatoClave(clave: String): String? = when {
        // Primero los caracteres: así el aviso sale apenas se escribe uno que las claves no tienen
        clave.any { it !in GeneradorClave.ALFABETO } -> "La clave no usa I, L, O, Ñ ni tildes, ni los números 0 y 1"
        clave.length != GeneradorClave.LONGITUD -> "La clave tiene ${GeneradorClave.LONGITUD} letras y números"
        else -> null
    }

    // ---------- Tarea (RN-10) ----------

    /**
     * Deja el texto plano en una línea: los saltos de línea y otros caracteres de control
     * se cambian por un espacio (para no pegar palabras) y se quitan los espacios sobrantes.
     */
    fun limpiarTexto(texto: String): String =
        texto.replace(Regex("\\p{Cntrl}+"), " ").replace(Regex(" {2,}"), " ").trim()

    fun validarTextoTarea(texto: String): String? {
        val limpio = limpiarTexto(texto)
        return when {
            limpio.isEmpty() -> "Escriba qué deben hacer"
            limpio.length > MAX_TEXTO_TAREA -> "Máximo $MAX_TEXTO_TAREA caracteres"
            else -> null
        }
    }

    fun validarFechaLimite(fecha: LocalDate?, hoy: LocalDate): String? = when {
        fecha == null -> "Elija la fecha límite"
        fecha.isBefore(hoy) -> "La fecha límite no puede ser anterior a hoy"
        fecha.isAfter(hoy.plusDays(DIAS_MAXIMOS_FECHA)) -> "La fecha límite puede ser hasta $DIAS_MAXIMOS_FECHA días después de hoy"
        else -> null
    }
}
