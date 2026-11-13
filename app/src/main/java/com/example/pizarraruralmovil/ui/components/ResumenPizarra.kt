package com.example.pizarraruralmovil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.TonoAmarillo
import com.example.pizarraruralmovil.ui.theme.TonoColor
import com.example.pizarraruralmovil.ui.theme.TonoMenta
import com.example.pizarraruralmovil.ui.theme.TonoRojo

/**
 * Resumen arriba de la pizarra (ReqF-16): cuántas tareas están pendientes y cuántas validó el acudiente.
 * Cada contador va en un recuadro del color de su estado (amarillo pendientes, verde validadas) y las
 * vencidas en una pastilla roja; siempre con su palabra al lado, nunca solo el color (RNF-16).
 */
@Composable
fun ResumenPizarra(
    pendientes: Int,
    validadas: Int,
    vencidas: Int,
    modifier: Modifier = Modifier
) {
    TarjetaBase(modifier = modifier) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Row: dos contadores que se reparten el ancho por igual (weight = flex: 1)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Contador(
                    numero = pendientes,
                    texto = if (pendientes == 1) "pendiente" else "pendientes",
                    tono = TonoAmarillo,
                    modifier = Modifier.weight(1f)
                )
                Contador(
                    numero = validadas,
                    texto = if (validadas == 1) "validada" else "validadas",
                    tono = TonoMenta,
                    modifier = Modifier.weight(1f)
                )
            }
            // Si hay vencidas se avisa debajo, en rojo, con ícono y texto
            if (vencidas > 0) {
                Pildora(
                    texto = if (vencidas == 1) "1 vencida" else "$vencidas vencidas",
                    tono = TonoRojo,
                    icono = rememberVectorPainter(Icons.Filled.Warning)
                )
            }
        }
    }
}

/** Un contador del resumen: el número grande y su palabra, en un recuadro suave del color de su estado. */
@Composable
private fun Contador(numero: Int, texto: String, tono: TonoColor, modifier: Modifier = Modifier) {
    val forma = MaterialTheme.shapes.small
    Column(
        modifier = modifier
            .background(tono.fondo, forma)
            .border(1.dp, tono.borde, forma)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(text = "$numero", style = MaterialTheme.typography.displaySmall, color = tono.texto)
        Text(text = texto, style = MaterialTheme.typography.bodyMedium, color = tono.texto)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFDFBF7)
@Composable
fun ResumenPizarraPreview() {
    PizarraRuralMovilTheme {
        ResumenPizarra(pendientes = 3, validadas = 1, vencidas = 1, modifier = Modifier.padding(16.dp))
    }
}
