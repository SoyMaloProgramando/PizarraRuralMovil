package com.example.pizarraruralmovil.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.ui.theme.BlancoTarjeta
import com.example.pizarraruralmovil.ui.theme.BordeBotonEncabezado
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.SubtituloEncabezado

/**
 * Botón cuadrado blanco del encabezado amarillo (volver, cerrar sesión), de 48 dp para tocarlo fácil.
 * Sin [onClick] es solo un ícono que adorna el título; entonces [descripcion] va en null.
 */
@Composable
fun BotonCuadrado(
    icono: Painter,
    descripcion: String?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val forma = MaterialTheme.shapes.small
    val borde = BorderStroke(1.dp, BordeBotonEncabezado)
    val color = BlancoTarjeta.copy(alpha = 0.92f)
    val dibujo: @Composable () -> Unit = {
        Box(contentAlignment = Alignment.Center) {
            Icon(painter = icono, contentDescription = descripcion, tint = SubtituloEncabezado, modifier = Modifier.size(24.dp))
        }
    }
    if (onClick != null) {
        Surface(onClick = onClick, modifier = modifier.size(48.dp), shape = forma, color = color, border = borde, content = dibujo)
    } else {
        Surface(modifier = modifier.size(48.dp), shape = forma, color = color, border = borde, content = dibujo)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFCA28)
@Composable
fun BotonCuadradoPreview() {
    PizarraRuralMovilTheme {
        BotonCuadrado(icono = rememberVectorPainter(Icons.AutoMirrored.Filled.ExitToApp), descripcion = "Cerrar sesión", onClick = {})
    }
}
