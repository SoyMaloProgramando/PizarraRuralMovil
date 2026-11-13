package com.example.pizarraruralmovil.logic

import java.security.SecureRandom

/**
 * Genera las claves que entrega el colegio: la clave familiar de cada acudiente (RN-20)
 * y la contraseña de cada docente (RN-23).
 */
object GeneradorClave {

    const val LONGITUD = 8

    /** 31 caracteres: letras mayúsculas sin I, L ni O, y dígitos del 2 al 9 (no se confunden al leerlos). */
    const val ALFABETO = "ABCDEFGHJKMNPQRSTUVWXYZ23456789"

    private val aleatorio = SecureRandom() // generador criptográficamente seguro

    /**
     * Devuelve una clave nueva con letras y números que no esté en [clavesUsadas]
     * (la clave es única en todo el sistema). Hay unos 774 000 millones de claves posibles.
     */
    fun generar(clavesUsadas: Collection<String>): String = generar { it in clavesUsadas }

    /**
     * Igual que la anterior, pero pregunta a [estaEnUso] si la clave ya la tiene alguien.
     * Así el ViewModel puede comparar huellas: la app no guarda las claves, solo su huella.
     */
    fun generar(estaEnUso: (String) -> Boolean): String {
        var clave: String
        do {
            clave = (1..LONGITUD)
                .map { ALFABETO[aleatorio.nextInt(ALFABETO.length)] }
                .joinToString("")
        } while (clave.none { it.isLetter() } || clave.none { it.isDigit() } || estaEnUso(clave))
        return clave
    }

    /** Lo que escribe la familia, sin espacios ni guiones y en mayúsculas: "mesa-3482" queda "MESA3482". */
    fun normalizar(texto: String): String = texto.uppercase().filter { it.isLetterOrDigit() }

    /** Clave en dos grupos para leerla y dictarla mejor: "MESA3482" se muestra "MESA-3482". */
    fun formatear(clave: String): String =
        if (clave.length > 4) "${clave.take(4)}-${clave.drop(4)}" else clave
}
