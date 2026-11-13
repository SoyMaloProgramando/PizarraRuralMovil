package com.example.pizarraruralmovil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.TextoTitulo
import com.example.pizarraruralmovil.ui.theme.TonoAmarillo
import com.example.pizarraruralmovil.ui.theme.TonosGrado

/**
 * Tarjeta de un curso: el grado en un cuadro del color de ese grado, el nombre y un detalle.
 * Si recibe [onClick] se puede tocar (en "Mis cursos") y muestra la flecha; sin él solo informa.
 */
@Composable
fun TarjetaCurso(
    grado: Int,
    nombre: String,
    detalle: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val tono = TonosGrado.getOrElse(grado - 1) { TonoAmarillo }
    TarjetaBase(modifier = modifier, onClick = onClick) {
        // Row = flex horizontal: grado | textos (weight = flex-grow) | flecha
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            val forma = MaterialTheme.shapes.small
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(tono.fondo, forma)
                    .border(1.dp, tono.borde, forma),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "$grado°", style = MaterialTheme.typography.titleLarge, color = tono.texto)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = nombre, style = MaterialTheme.typography.titleSmall, color = TextoTitulo)
                Text(text = detalle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (onClick != null) FlechaCirculo()
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFDFBF7)
@Composable
fun TarjetaCursoPreview() {
    PizarraRuralMovilTheme {
        TarjetaCurso(
            grado = 3,
            nombre = "Sede Centro, jornada mañana",
            detalle = "3 estudiantes · 4 tareas",
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
