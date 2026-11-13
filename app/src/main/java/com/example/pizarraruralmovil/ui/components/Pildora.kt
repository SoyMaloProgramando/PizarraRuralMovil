package com.example.pizarraruralmovil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.ui.theme.GrisChip
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.TextoSecundario
import com.example.pizarraruralmovil.ui.theme.TonoAmarillo
import com.example.pizarraruralmovil.ui.theme.TonoColor
import com.example.pizarraruralmovil.ui.theme.TonoMenta

/**
 * Pastilla pequeña del diseño ("Jornada mañana", "3 estudiantes", "Bienvenida y acceso").
 * Con [tono] lleva fondo suave, borde y texto de ese color; sin tono es gris.
 * Puede llevar un [icono] o un [emoji] antes del texto.
 */
@Composable
fun Pildora(
    texto: String,
    modifier: Modifier = Modifier,
    tono: TonoColor? = null,
    icono: Painter? = null,
    emoji: String? = null
) {
    val colorTexto = tono?.texto ?: TextoSecundario
    val conBorde = if (tono != null) Modifier.border(1.dp, tono.borde, CircleShape) else Modifier
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(tono?.fondo ?: GrisChip)
            .then(conBorde)
            .padding(horizontal = 12.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (emoji != null) Text(text = emoji, style = MaterialTheme.typography.labelMedium)
        if (icono != null) Icon(painter = icono, contentDescription = null, tint = colorTexto, modifier = Modifier.size(16.dp))
        Text(text = texto, style = MaterialTheme.typography.labelMedium, color = colorTexto)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFDFBF7)
@Composable
fun PildoraPreview() {
    PizarraRuralMovilTheme {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Pildora(texto = "Bienvenida y acceso", tono = TonoAmarillo, emoji = "🎒")
            Pildora(texto = "Jornada mañana", tono = TonoMenta)
            Pildora(texto = "3 estudiantes")
        }
    }
}
