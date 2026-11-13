package com.example.pizarraruralmovil.ui.screens.docente

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.pizarraruralmovil.logic.FechasLegibles
import com.example.pizarraruralmovil.logic.Validaciones
import com.example.pizarraruralmovil.ui.components.AvisoInformativo
import com.example.pizarraruralmovil.ui.components.BarraSuperior
import com.example.pizarraruralmovil.ui.components.BotonPrincipal
import com.example.pizarraruralmovil.ui.components.CampoFecha
import com.example.pizarraruralmovil.ui.components.CampoTexto
import com.example.pizarraruralmovil.ui.components.ContenidoPantalla
import com.example.pizarraruralmovil.ui.components.MensajeTexto
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.TonoMenta
import com.example.pizarraruralmovil.ui.theme.fondoPizarra
import java.time.LocalDate

/**
 * Publicar una tarea (ReqF-8): texto corto con contador y fecha límite.
 * La misma pantalla sirve para editar una tarea: llega llena con [textoInicial] y [fechaInicial]
 * y cambian el [titulo], el [textoBoton] y la [nota] (reutilizar en vez de copiar la pantalla).
 * Si la publicación falla, lo escrito se conserva porque el estado vive en esta pantalla.
 */
@Composable
fun NuevaTareaScreen(
    tituloCurso: String,
    hoy: LocalDate,
    onPublicar: (texto: String, fechaLimite: LocalDate?) -> String?,
    onVolver: () -> Unit,
    titulo: String = "Nueva tarea",
    textoBoton: String = "Publicar",
    textoInicial: String = "",
    fechaInicial: LocalDate? = null,
    nota: String? = null
) {
    var texto by rememberSaveable { mutableStateOf(textoInicial) }
    var fechaLimite by rememberSaveable { mutableStateOf(fechaInicial) }
    var mensaje by rememberSaveable { mutableStateOf("") }

    val largo = Validaciones.limpiarTexto(texto).length
    val errorTexto = if (largo > Validaciones.MAX_TEXTO_TAREA) "Máximo ${Validaciones.MAX_TEXTO_TAREA} caracteres" else null
    val fechaElegida = fechaLimite

    Scaffold(
        modifier = Modifier.fondoPizarra(),
        containerColor = Color.Transparent,
        topBar = { BarraSuperior(titulo = titulo, onVolver = onVolver) }) { relleno ->
        ContenidoPantalla(relleno = relleno) {
            Text(
                text = "Curso: $tituloCurso",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            CampoTexto(
                etiqueta = "¿Qué deben hacer?",
                valor = texto,
                onValorCambia = { texto = it; mensaje = "" },
                textoAyuda = "Use frases cortas y sencillas",
                mensajeError = errorTexto,
                contador = "$largo/${Validaciones.MAX_TEXTO_TAREA}",
                unaLinea = false
            )
            CampoFecha(
                etiqueta = "Fecha límite",
                fecha = fechaElegida,
                onFechaElegida = { fechaLimite = it; mensaje = "" },
                fechaMinima = hoy,
                fechaMaxima = hoy.plusDays(Validaciones.DIAS_MAXIMOS_FECHA)
            )
            if (fechaElegida != null) {
                Text(
                    text = "Las familias verán: \"${FechasLegibles.vencimiento(fechaElegida, hoy)}\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (nota != null) AvisoInformativo(texto = nota, tono = TonoMenta)
            BotonPrincipal(
                texto = textoBoton,
                onClick = { mensaje = onPublicar(texto, fechaElegida).orEmpty() },
                habilitado = largo > 0 && errorTexto == null && fechaElegida != null
            )
            MensajeTexto(mensaje = mensaje)
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun NuevaTareaScreenPreview() {
    PizarraRuralMovilTheme {
        NuevaTareaScreen(
            tituloCurso = "3° · Sede Centro, jornada mañana",
            hoy = LocalDate.of(2026, 10, 30),
            onPublicar = { _, _ -> null },
            onVolver = {}
        )
    }
}
