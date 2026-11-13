package com.example.pizarraruralmovil.logic

/** Respuesta de una operación de negocio: salió bien (con un valor) o falló (con un mensaje para el usuario). */
sealed class Resultado<out T> {
    data class Exito<out T>(val valor: T) : Resultado<T>()
    data class Error(val mensaje: String) : Resultado<Nothing>()
}

/**
 * Si el resultado es un éxito ejecuta [accion] y devuelve null.
 * Si es un error devuelve el mensaje para mostrarlo en la pantalla.
 */
inline fun <T> Resultado<T>.alExito(accion: (T) -> Unit): String? = when (this) {
    is Resultado.Exito -> {
        accion(valor)
        null
    }
    is Resultado.Error -> mensaje
}
