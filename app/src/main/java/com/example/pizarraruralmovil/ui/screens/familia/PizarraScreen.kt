package com.example.pizarraruralmovil.ui.screens.familia

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.data.DatosDePrueba
import com.example.pizarraruralmovil.logic.FechasLegibles
import com.example.pizarraruralmovil.logic.ReglasTareas
import com.example.pizarraruralmovil.model.Estudiante
import com.example.pizarraruralmovil.model.ResumenTareas
import com.example.pizarraruralmovil.model.TareaConEstado
import com.example.pizarraruralmovil.navigation.PestanaFamilia
import com.example.pizarraruralmovil.ui.components.BarraNavegacion
import com.example.pizarraruralmovil.ui.components.BarraSuperior
import com.example.pizarraruralmovil.ui.components.BotonSalir
import com.example.pizarraruralmovil.ui.components.MensajeVacio
import com.example.pizarraruralmovil.ui.components.ResumenPizarra
import com.example.pizarraruralmovil.ui.components.TarjetaTarea
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.fondoPizarra
import java.time.LocalDate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Pizarra del hijo seleccionado (ReqF-13): resumen arriba (ReqF-16) y tarjetas ordenadas.
 * Validar una entrega es responsabilidad del acudiente (RN-22): por eso pide confirmación,
 * para que no baste un toque del niño. Después vibra y ofrece "Deshacer" durante 5 segundos.
 *
 * Desde aquí mismo se puede salir (botón "Salir" arriba, con confirmación), sin pasar por "Mis hijos".
 *
 * Es declarativa: la pantalla solo describe qué mostrar con los datos que recibe (hijo, tareas, resumen).
 * Al validar no se cambia ninguna tarjeta a mano; se avisa al ViewModel, el estado cambia
 * y Compose redibuja la lista con la tarjeta en su nuevo lugar.
 */
@Composable
fun PizarraScreen(
    hijo: Estudiante,
    tituloCurso: String,
    tareas: List<TareaConEstado>,
    resumen: ResumenTareas,
    hoy: LocalDate,
    onCambiarValidacion: (tareaId: Int, validada: Boolean) -> Unit,
    onIrAMisHijos: () -> Unit,
    onSalir: () -> Unit
) {
    val avisos = remember { SnackbarHostState() }
    val alcance = rememberCoroutineScope()
    val vibracion = LocalHapticFeedback.current

    // Tarea que espera la confirmación del acudiente (null = no hay ventana abierta)
    var tareaPorValidarId by rememberSaveable { mutableStateOf<Int?>(null) }
    val primerNombre = hijo.nombre.substringBefore(" ")

    fun cambiarConDeshacer(tareaId: Int, validada: Boolean) {
        onCambiarValidacion(tareaId, validada)
        vibracion.performHapticFeedback(HapticFeedbackType.LongPress)
        alcance.launch {
            avisos.currentSnackbarData?.dismiss()
            val resultado = withTimeoutOrNull(5_000L) {
                avisos.showSnackbar(
                    message = if (validada) "Entrega validada" else "Se quitó la validación",
                    actionLabel = "Deshacer",
                    duration = SnackbarDuration.Indefinite
                )
            }
            if (resultado == SnackbarResult.ActionPerformed) onCambiarValidacion(tareaId, !validada)
        }
    }

    Scaffold(
        modifier = Modifier.fondoPizarra(),
        containerColor = Color.Transparent,
        topBar = {
            BarraSuperior(
                titulo = hijo.nombre,
                subtitulo = tituloCurso,
                avatar = hijo.avatar,
                acciones = { BotonSalir(onSalir = onSalir) }
            )
        },
        bottomBar = {
            BarraNavegacion(
                pestanas = PestanaFamilia.entries,
                pestanaActual = PestanaFamilia.PIZARRA,
                onElegir = { onIrAMisHijos() }
            )
        },
        snackbarHost = { SnackbarHost(hostState = avisos) }
    ) { relleno ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ResumenPizarra(pendientes = resumen.pendientes, validadas = resumen.validadas, vencidas = resumen.vencidas)
            }
            if (tareas.isEmpty()) {
                item {
                    MensajeVacio(mensaje = "Aún no hay tareas. Cuando el docente publique una, aparecerá aquí.")
                }
            } else {
                items(tareas, key = { it.tarea.id }) { tareaConEstado ->
                    val tarea = tareaConEstado.tarea
                    TarjetaTarea(
                        texto = tarea.texto,
                        vencimiento = FechasLegibles.vencimiento(tarea.fechaLimite, hoy),
                        estado = tareaConEstado.estado,
                        onValidar = { tareaPorValidarId = tarea.id },
                        onQuitarValidacion = { cambiarConDeshacer(tarea.id, validada = false) },
                        modifier = Modifier.animateItem() // la tarjeta se mueve suave al cambiar de lugar
                    )
                }
            }
        }
    }

    // Confirmación del acudiente antes de validar (RN-22)
    val tareaPorValidar = tareas.find { it.tarea.id == tareaPorValidarId }?.tarea
    if (tareaPorValidar != null) {
        AlertDialog(
            onDismissRequest = { tareaPorValidarId = null },
            title = { Text(text = "¿Validar la entrega?") },
            text = {
                Text(
                    text = "Confirme que usted revisó que $primerNombre hizo esta tarea:\n«${tareaPorValidar.texto}»\n\n" +
                        "El docente verá que la familia la validó."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    tareaPorValidarId = null
                    cambiarConDeshacer(tareaPorValidar.id, validada = true)
                }) { Text(text = "Sí, la revisé") }
            },
            dismissButton = {
                TextButton(onClick = { tareaPorValidarId = null }) { Text(text = "Cancelar") }
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun PizarraScreenPreview() {
    val hoy = LocalDate.of(2026, 11, 10)
    val hijo = DatosDePrueba.estudiantes.first()
    val tareas = ReglasTareas.pizarra(DatosDePrueba.tareas(hoy).filter { it.cursoId == 1 }, hijo.tareasValidadas, hoy)
    PizarraRuralMovilTheme {
        PizarraScreen(
            hijo = hijo,
            tituloCurso = "3° · Sede Centro, jornada mañana",
            tareas = tareas,
            resumen = ReglasTareas.resumen(tareas),
            hoy = hoy,
            onCambiarValidacion = { _, _ -> },
            onIrAMisHijos = {},
            onSalir = {}
        )
    }
}
