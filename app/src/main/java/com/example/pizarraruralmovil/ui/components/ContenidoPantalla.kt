package com.example.pizarraruralmovil.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme

/**
 * Contenido de las pantallas con formulario, dentro del Scaffold.
 * Column = flex vertical con separación de 16 dp y márgenes de 20 dp, como en el diseño.
 * El fondo crema con puntos lo pone el Scaffold de cada pantalla (fondoPizarra).
 * Se desplaza si no cabe y se acomoda cuando sale el teclado.
 */
@Composable
fun ContenidoPantalla(
    relleno: PaddingValues,
    modifier: Modifier = Modifier,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(relleno)
            .consumeWindowInsets(relleno)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = contenido
    )
}

@Preview(showBackground = true)
@Composable
fun ContenidoPantallaPreview() {
    PizarraRuralMovilTheme {
        ContenidoPantalla(relleno = PaddingValues(0.dp)) {
            Text(text = "Primer elemento")
            Text(text = "Segundo elemento")
        }
    }
}
