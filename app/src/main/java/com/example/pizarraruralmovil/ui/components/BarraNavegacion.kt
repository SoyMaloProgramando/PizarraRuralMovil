package com.example.pizarraruralmovil.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.navigation.Pestana
import com.example.pizarraruralmovil.navigation.PestanaColegio
import com.example.pizarraruralmovil.ui.theme.BlancoTarjeta
import com.example.pizarraruralmovil.ui.theme.BordeSuave
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.RosaIndicador
import com.example.pizarraruralmovil.ui.theme.RosaNavegacion
import com.example.pizarraruralmovil.ui.theme.TextoSecundario

/**
 * Barra inferior blanca con pestañas: la elegida lleva una pastilla rosada y su nombre en rosado.
 * Va en el bottomBar del Scaffold. Es genérica: la misma función sirve para la familia y para el colegio.
 */
@Composable
fun <T : Pestana> BarraNavegacion(
    pestanas: List<T>,
    pestanaActual: T,
    onElegir: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        // Línea fina arriba, para separarla del contenido como en el diseño
        modifier = modifier.drawWithContent {
            drawContent()
            drawLine(BordeSuave, Offset(0f, 0f), Offset(size.width, 0f), strokeWidth = 1.dp.toPx())
        },
        containerColor = BlancoTarjeta,
        tonalElevation = 0.dp
    ) {
        pestanas.forEach { pestana ->
            val elegida = pestana == pestanaActual
            NavigationBarItem(
                selected = elegida,
                onClick = { if (!elegida) onElegir(pestana) },
                icon = { Icon(imageVector = pestana.icono, contentDescription = null) },
                label = { Text(text = pestana.titulo, style = MaterialTheme.typography.labelMedium) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = RosaIndicador,
                    selectedIconColor = RosaNavegacion,
                    selectedTextColor = RosaNavegacion,
                    unselectedIconColor = TextoSecundario,
                    unselectedTextColor = TextoSecundario
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BarraNavegacionPreview() {
    PizarraRuralMovilTheme {
        BarraNavegacion(pestanas = PestanaColegio.entries, pestanaActual = PestanaColegio.ESTUDIANTES, onElegir = {})
    }
}
