package com.example.pizarraruralmovil.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.TonoAmarillo

/** Estado vacío: tarjeta blanca con un ícono amarillo, un mensaje amable y, si se quiere, la acción siguiente (RNF-11). */
@Composable
fun MensajeVacio(
    mensaje: String,
    modifier: Modifier = Modifier,
    textoBoton: String? = null,
    onClickBoton: (() -> Unit)? = null
) {
    TarjetaBase(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IconoDegradado(icono = rememberVectorPainter(Icons.Filled.Info), tono = TonoAmarillo, tamano = 52.dp)
            Text(
                text = mensaje,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (textoBoton != null && onClickBoton != null) {
                BotonPrincipal(texto = textoBoton, onClick = onClickBoton)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFDFBF7)
@Composable
fun MensajeVacioPreview() {
    PizarraRuralMovilTheme {
        MensajeVacio(
            mensaje = "Aún no hay tareas en este curso.",
            textoBoton = "Nueva tarea",
            onClickBoton = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
