package com.example.pizarraruralmovil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.RosaAccion
import com.example.pizarraruralmovil.ui.theme.TonosGrado

/**
 * Selector del grado del curso: solo de 1° a 5° (RN-04). Cada grado es un cuadro redondeado de su color;
 * el elegido lleva un aro rosado grueso, como el dibujo y el curso elegidos (no se marca solo con color).
 */
@Composable
fun SelectorGrado(
    gradoSeleccionado: Int?,
    onSeleccionar: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Row = flex horizontal; cada grado usa weight(1f), así los 5 se reparten el ancho por igual
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (grado in 1..5) {
            val elegido = grado == gradoSeleccionado
            val tono = TonosGrado[grado - 1]
            val forma = MaterialTheme.shapes.small
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
                    .clip(forma)
                    .background(if (elegido) tono.borde else tono.fondo)
                    .border(width = if (elegido) 3.dp else 1.dp, color = if (elegido) RosaAccion else tono.borde, shape = forma)
                    .selectable(selected = elegido, onClick = { onSeleccionar(grado) }, role = Role.RadioButton),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "$grado°", style = MaterialTheme.typography.titleMedium, color = tono.texto)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFDFBF7)
@Composable
fun SelectorGradoPreview() {
    PizarraRuralMovilTheme {
        SelectorGrado(gradoSeleccionado = 3, onSeleccionar = {}, modifier = Modifier.padding(16.dp))
    }
}
