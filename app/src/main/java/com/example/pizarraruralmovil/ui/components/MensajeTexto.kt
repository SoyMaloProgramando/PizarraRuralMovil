package com.example.pizarraruralmovil.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme

/** Mensaje centrado para avisos y errores (como el MessagesText del ejemplo de clase). Si está vacío no muestra nada. */
@Composable
fun MensajeTexto(
    mensaje: String,
    modifier: Modifier = Modifier,
    esError: Boolean = true
) {
    if (mensaje.isBlank()) return
    Text(
        text = mensaje,
        modifier = modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyMedium,
        color = if (esError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Preview(showBackground = true)
@Composable
fun MensajeTextoPreview() {
    PizarraRuralMovilTheme {
        MensajeTexto(mensaje = "Correo o contraseña incorrectos")
    }
}
