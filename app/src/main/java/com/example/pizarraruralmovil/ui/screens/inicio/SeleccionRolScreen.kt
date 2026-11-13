package com.example.pizarraruralmovil.ui.screens.inicio

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.R
import com.example.pizarraruralmovil.model.Rol
import com.example.pizarraruralmovil.ui.components.BotonPrincipal
import com.example.pizarraruralmovil.ui.components.CasillaConfirmacion
import com.example.pizarraruralmovil.ui.components.ContenidoPantalla
import com.example.pizarraruralmovil.ui.components.Pildora
import com.example.pizarraruralmovil.ui.components.TarjetaRol
import com.example.pizarraruralmovil.ui.theme.AmarilloEncabezado
import com.example.pizarraruralmovil.ui.theme.BlancoTarjeta
import com.example.pizarraruralmovil.ui.theme.BordeSuave
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.SubtituloEncabezado
import com.example.pizarraruralmovil.ui.theme.TextoEncabezado
import com.example.pizarraruralmovil.ui.theme.TextoPrincipal
import com.example.pizarraruralmovil.ui.theme.TextoSecundario
import com.example.pizarraruralmovil.ui.theme.TextoTitulo
import com.example.pizarraruralmovil.ui.theme.TonoAmarillo
import com.example.pizarraruralmovil.ui.theme.TonoColegio
import com.example.pizarraruralmovil.ui.theme.TonoColor
import com.example.pizarraruralmovil.ui.theme.TonoDocente
import com.example.pizarraruralmovil.ui.theme.TonoFamilia
import com.example.pizarraruralmovil.ui.theme.fondoPizarra
import com.example.pizarraruralmovil.ui.theme.sombraSuave

/** Datos de prueba de un rol para la tarjeta "Accesos rápidos de prueba" (solo en la versión de prueba). */
data class AccesoDePrueba(val rol: Rol, val datos: String)

/**
 * Primera pantalla, con el diseño de Stitch: encabezado de la marca, las tres tarjetas de rol con su color,
 * el aviso de privacidad y "Continuar" (ReqF-4). Ningún rol se registra desde aquí: el colegio crea las
 * cuentas y entrega las claves. En la versión de prueba se muestran los [accesosDePrueba].
 */
@Composable
fun SeleccionRolScreen(
    onContinuar: (Rol) -> Unit,
    accesosDePrueba: List<AccesoDePrueba>? = null
) {
    // Estado de esta pantalla. rememberSaveable lo conserva si la actividad se recrea.
    var rolElegido by rememberSaveable { mutableStateOf<Rol?>(null) }
    var aceptoAviso by rememberSaveable { mutableStateOf(false) }
    var mostrarAyuda by rememberSaveable { mutableStateOf(false) }

    Scaffold(modifier = Modifier.fondoPizarra(), containerColor = Color.Transparent) { relleno ->
        ContenidoPantalla(relleno = relleno) {
            EncabezadoMarca(onAyuda = { mostrarAyuda = true })

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Pildora(texto = "Bienvenida y acceso", tono = TonoAmarillo, emoji = "🎒")
                Text(
                    text = "¿Quién va a usar la app?",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoTitulo,
                    modifier = Modifier.semantics { heading() }
                )
                Text(
                    text = "Elija su rol para entrar y ver sus opciones.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario
                )
            }

            TarjetaRol(
                titulo = "Padre, madre o acudiente",
                descripcion = "Entro con la clave del colegio y valido las tareas de mis hijos",
                icono = rememberVectorPainter(Icons.Filled.Home),
                tono = TonoFamilia,
                seleccionada = rolElegido == Rol.ACUDIENTE,
                onClick = { rolElegido = Rol.ACUDIENTE }
            )
            TarjetaRol(
                titulo = "Soy docente",
                descripcion = "Publico las tareas de mis cursos y veo quién las entregó",
                icono = rememberVectorPainter(Icons.Filled.Edit),
                tono = TonoDocente,
                seleccionada = rolElegido == Rol.DOCENTE,
                onClick = { rolElegido = Rol.DOCENTE }
            )
            TarjetaRol(
                titulo = "Colegio",
                descripcion = "Secretaría o rectoría: matriculo a los estudiantes y entrego las claves",
                icono = painterResource(R.drawable.ic_escuela),
                tono = TonoColegio,
                seleccionada = rolElegido == Rol.COLEGIO,
                onClick = { rolElegido = Rol.COLEGIO }
            )

            // Aviso de privacidad: sin aceptarlo el botón "Continuar" queda deshabilitado (CA-04.2)
            CasillaConfirmacion(
                texto = "Acepto el aviso de privacidad: la app guarda el nombre, el curso y un dibujo de cada estudiante, " +
                    "el nombre de su acudiente y las tareas validadas. No guarda documentos.",
                marcada = aceptoAviso,
                onCambiar = { aceptoAviso = it }
            )

            BotonPrincipal(
                texto = "Continuar",
                onClick = { rolElegido?.let(onContinuar) },
                habilitado = rolElegido != null && aceptoAviso,
                iconoFinal = Icons.AutoMirrored.Filled.ArrowForward
            )

            if (accesosDePrueba != null) TarjetaAccesosDePrueba(accesos = accesosDePrueba)
        }
    }

    if (mostrarAyuda) {
        AlertDialog(
            onDismissRequest = { mostrarAyuda = false },
            title = { Text(text = "¿Cómo funciona?") },
            text = {
                Text(
                    text = "• Colegio: matricula a los estudiantes después de revisar sus documentos y entrega las claves.\n" +
                        "• Docente: publica las tareas de sus cursos.\n" +
                        "• Familia: entra con la clave que le dio el colegio y valida las entregas de sus hijos.\n\n" +
                        "Nadie se registra solo."
                )
            },
            confirmButton = { TextButton(onClick = { mostrarAyuda = false }) { Text(text = "Entendido") } }
        )
    }
}

