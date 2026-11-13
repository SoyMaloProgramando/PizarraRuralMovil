package com.example.pizarraruralmovil.ui.screens.docente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.logic.Validaciones
import com.example.pizarraruralmovil.ui.components.BarraSuperior
import com.example.pizarraruralmovil.ui.components.BotonPrincipal
import com.example.pizarraruralmovil.ui.components.CampoTexto
import com.example.pizarraruralmovil.ui.components.ContenidoPantalla
import com.example.pizarraruralmovil.ui.components.MensajeTexto
import com.example.pizarraruralmovil.ui.components.SelectorGrado
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.fondoPizarra

/** Crear un curso con grado y nombre (ReqF-2). Después el colegio matricula en él a los estudiantes (RN-19). */
@Composable
fun CrearCursoScreen(
    onGuardar: (grado: Int?, nombre: String) -> String?,
    onVolver: () -> Unit
) {
    var grado by rememberSaveable { mutableStateOf<Int?>(null) }
    var nombre by rememberSaveable { mutableStateOf("") }
    var mensaje by rememberSaveable { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fondoPizarra(),
        containerColor = Color.Transparent,
        topBar = { BarraSuperior(titulo = "Crear curso", onVolver = onVolver) }) { relleno ->
        ContenidoPantalla(relleno = relleno) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Grado", style = MaterialTheme.typography.labelMedium)
                SelectorGrado(gradoSeleccionado = grado, onSeleccionar = { grado = it; mensaje = "" })
                Text(
                    text = "Si tiene varios grados en el aula, cree un curso por cada grado.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            CampoTexto(
                etiqueta = "Nombre del curso",
                valor = nombre,
                onValorCambia = { nombre = it; mensaje = "" },
                textoAyuda = "Por ejemplo: Sede Centro, jornada mañana",
                contador = "${nombre.trim().length}/${Validaciones.MAX_NOMBRE_CURSO}"
            )
            BotonPrincipal(
                texto = "Guardar",
                onClick = { mensaje = onGuardar(grado, nombre).orEmpty() },
                habilitado = grado != null && nombre.isNotBlank()
            )
            MensajeTexto(mensaje = mensaje)
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun CrearCursoScreenPreview() {
    PizarraRuralMovilTheme {
        CrearCursoScreen(onGuardar = { _, _ -> null }, onVolver = {})
    }
}
