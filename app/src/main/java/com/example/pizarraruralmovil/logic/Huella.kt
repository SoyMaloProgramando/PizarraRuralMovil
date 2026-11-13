package com.example.pizarraruralmovil.logic

import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Huella (hash) de las claves familiares y de las contraseñas de los docentes.
 *
 * Como ahora los datos se guardan en el celular, la app nunca escribe una clave tal cual:
 * guarda su huella, que sirve para comprobar la clave pero no para leerla. Al entrar se calcula
 * la huella de lo que escribió la persona y se compara con la guardada.
 * PBKDF2 repite el cálculo miles de veces para que adivinar claves a partir del archivo sea muy lento.
 * En el servidor se usará bcrypt (RNF-26).
 */
object Huella {

    private const val ALGORITMO = "PBKDF2WithHmacSHA256"
    private const val ITERACIONES = 10_000
    private const val BITS = 256
    private const val HEXADECIMAL = "0123456789abcdef"

    // Sal fija de la app: la clave familiar se busca por su huella, así que todas usan la misma sal.
    // Las claves son únicas y aleatorias (unos 774 000 millones posibles), no palabras que alguien elige.
    private val SAL = "PizarraEscolar-huella-v1".toByteArray(Charsets.UTF_8)

    /** Huella de una clave familiar ya normalizada con [GeneradorClave.normalizar] ("MESA3482"). */
    fun deClave(claveNormalizada: String): String = calcular(claveNormalizada)

    /**
     * Huella de la contraseña de un docente. Se pasa a mayúsculas antes de calcularla: las contraseñas
     * que genera la app solo tienen mayúsculas y números, así que da igual cómo las escriba el docente.
     */
    fun deContrasena(contrasena: String): String = calcular(contrasena.trim().uppercase())

    private fun calcular(texto: String): String {
        val especificacion = PBEKeySpec(texto.toCharArray(), SAL, ITERACIONES, BITS)
        try {
            val bytes = SecretKeyFactory.getInstance(ALGORITMO).generateSecret(especificacion).encoded
            return buildString(bytes.size * 2) {
                for (byte in bytes) {
                    append(HEXADECIMAL[(byte.toInt() shr 4) and 0xF])
                    append(HEXADECIMAL[byte.toInt() and 0xF])
                }
            }
        } finally {
            especificacion.clearPassword()
        }
    }
}
