package com.example.pizarraruralmovil.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.R
import com.example.pizarraruralmovil.model.EstadoTarea
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.TonoAmarillo
import com.example.pizarraruralmovil.ui.theme.TonoMenta
import com.example.pizarraruralmovil.ui.theme.TonoRojo

/**
 * Chip de estado de una tarea (ReqF-15), como las pastillas del diseño:
 * amarillo pendiente, verde validada y rojo vencida.
 * El estado nunca se indica solo con color: siempre lleva ícono + texto + color (RNF-16).
 */
@Composable
fun ChipEstado(estado: EstadoTarea, modifier: Modifier = Modifier) {
    val tono = when (estado) {
        EstadoTarea.PENDIENTE -> TonoAmarillo
        EstadoTarea.VALIDADA -> TonoMenta
        EstadoTarea.VENCIDA -> TonoRojo
    }
    val icono = when (estado) {
        EstadoTarea.PENDIENTE -> painterResource(R.drawable.ic_reloj)
        EstadoTarea.VALIDADA -> rememberVectorPainter(Icons.Filled.Check)
        EstadoTarea.VENCIDA -> rememberVectorPainter(Icons.Filled.Warning)
    }
    Pildora(texto = estado.etiqueta, tono = tono, icono = icono, modifier = modifier)
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun ChipEstadoPreview() {
    PizarraRuralMovilTheme {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EstadoTarea.entries.forEach { ChipEstado(estado = it) }
        }
    }
}
