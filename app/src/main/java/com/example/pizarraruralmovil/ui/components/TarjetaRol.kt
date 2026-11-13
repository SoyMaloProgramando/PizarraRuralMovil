package com.example.pizarraruralmovil.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.ui.theme.BlancoTarjeta
import com.example.pizarraruralmovil.ui.theme.BordeMedio
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.TextoSecundario
import com.example.pizarraruralmovil.ui.theme.TextoTitulo
import com.example.pizarraruralmovil.ui.theme.TonoColor
import com.example.pizarraruralmovil.ui.theme.TonoFamilia
import com.example.pizarraruralmovil.ui.theme.sombraSuave

/**
 * Tarjeta para elegir el rol (colegio, docente o familia) en la primera pantalla (ReqF-4), como en el diseño:
 * tarjeta blanca con el borde del [tono] del rol, su ícono en un cuadro con degradado y un círculo a la derecha.
 * La elegida lleva el borde y el círculo en el color fuerte del rol y una sombra de ese color.
 */
@Composable
fun TarjetaRol(
    titulo: String,
    descripcion: String,
    icono: Painter,
    tono: TonoColor,
    seleccionada: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val forma = MaterialTheme.shapes.medium
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .sombraSuave(forma, elevacion = if (seleccionada) 14.dp else 8.dp, color = if (seleccionada) tono.fuerte else Color(0xFFDCD2BE))
            .clip(forma)
            .selectable(selected = seleccionada, onClick = onClick, role = Role.RadioButton),
        shape = forma,
        color = BlancoTarjeta,
        border = BorderStroke(2.dp, if (seleccionada) tono.fuerte else tono.borde)
    ) {
        // Row = flex horizontal: ícono | textos (weight = flex: 1) | círculo de selección
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            IconoDegradado(icono = icono, tono = tono)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextoTitulo
                )
                Text(text = descripcion, style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
            }
            CirculoSeleccion(seleccionado = seleccionada, color = tono.fuerte)
        }
    }
}

/** Círculo de opción: vacío con borde gris, o lleno del color del rol con un punto blanco. Es decorativo. */
@Composable
private fun CirculoSeleccion(seleccionado: Boolean, color: Color) {
    Box(
        modifier = Modifier
            .size(26.dp)
            .background(if (seleccionado) color else Color.Transparent, CircleShape)
            .border(2.dp, if (seleccionado) color else BordeMedio, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (seleccionado) Box(modifier = Modifier.size(10.dp).background(Color.White, CircleShape))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFDFBF7)
@Composable
fun TarjetaRolPreview() {
    PizarraRuralMovilTheme {
        TarjetaRol(
            titulo = "Padre, madre o acudiente",
            descripcion = "Entro con la clave del colegio y valido las tareas de mis hijos",
            icono = rememberVectorPainter(Icons.Filled.Home),
            tono = TonoFamilia,
            seleccionada = true,
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
