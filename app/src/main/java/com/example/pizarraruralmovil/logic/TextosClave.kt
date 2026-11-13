package com.example.pizarraruralmovil.logic

import com.example.pizarraruralmovil.model.ClaveParaEntregar
import com.example.pizarraruralmovil.model.TipoClave

/**
 * Textos de la pantalla que muestra una clave recién generada, y el texto que se copia
 * con el botón "Copiar clave y avisos". Están aquí (y no en la pantalla) para probarlos sin interfaz
 * y para que lo que se copia diga exactamente lo mismo que se ve.
 */
object TextosClave {

    /** Indicaciones para quien entrega la clave en el colegio, una por línea. */
    fun indicaciones(clave: ClaveParaEntregar): List<String> =
        if (clave.esFamiliar) {
            listOf(
                "Entréguela en persona o escrita en un papel, solo a ${clave.nombre}.",
                "Con ella la familia entra a la app sin registrarse y ve únicamente a sus hijos.",
                "No la envíe por grupos de WhatsApp ni la publique.",
                "Si la copia, péguela solo en un mensaje privado para ${clave.nombre} o en un papel para imprimir.",
                "Por seguridad no se vuelve a mostrar. Si se pierde, genere una nueva desde la ficha del estudiante."
            )
        } else {
            listOf(
                "Entréguela en persona a ${clave.nombre}. Es solo para este docente, no para las familias.",
                "Con su correo (${clave.detalle}) y esta contraseña entra a la app como docente.",
                "Puede escribirla en mayúsculas o en minúsculas.",
                "Por seguridad no se vuelve a mostrar. Si la olvida, genere otra en la pestaña Docentes."
            )
        }

    /**
     * Texto completo de una clave familiar para pegarlo en un mensaje privado o imprimirlo:
     * acudiente, estudiantes, la clave, cómo entrar y lo que no se debe hacer con ella.
     */
    fun textoParaCopiar(clave: ClaveParaEntregar): String = buildString {
        val nueva = clave.tipo == TipoClave.CLAVE_CAMBIADA
        appendLine(if (nueva) "PIZARRA ESCOLAR · CLAVE FAMILIAR NUEVA" else "PIZARRA ESCOLAR · CLAVE FAMILIAR")
        appendLine("Acudiente: ${clave.nombre}")
        when (clave.estudiantes.size) {
            0 -> Unit
            1 -> appendLine("Estudiante: ${clave.estudiantes.first()}")
            else -> {
                appendLine("Estudiantes:")
                clave.estudiantes.forEach { appendLine("• $it") }
            }
        }
        appendLine("Clave familiar: ${GeneradorClave.formatear(clave.clave)}")
        if (nueva) appendLine("La clave anterior ya no funciona.")
        appendLine()
        appendLine("Cómo entrar: abra Pizarra Escolar, elija «Padre, madre o acudiente» y escriba la clave. No necesita crear una cuenta.")
        appendLine()
        appendLine("Qué no hacer con esta clave:")
        appendLine("• No la comparta: es solo para ${clave.nombre}.")
        appendLine("• No la envíe a grupos de WhatsApp ni la publique en redes sociales.")
        appendLine("• No se la dé al niño: validar las entregas le corresponde a usted.")
        append("Si la pierde, pida una nueva en el colegio. La anterior dejará de funcionar.")
    }
}
