package com.example.pizarraruralmovil.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.ui.theme.BlancoTarjeta
import com.example.pizarraruralmovil.ui.theme.BordeBotonEncabezado
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.SubtituloEncabezado

/**
 * Botón "Salir" de la familia, con ícono y palabra, para el encabezado de la pizarra y de "Mis hijos".
 * Tiene la misma forma que los botones cuadrados del encabezado, pero con la palabra para que se entienda.
 * Pide confirmación para que un toque del niño no cierre la sesión sin querer:
 * para volver a entrar hace falta la clave familiar.
 */
@Composable
fun BotonSalir(onSalir: () -> Unit, modifier: Modifier = Modifier) {
    // Si la ventana de confirmación está abierta solo le importa a este botón: no se eleva
    var preguntar by rememberSaveable { mutableStateOf(false) }

    Surface(
        onClick = { preguntar = true },
        modifier = modifier.heightIn(min = 48.dp),
        shape = MaterialTheme.shapes.small,
        color = BlancoTarjeta.copy(alpha = 0.92f),
        contentColor = SubtituloEncabezado,
        border = BorderStroke(1.dp, BordeBotonEncabezado)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(20.dp))
            Text(text = "Salir", style = MaterialTheme.typography.labelLarge)
        }
    }

    if (preguntar) {
        AlertDialog(
            onDismissRequest = { preguntar = false },
            title = { Text(text = "¿Quiere salir?") },
            text = { Text(text = "Para volver a entrar necesitará la clave familiar que le entregó el colegio.") },
            confirmButton = {
                TextButton(onClick = {
                    preguntar = false
                    onSalir()
                }) { Text(text = "Sí, salir") }
            },
            dismissButton = {
                TextButton(onClick = { preguntar = false }) { Text(text = "Cancelar") }
            }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFCA28)
@Composable
fun BotonSalirPreview() {
    PizarraRuralMovilTheme {
        BotonSalir(onSalir = {})
    }
}
