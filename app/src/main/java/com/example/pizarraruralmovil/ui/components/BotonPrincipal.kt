package com.example.pizarraruralmovil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.ui.theme.BordeSuave
import com.example.pizarraruralmovil.ui.theme.DegradadoAccion
import com.example.pizarraruralmovil.ui.theme.DegradadoValidar
import com.example.pizarraruralmovil.ui.theme.GrisIcono
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.sombraSuave

/**
 * Botón principal del diseño: pastilla con degradado rosado, letra blanca grande y sombra rosada.
 * - [icono] va antes del texto y [iconoFinal] después (por ejemplo la flecha de "Continuar").
 * - [degradado] cambia el color: verde para "Validar entrega".
 * - Con [anchoCompleto] ocupa todo el ancho; si no, solo lo que necesita (dentro de una tarjeta).
 * Deshabilitado se ve gris. El texto blanco se lee bien sobre el rosado y el verde oscuro (contraste ≥ 3:1).
 */
@Composable
fun BotonPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    icono: ImageVector? = null,
    iconoFinal: ImageVector? = null,
    degradado: List<Color> = DegradadoAccion,
    anchoCompleto: Boolean = true,
    altoMinimo: Dp = 56.dp
) {
    val forma = CircleShape
    val sombra = if (habilitado) Modifier.sombraSuave(forma, elevacion = 10.dp, color = degradado.last()) else Modifier
    val ancho = if (anchoCompleto) Modifier.fillMaxWidth() else Modifier
    val colorContenido = if (habilitado) Color.White else GrisIcono
    Button(
        onClick = onClick,
        enabled = habilitado,
        shape = forma,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            contentColor = colorContenido,
            disabledContentColor = colorContenido
        ),
        modifier = modifier
            .then(ancho)
            .heightIn(min = altoMinimo)
            .then(sombra)
    ) {
        Box(
            modifier = Modifier
                .then(ancho)
                .heightIn(min = altoMinimo)
                .background(if (habilitado) Brush.horizontalGradient(degradado) else SolidColor(BordeSuave), forma)
                .padding(horizontal = 24.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (icono != null) Icon(imageVector = icono, contentDescription = null, modifier = Modifier.size(22.dp))
                Text(text = texto, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                if (iconoFinal != null) Icon(imageVector = iconoFinal, contentDescription = null, modifier = Modifier.size(22.dp))
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFDFBF7)
@Composable
fun BotonPrincipalPreview() {
    PizarraRuralMovilTheme {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            BotonPrincipal(texto = "Continuar", onClick = {}, iconoFinal = Icons.AutoMirrored.Filled.ArrowForward)
            BotonPrincipal(texto = "Matricular", onClick = {}, icono = Icons.Filled.Add, habilitado = false)
            BotonPrincipal(
                texto = "Validar entrega",
                onClick = {},
                icono = Icons.Filled.Check,
                degradado = DegradadoValidar,
                anchoCompleto = false,
                altoMinimo = 48.dp
            )
        }
    }
}
