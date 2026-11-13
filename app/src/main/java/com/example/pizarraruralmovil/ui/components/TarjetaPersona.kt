package com.example.pizarraruralmovil.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.ui.theme.BlancoTarjeta
import com.example.pizarraruralmovil.ui.theme.BordeSuave
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.RosaAccion
import com.example.pizarraruralmovil.ui.theme.TextoPrincipal
import com.example.pizarraruralmovil.ui.theme.TextoSecundario
import com.example.pizarraruralmovil.ui.theme.TextoTitulo
import com.example.pizarraruralmovil.ui.theme.TonoRosado

/**
 * Tarjeta de una persona: dibujo (o iniciales), nombre y un detalle, como en la lista de estudiantes del diseño.
 * Sirve para los hijos en "Mis hijos", los estudiantes y docentes del colegio y la lista del curso.
 * Si el detalle trae "Etiqueta: valor", la etiqueta va en gris y el valor resaltado ("Acudiente: María Pérez").
 * Si recibe [onClick] se puede tocar y muestra la flecha; si está [seleccionado] lleva borde rosado y dice "Viendo".
 */
@Composable
fun TarjetaPersona(
    nombre: String,
    detalle: String,
    modifier: Modifier = Modifier,
    avatar: String? = null,
    seleccionado: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    TarjetaBase(
        modifier = modifier,
        onClick = onClick,
        colorFondo = if (seleccionado) TonoRosado.fondo else BlancoTarjeta,
        borde = if (seleccionado) BorderStroke(2.dp, RosaAccion) else BorderStroke(1.dp, BordeSuave)
    ) {
        // Row = flex horizontal: dibujo | textos (weight = flex-grow) | "Viendo" o flecha
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AvatarPersona(nombre = nombre, avatar = avatar, tamano = 56.dp)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = nombre, style = MaterialTheme.typography.titleSmall, color = TextoTitulo)
                Text(text = detalleConEtiqueta(detalle), style = MaterialTheme.typography.bodyMedium)
            }
            when {
                seleccionado -> Pildora(texto = "Viendo", tono = TonoRosado)
                onClick != null -> FlechaCirculo()
            }
        }
    }
}

/** "Acudiente: María Pérez" → "Acudiente:" en gris y "María Pérez" en negrita. Sin ":" queda todo en gris. */
private fun detalleConEtiqueta(detalle: String): AnnotatedString = buildAnnotatedString {
    val posicion = detalle.indexOf(": ")
    if (posicion <= 0) {
        withStyle(SpanStyle(color = TextoSecundario)) { append(detalle) }
    } else {
        withStyle(SpanStyle(color = TextoSecundario)) { append(detalle.substring(0, posicion + 1)) }
        withStyle(SpanStyle(color = TextoPrincipal, fontWeight = FontWeight.Bold)) { append(detalle.substring(posicion + 1)) }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFDFBF7)
@Composable
fun TarjetaPersonaPreview() {
    PizarraRuralMovilTheme {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            TarjetaPersona(nombre = "Sofía Pérez", detalle = "Acudiente: María Pérez", avatar = "🐰", onClick = {})
            TarjetaPersona(nombre = "Juan Pérez", detalle = "5° · Sede Centro, jornada mañana", avatar = "🦊", seleccionado = true)
            TarjetaPersona(nombre = "Profe Demo", detalle = "docente@pizarra.co · 2 cursos")
        }
    }
}
