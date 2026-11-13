package com.example.pizarraruralmovil.logic

/**
 * Límite de intentos fallidos al iniciar sesión y al entrar con la clave familiar (RN-06):
 * con 5 fallos dentro de un minuto se bloquea 1 minuto completo. Así nadie puede
 * probar claves una tras otra hasta adivinar la de una familia. Un ingreso correcto no borra
 * los fallos: los fallos se olvidan solos cuando pasa el minuto.
 * [reloj] permite cambiar la hora en las pruebas unitarias.
 */
class LimiteIntentos(
    private val maximo: Int = 5,
    private val ventanaMs: Long = 60_000L,
    private val reloj: () -> Long = { System.currentTimeMillis() }
) {
    private val fallos = mutableListOf<Long>()
    private var bloqueadoHasta = 0L

    /** true si en este momento se puede intentar. */
    fun puedeIntentar(): Boolean = reloj() >= bloqueadoHasta

    /** Registra un intento fallido. Al llegar al máximo dentro del minuto, bloquea durante un minuto. */
    fun registrarFallo() {
        val ahora = reloj()
        fallos.removeAll { ahora - it >= ventanaMs }
        fallos.add(ahora)
        if (fallos.size >= maximo) {
            bloqueadoHasta = ahora + ventanaMs
            fallos.clear()
        }
    }
}
