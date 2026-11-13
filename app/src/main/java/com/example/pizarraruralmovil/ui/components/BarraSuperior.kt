package com.example.pizarraruralmovil.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizarraruralmovil.R
import com.example.pizarraruralmovil.ui.theme.AmarilloEncabezado
import com.example.pizarraruralmovil.ui.theme.FormaEncabezado
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.SubtituloEncabezado
import com.example.pizarraruralmovil.ui.theme.TextoEncabezado
import com.example.pizarraruralmovil.ui.theme.sombraSuave

/**
 * Encabezado amarillo del diseño (RNF-1), con las esquinas de abajo redondeadas. Va en el topBar del Scaffold.
 * A la izquierda lleva un cuadro blanco: la flecha para volver si recibe [onVolver], el dibujo del niño
 * si recibe [avatar], o el [icono] de la sección. Debajo del [titulo] puede ir un [subtitulo].
 * Las [acciones] (cerrar sesión, salir) van a la derecha. El amarillo llega hasta arriba, detrás de la hora.
 */
@Composable
fun BarraSuperior(
    titulo: String,
    modifier: Modifier = Modifier,
    subtitulo: String? = null,
    avatar: String? = null,
    icono: Painter? = null,
    onVolver: (() -> Unit)? = null,
    acciones: @Composable RowScope.() -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .sombraSuave(FormaEncabezado, elevacion = 6.dp, color = AmarilloEncabezado),
        shape = FormaEncabezado,
        color = AmarilloEncabezado,
        contentColor = TextoEncabezado
    ) {
        // Row = flex horizontal: cuadro izquierdo | títulos (weight = flex: 1) | acciones
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when {
                onVolver != null -> BotonCuadrado(
                    icono = rememberVectorPainter(Icons.AutoMirrored.Filled.ArrowBack),
                    descripcion = "Volver",
                    onClick = onVolver
                )
                avatar != null -> AvatarPersona(nombre = titulo, avatar = avatar, tamano = 48.dp)
                icono != null -> BotonCuadrado(icono = icono, descripcion = null)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp, lineHeight = 30.sp),
                    color = TextoEncabezado,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.semantics { heading() }
                )
                if (subtitulo != null) {
                    // Hasta dos líneas: el curso completo debe leerse aunque el nombre sea largo (RNF-18)
                    Text(
                        text = subtitulo,
                        style = MaterialTheme.typography.bodySmall,
                        color = SubtituloEncabezado,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                content = acciones
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFDFBF7)
@Composable
fun BarraSuperiorPreview() {
    PizarraRuralMovilTheme {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            BarraSuperior(
                titulo = "Estudiantes",
                subtitulo = "Pizarra Escolar",
                icono = painterResource(R.drawable.ic_escuela),
                acciones = {
                    BotonCuadrado(
                        icono = rememberVectorPainter(Icons.AutoMirrored.Filled.ExitToApp),
                        descripcion = "Cerrar sesión",
                        onClick = {}
                    )
                }
            )
            BarraSuperior(titulo = "Sofía Pérez", subtitulo = "3° · Sede Centro, jornada mañana", avatar = "🐰")
            BarraSuperior(titulo = "Matricular", onVolver = {})
        }
    }
}