/** Encabezado de la marca: pastilla blanca con la pizarrita amarilla, el nombre de la app y el botón de ayuda. */
@Composable
private fun EncabezadoMarca(onAyuda: () -> Unit) {
    val forma = CircleShape
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .sombraSuave(forma, elevacion = 4.dp),
        shape = forma,
        color = BlancoTarjeta,
        border = BorderStroke(1.dp, TonoAmarillo.borde)
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .rotate(-3f)
                    .background(AmarilloEncabezado, MaterialTheme.shapes.small),
                contentAlignment = Alignment.Center
            ) {
                Icon(painter = painterResource(R.drawable.ic_pizarra), contentDescription = null, tint = TextoEncabezado, modifier = Modifier.size(24.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Pizarra Escolar", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextoTitulo)
                Text(text = "Comunidad de aprendizaje", style = MaterialTheme.typography.bodySmall, color = SubtituloEncabezado)
            }
            IconButton(onClick = onAyuda) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(TonoAmarillo.fondo, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Filled.Info, contentDescription = "Cómo funciona", tint = SubtituloEncabezado)
                }
            }
        }
    }
}

/** "Accesos rápidos de prueba": los datos para entrar con cada rol, cada uno en el color de su rol. */
@Composable
private fun TarjetaAccesosDePrueba(accesos: List<AccesoDePrueba>) {
    val forma = MaterialTheme.shapes.small
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BlancoTarjeta.copy(alpha = 0.85f), forma)
            .border(1.dp, BordeSuave, forma)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(8.dp).background(TonoDocente.fuerte, CircleShape))
            Text(text = "Accesos rápidos de prueba", style = MaterialTheme.typography.labelMedium, color = TextoPrincipal)
        }
        accesos.forEach { acceso ->
            val (etiqueta, tono) = rotulo(acceso.rol)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(tono.fondo, MaterialTheme.shapes.extraSmall)
                    .border(1.dp, tono.borde, MaterialTheme.shapes.extraSmall)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "$etiqueta:", style = MaterialTheme.typography.labelMedium, color = tono.texto)
                Text(
                    text = acceso.datos,
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    color = TextoPrincipal,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

private fun rotulo(rol: Rol): Pair<String, TonoColor> = when (rol) {
    Rol.COLEGIO -> "Colegio" to TonoColegio
    Rol.DOCENTE -> "Docente" to TonoDocente
    Rol.ACUDIENTE -> "Familia" to TonoFamilia
}

@Preview(showBackground = true, widthDp = 360, heightDp = 1000)
@Composable
fun SeleccionRolScreenPreview() {
    PizarraRuralMovilTheme {
        SeleccionRolScreen(
            onContinuar = {},
            accesosDePrueba = listOf(
                AccesoDePrueba(Rol.COLEGIO, "colegio@pizarra.co · colegio123"),
                AccesoDePrueba(Rol.DOCENTE, "docente@pizarra.co · pizarra123"),
                AccesoDePrueba(Rol.ACUDIENTE, "MESA-3482")
            )
        )
    }
}
