package com.example.pizarraruralmovil.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.ui.theme.BlancoTarjeta
import com.example.pizarraruralmovil.ui.theme.BordeSuave
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.sombraSuave

/**
 * Tarjeta blanca del diseño: esquinas de 24 dp, borde muy suave y sombra cálida.
 * Es la base de las tarjetas de personas, cursos, tareas y del resumen de la pizarra.
 * Si recibe [onClick] toda la tarjeta se puede tocar; [colorFondo] y [borde] cambian cuando está elegida.
 */
@Composable
fun TarjetaBase(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    colorFondo: Color = BlancoTarjeta,
    borde: BorderStroke = BorderStroke(1.dp, BordeSuave),
    contenido: @Composable ColumnScope.() -> Unit
) {
    val forma = MaterialTheme.shapes.medium
    val estilo = modifier
        .fillMaxWidth()
        .sombraSuave(forma)
    if (onClick != null) {
        Surface(onClick = onClick, modifier = estilo, shape = forma, color = colorFondo, border = borde) {
            Column(content = contenido)
        }
    } else {
        Surface(modifier = estilo, shape = forma, color = colorFondo, border = borde) {
            Column(content = contenido)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFDFBF7)
@Composable
fun TarjetaBasePreview() {
    PizarraRuralMovilTheme {
        TarjetaBase(modifier = Modifier.padding(16.dp)) {
            Text(text = "Tarjeta blanca", modifier = Modifier.padding(16.dp))
        }
    }
}
