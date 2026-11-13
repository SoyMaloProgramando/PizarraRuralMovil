package com.example.pizarraruralmovil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.RosaAccion
import com.example.pizarraruralmovil.ui.theme.TextoPrincipal
import com.example.pizarraruralmovil.ui.theme.TonoAmarillo

/**
 * Casilla para confirmar algo antes de seguir (aviso de privacidad, documentos revisados...),
 * dentro de un recuadro amarillo suave como en el diseño. Todo el recuadro se puede tocar.
 * Sin estado propio: recibe [marcada] y avisa con [onCambiar].
 */
@Composable
fun CasillaConfirmacion(
    texto: String,
    marcada: Boolean,
    onCambiar: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val forma = MaterialTheme.shapes.small
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clip(forma)
            .background(TonoAmarillo.fondo)
            .border(1.dp, TonoAmarillo.borde, forma)
            .toggleable(value = marcada, onValueChange = onCambiar, role = Role.Checkbox)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Checkbox(
            checked = marcada,
            onCheckedChange = null,
            colors = CheckboxDefaults.colors(checkedColor = RosaAccion, uncheckedColor = TonoAmarillo.texto, checkmarkColor = Color.White)
        )
        Text(text = texto, style = MaterialTheme.typography.bodyMedium, color = TextoPrincipal, modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFDFBF7)
@Composable
fun CasillaConfirmacionPreview() {
    PizarraRuralMovilTheme {
        CasillaConfirmacion(
            texto = "Revisé en persona el documento del estudiante y la identidad del acudiente.",
            marcada = true,
            onCambiar = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
