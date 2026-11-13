package com.example.pizarraruralmovil.ui.screens.docente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
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
import com.example.pizarraruralmovil.model.Curso
import com.example.pizarraruralmovil.ui.components.BarraSuperior
import com.example.pizarraruralmovil.ui.components.BotonCuadrado
import com.example.pizarraruralmovil.ui.components.BotonFlotante
import com.example.pizarraruralmovil.ui.components.MensajeVacio
import com.example.pizarraruralmovil.ui.components.TarjetaCurso
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.TextoTitulo
import com.example.pizarraruralmovil.ui.theme.fondoPizarra

/** "Mis cursos" del docente (ReqF-2): lista de cursos con sus estudiantes y tareas, y botón para crear uno nuevo. */
@Composable
fun MisCursosScreen(
    nombreDocente: String,
    cursos: List<Curso>,
    tareasPorCurso: Map<Int, Int>,
    estudiantesPorCurso: Map<Int, Int>,
    onAbrirCurso: (cursoId: Int) -> Unit,
    onCrearCurso: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fondoPizarra(),
        containerColor = Color.Transparent,
        topBar = {
            BarraSuperior(
                titulo = "Mis cursos",
                subtitulo = "Pizarra Escolar",
                icono = rememberVectorPainter(Icons.Filled.Edit),
                acciones = {
                    BotonCuadrado(
                        icono = rememberVectorPainter(Icons.AutoMirrored.Filled.ExitToApp),
                        descripcion = "Cerrar sesión",
                        onClick = onCerrarSesion
                    )
                }
            )
        },
        floatingActionButton = {
            // Sin cursos, el botón ya aparece en el mensaje vacío
            if (cursos.isNotEmpty()) BotonFlotante(texto = "Crear curso", onClick = onCrearCurso)
        }
    ) { relleno ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno),
            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(text = "Hola, $nombreDocente", style = MaterialTheme.typography.titleLarge, color = TextoTitulo)
            }
            if (cursos.isEmpty()) {
                item {
                    MensajeVacio(
                        mensaje = "Aún no tiene cursos. Cree el primero; el colegio matriculará en él a sus estudiantes.",
                        textoBoton = "Crear curso",
                        onClickBoton = onCrearCurso
                    )
                }
            } else {
                item {
                    Text(
                        text = "Toque un curso para ver sus tareas y sus estudiantes.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                items(cursos, key = { it.id }) { curso ->
                    val cantidad = tareasPorCurso[curso.id] ?: 0
                    val estudiantes = estudiantesPorCurso[curso.id] ?: 0
                    TarjetaCurso(
                        grado = curso.grado,
                        nombre = curso.nombre,
                        detalle = "${if (estudiantes == 1) "1 estudiante" else "$estudiantes estudiantes"} · " +
                            if (cantidad == 1) "1 tarea" else "$cantidad tareas",
                        onClick = { onAbrirCurso(curso.id) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun MisCursosScreenPreview() {
    PizarraRuralMovilTheme {
        MisCursosScreen(
            nombreDocente = "Profe Demo",
            cursos = DatosDePrueba.cursos,
            tareasPorCurso = mapOf(1 to 4, 2 to 2),
            estudiantesPorCurso = mapOf(1 to 3, 2 to 1),
            onAbrirCurso = {},
            onCrearCurso = {},
            onCerrarSesion = {}
        )
    }
}
