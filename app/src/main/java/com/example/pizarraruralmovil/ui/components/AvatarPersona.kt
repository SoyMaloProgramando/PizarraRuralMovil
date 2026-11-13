package com.example.pizarraruralmovil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizarraruralmovil.model.Avatares
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.RosaAccion
import com.example.pizarraruralmovil.ui.theme.TonoColor
import com.example.pizarraruralmovil.ui.theme.TonosAvatar

/**
 * Cuadro redondeado con el dibujo del estudiante ([avatar], un emoji) o, si no tiene, con las iniciales
 * del [nombre] (docentes y acudientes), sobre un color suave con borde, como en el diseño:
 * cada dibujo y cada persona siempre con su color. Si está [seleccionado] lleva un aro rosado.
 */
@Composable
fun AvatarPersona(
    nombre: String,
    modifier: Modifier = Modifier,
    avatar: String? = null,
    tamano: Dp = 48.dp,
    seleccionado: Boolean = false
) {
    val tono = tonoDe(avatar ?: nombre)
    val forma = MaterialTheme.shapes.small
    Box(
        modifier = modifier
            .size(tamano)
            .clip(forma)
            .background(tono.fondo)
            .border(if (seleccionado) 3.dp else 1.dp, if (seleccionado) RosaAccion else tono.borde, forma),
        contentAlignment = Alignment.Center
    ) {
        if (avatar != null) {
            Text(text = avatar, style = TextStyle(fontSize = (tamano.value * 0.5f).sp))
        } else {
            Text(
                text = iniciales(nombre),
                style = MaterialTheme.typography.titleSmall.copy(fontSize = (tamano.value * 0.36f).sp, fontWeight = FontWeight.Bold),
                color = tono.texto
            )
        }
    }
}

/** El mismo dibujo (o la misma persona) siempre sale con el mismo tono. */
private fun tonoDe(clave: String): TonoColor {
    val posicion = Avatares.lista.indexOf(clave).takeIf { it >= 0 } ?: clave.hashCode().mod(TonosAvatar.size)
    return TonosAvatar[posicion % TonosAvatar.size]
}

/** "María Pérez" → "MP". */
private fun iniciales(nombre: String): String =
    nombre.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }

@Preview(showBackground = true, backgroundColor = 0xFFFDFBF7)
@Composable
fun AvatarPersonaPreview() {
    PizarraRuralMovilTheme {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AvatarPersona(nombre = "Sofía Pérez", avatar = "🐰", tamano = 56.dp)
            AvatarPersona(nombre = "Mateo Díaz", avatar = "🐸", tamano = 56.dp, seleccionado = true)
            AvatarPersona(nombre = "Profe Demo", tamano = 56.dp)
        }
    }
}
