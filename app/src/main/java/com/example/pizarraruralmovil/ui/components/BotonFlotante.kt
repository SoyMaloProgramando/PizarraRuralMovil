package com.example.pizarraruralmovil.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme

/**
 * Botón flotante del diseño ("+ Matricular", "+ Nueva tarea"): la misma pastilla rosada en degradado
 * del botón principal, pero del ancho de su texto. Va en el floatingActionButton del Scaffold.
 */
@Composable
fun BotonFlotante(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icono: ImageVector = Icons.Filled.Add
) {
    BotonPrincipal(texto = texto, onClick = onClick, modifier = modifier, icono = icono, anchoCompleto = false)
}

@Preview
@Composable
fun BotonFlotantePreview() {
    PizarraRuralMovilTheme {
        BotonFlotante(texto = "Matricular", onClick = {})
    }
}
