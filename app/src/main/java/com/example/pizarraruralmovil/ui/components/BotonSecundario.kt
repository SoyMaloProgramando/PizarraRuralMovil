package com.example.pizarraruralmovil.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.ui.theme.BlancoTarjeta
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.TonoRosado

/** Botón blanco con borde rosado para acciones secundarias ("Generar nueva clave"...). El [icono] es opcional. */
@Composable
fun BotonSecundario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    icono: Painter? = null
) {
    OutlinedButton(
        onClick = onClick,
        enabled = habilitado,
        shape = CircleShape,
        border = BorderStroke(2.dp, TonoRosado.borde),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = BlancoTarjeta, contentColor = TonoRosado.texto),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
    ) {
        if (icono != null) {
            Icon(painter = icono, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text = texto, style = MaterialTheme.typography.titleSmall)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFDFBF7)
@Composable
fun BotonSecundarioPreview() {
    PizarraRuralMovilTheme {
        BotonSecundario(texto = "Generar nueva clave", onClick = {})
    }
}
