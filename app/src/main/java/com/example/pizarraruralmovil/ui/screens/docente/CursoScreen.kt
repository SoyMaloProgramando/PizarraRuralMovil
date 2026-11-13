package com.example.pizarraruralmovil.ui.screens.docente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.data.DatosDePrueba
import com.example.pizarraruralmovil.logic.FechasLegibles
import com.example.pizarraruralmovil.logic.ReglasTareas
import com.example.pizarraruralmovil.model.Curso
import com.example.pizarraruralmovil.model.Estudiante
import com.example.pizarraruralmovil.model.Tarea
import com.example.pizarraruralmovil.ui.components.BarraSuperior
import com.example.pizarraruralmovil.ui.components.BotonFlotante
import com.example.pizarraruralmovil.ui.components.MensajeVacio
import com.example.pizarraruralmovil.ui.components.TarjetaPersona
import com.example.pizarraruralmovil.ui.components.TarjetaTarea
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.TextoTitulo
import com.example.pizarraruralmovil.ui.theme.fondoPizarra
import java.time.LocalDate

/**
 * Pantalla de un curso para el docente: tareas por fecha límite (ReqF-9) con cuántas familias
 * validaron cada entrega (RN-22), y la lista de estudiantes que matriculó el colegio (RN-19).
 * Cada tarea se puede editar o eliminar; eliminar pide confirmación porque las familias dejan de verla.
 * Ya no hay código para compartir: nadie entra al curso por su cuenta.
 */
@Composable
fun CursoScreen(
    curso: Curso,
    tareas: List<Tarea>,
    estudiantes: List<Estudiante>,
    validaciones: Map<Int, Int>,
    hoy: LocalDate,
    onNuevaTarea: () -> Unit,
    onEditarTarea: (tareaId: Int) -> Unit,
    onEliminarTarea: (tareaId: Int) -> Unit,
    onVolver: () -> Unit
) {
    val idsTareas = tareas.map { it.id }.toSet()
    // Tarea que espera confirmación para eliminarse (null = no hay ventana abierta)
    var tareaPorEliminarId by rememberSaveable { mutableStateOf<Int?>(null) }

    Scaffold(
        modifier = Modifier.fondoPizarra(),
        containerColor = Color.Transparent,
        topBar = { BarraSuperior(titulo = "Curso de ${curso.grado}°", onVolver = onVolver) },
        floatingActionButton = {
            // Sin tareas, el botón "Nueva tarea" ya aparece en el mensaje vacío (CA-09.2)
            if (tareas.isNotEmpty()) BotonFlotante(texto = "Nueva tarea", onClick = onNuevaTarea)
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
                // El encabezado dice solo el grado; el nombre completo va aquí, donde no se corta con letra grande (RNF-18)
                Text(text = curso.nombre, style = MaterialTheme.typography.titleLarge, color = TextoTitulo)
            }

            // ---------- Tareas ----------
            item {
                Text(text = "Tareas publicadas (${tareas.size})", style = MaterialTheme.typography.titleMedium)
            }
            if (tareas.isEmpty()) {
                item {
                    MensajeVacio(
                        mensaje = "Aún no hay tareas en este curso.",
                        textoBoton = "Nueva tarea",
                        onClickBoton = onNuevaTarea
                    )
                }
            } else {
                items(tareas, key = { it.id }) { tarea ->
                    TarjetaTarea(
                        texto = tarea.texto,
                        vencimiento = FechasLegibles.vencimiento(tarea.fechaLimite, hoy),
                        vencida = ReglasTareas.fechaVencida(tarea.fechaLimite, hoy),
                        validaciones = if (estudiantes.isEmpty()) null
                        else "Entregas validadas: ${validaciones[tarea.id] ?: 0} de ${estudiantes.size}",
                        onEditar = { onEditarTarea(tarea.id) },
                        onEliminar = { tareaPorEliminarId = tarea.id },
                        modifier = Modifier.animateItem() // las demás tarjetas se acomodan suave al eliminar una
                    )
                }
            }

            // ---------- Estudiantes ----------
            item {
                Column(modifier = Modifier.padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Estudiantes (${estudiantes.size})", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "Los matricula el colegio después de revisar sus documentos.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (estudiantes.isEmpty()) {
                item { MensajeVacio(mensaje = "Aún no hay estudiantes matriculados en este curso.") }
            } else {
                items(estudiantes, key = { "estudiante-${it.id}" }) { estudiante ->
                    val validadas = estudiante.tareasValidadas.count { it in idsTareas }
                    TarjetaPersona(
                        nombre = estudiante.nombre,
                        detalle = "Entregas validadas: $validadas de ${tareas.size}",
                        avatar = estudiante.avatar
                    )
                }
            }
        }
    }

    // Confirmación antes de eliminar: las familias dejan de ver la tarea y se borran sus validaciones
    val tareaPorEliminar = tareas.find { it.id == tareaPorEliminarId }
    if (tareaPorEliminar != null) {
        val validadas = validaciones[tareaPorEliminar.id] ?: 0
        val avisoValidaciones = when (validadas) {
            0 -> ""
            1 -> " También se borrará la entrega que ya validó una familia."
            else -> " También se borrarán las $validadas entregas que ya validaron las familias."
        }
        AlertDialog(
            onDismissRequest = { tareaPorEliminarId = null },
            title = { Text(text = "¿Eliminar la tarea?") },
            text = {
                Text(text = "«${tareaPorEliminar.texto}»\n\nLas familias dejarán de verla en la pizarra.$avisoValidaciones")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        tareaPorEliminarId = null
                        onEliminarTarea(tareaPorEliminar.id)
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text(text = "Sí, eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { tareaPorEliminarId = null }) { Text(text = "Cancelar") }
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 900)
@Composable
fun CursoScreenPreview() {
    val hoy = LocalDate.of(2026, 11, 10)
    PizarraRuralMovilTheme {
        CursoScreen(
            curso = DatosDePrueba.cursos.first(),
            tareas = DatosDePrueba.tareas(hoy).filter { it.cursoId == 1 },
            estudiantes = DatosDePrueba.estudiantes.filter { it.cursoId == 1 },
            validaciones = mapOf(1 to 1, 3 to 2),
            hoy = hoy,
            onNuevaTarea = {},
            onEditarTarea = {},
            onEliminarTarea = {},
            onVolver = {}
        )
    }
}
