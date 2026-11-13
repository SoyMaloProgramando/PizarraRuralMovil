package com.example.pizarraruralmovil.ui.screens.familia

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.data.DatosDePrueba
import com.example.pizarraruralmovil.model.Estudiante
import com.example.pizarraruralmovil.navigation.PestanaFamilia
import com.example.pizarraruralmovil.ui.components.AvisoInformativo
import com.example.pizarraruralmovil.ui.components.BarraNavegacion
import com.example.pizarraruralmovil.ui.components.BarraSuperior
import com.example.pizarraruralmovil.ui.components.BotonSalir
import com.example.pizarraruralmovil.ui.components.TarjetaPersona
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.TextoTitulo
import com.example.pizarraruralmovil.ui.theme.TonoFamilia
import com.example.pizarraruralmovil.ui.theme.fondoPizarra

/**
 * "Mis hijos" (ReqF-6): los estudiantes que el colegio matriculó con esta clave familiar.
 * La familia no agrega hijos ni ve a otros estudiantes del curso (RN-19 y RN-21).
 */
@Composable
fun MisHijosScreen(
    nombreAcudiente: String,
    hijos: List<Estudiante>,
    hijoSeleccionadoId: Int?,
    tituloCurso: (cursoId: Int) -> String,
    onSeleccionarHijo: (hijoId: Int) -> Unit,
    onIrAPizarra: () -> Unit,
    onSalir: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fondoPizarra(),
        containerColor = Color.Transparent,
        topBar = {
            BarraSuperior(
                titulo = "Mis hijos",
                subtitulo = "Pizarra Escolar",
                icono = rememberVectorPainter(Icons.Filled.Face),
                acciones = { BotonSalir(onSalir = onSalir) }
            )
        },
        bottomBar = {
            BarraNavegacion(
                pestanas = PestanaFamilia.entries,
                pestanaActual = PestanaFamilia.MIS_HIJOS,
                onElegir = { onIrAPizarra() }
            )
        }
    ) { relleno ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Hola, $nombreAcudiente", style = MaterialTheme.typography.titleLarge, color = TextoTitulo)
                    Text(
                        text = "Toque a un hijo para ver su pizarra.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            items(hijos, key = { it.id }) { hijo ->
                TarjetaPersona(
                    nombre = hijo.nombre,
                    detalle = tituloCurso(hijo.cursoId),
                    avatar = hijo.avatar,
                    seleccionado = hijo.id == hijoSeleccionadoId,
                    onClick = { onSeleccionarHijo(hijo.id) }
                )
            }
            item {
                AvisoInformativo(
                    texto = "¿Falta alguno de sus hijos? Pídale al colegio que lo matricule con su misma clave familiar.",
                    tono = TonoFamilia,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun MisHijosScreenPreview() {
    PizarraRuralMovilTheme {
        MisHijosScreen(
            nombreAcudiente = "María Pérez",
            hijos = DatosDePrueba.estudiantes.filter { it.acudienteId == 1 },
            hijoSeleccionadoId = 1,
            tituloCurso = { if (it == 1) "3° · Sede Centro, jornada mañana" else "5° · Sede Centro, jornada mañana" },
            onSeleccionarHijo = {},
            onIrAPizarra = {},
            onSalir = {}
        )
    }
}
