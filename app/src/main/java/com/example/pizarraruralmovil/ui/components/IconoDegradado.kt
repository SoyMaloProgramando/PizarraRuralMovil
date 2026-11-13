package com.example.pizarraruralmovil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.TonoColor
import com.example.pizarraruralmovil.ui.theme.TonoRosado
import com.example.pizarraruralmovil.ui.theme.sombraSuave

/**
 * Ícono blanco en un cuadro redondeado con el degradado de su [tono], como los íconos de rol del diseño.
 * Es decorativo: el texto que va al lado dice lo mismo.
 */
@Composable
fun IconoDegradado(
    icono: Painter,
    tono: TonoColor,
    modifier: Modifier = Modifier,
    tamano: Dp = 56.dp,
    colorIcono: Color = Color.White
) {
    val forma = MaterialTheme.shapes.small
    Box(
        modifier = modifier
            .size(tamano)
            .sombraSuave(forma, elevacion = 6.dp, color = tono.fuerte)
            .background(Brush.linearGradient(tono.degradado), forma),
        contentAlignment = Alignment.Center
    ) {
        Icon(painter = icono, contentDescription = null, tint = colorIcono, modifier = Modifier.size(tamano * 0.5f))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFDFBF7)
@Composable
fun IconoDegradadoPreview() {
    PizarraRuralMovilTheme {
        IconoDegradado(icono = rememberVectorPainter(Icons.Filled.Home), tono = TonoRosado)
    }
}
