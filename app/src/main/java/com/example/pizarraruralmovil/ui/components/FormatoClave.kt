package com.example.pizarraruralmovil.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Muestra la clave familiar en dos grupos mientras se escribe: "MESA3482" se ve "MESA-3482".
 * Solo cambia cómo se ve; el valor guardado en el estado sigue sin guion.
 */
object FormatoClave : VisualTransformation {

    private const val GRUPO = 4

    override fun filter(text: AnnotatedString): TransformedText {
        val original = text.text
        val conGuion = if (original.length > GRUPO) original.take(GRUPO) + "-" + original.drop(GRUPO) else original
        val conGuionVisible = conGuion.length != original.length

        val posiciones = object : OffsetMapping {
            // Después de la cuarta letra hay que contar el guion
            override fun originalToTransformed(offset: Int): Int =
                if (conGuionVisible && offset > GRUPO) offset + 1 else offset

            override fun transformedToOriginal(offset: Int): Int =
                if (conGuionVisible && offset > GRUPO) offset - 1 else offset
        }
        return TransformedText(AnnotatedString(conGuion), posiciones)
    }
}
