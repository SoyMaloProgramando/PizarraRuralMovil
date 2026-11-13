package com.example.pizarraruralmovil.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.logic.FechasLegibles
import com.example.pizarraruralmovil.ui.theme.BlancoTarjeta
import com.example.pizarraruralmovil.ui.theme.BordeCampo
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.RosaAccion
import com.example.pizarraruralmovil.ui.theme.TextoPrincipal
import java.time.LocalDate

private const val MILLIS_POR_DIA = 86_400_000L

private fun LocalDate.aMillisUtc(): Long = toEpochDay() * MILLIS_POR_DIA
private fun Long.aFecha(): LocalDate = LocalDate.ofEpochDay(floorDiv(MILLIS_POR_DIA))

/**
 * Selector de fecha límite con calendario.
 * La fecha elegida se eleva al padre ([fecha] y [onFechaElegida]); si el calendario está abierto
 * o no es un estado interno, porque solo le importa a este componente.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoFecha(
    etiqueta: String,
    fecha: LocalDate?,
    onFechaElegida: (LocalDate) -> Unit,
    fechaMinima: LocalDate,
    fechaMaxima: LocalDate,
    modifier: Modifier = Modifier
) {
    var mostrarCalendario by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = etiqueta, style = MaterialTheme.typography.labelMedium)
        OutlinedButton(
            onClick = { mostrarCalendario = true },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, BordeCampo),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = BlancoTarjeta, contentColor = TextoPrincipal)
        ) {
            Icon(imageVector = Icons.Filled.DateRange, contentDescription = null, tint = RosaAccion)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = fecha?.let { FechasLegibles.fechaTitulo(it) } ?: "Elegir fecha",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }
    }

    if (mostrarCalendario) {
        val estadoCalendario = rememberDatePickerState(
            // Al editar una tarea vencida, el calendario abre en hoy: una fecha pasada ya no se puede elegir
            initialSelectedDateMillis = (fecha?.takeIf { !it.isBefore(fechaMinima) } ?: fechaMinima).aMillisUtc(),
            selectableDates = object : SelectableDates {
                // Solo se puede elegir desde hoy hasta 90 días después (RN-10)
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val dia = utcTimeMillis.aFecha()
                    return !dia.isBefore(fechaMinima) && !dia.isAfter(fechaMaxima)
                }

                override fun isSelectableYear(year: Int): Boolean = year in fechaMinima.year..fechaMaxima.year
            }
        )
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(onClick = {
                    estadoCalendario.selectedDateMillis?.let { onFechaElegida(it.aFecha()) }
                    mostrarCalendario = false
                }) { Text(text = "Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) { Text(text = "Cancelar") }
            }
        ) {
            // Calendario sencillo: sin cambio a modo teclado y con la fecha escrita como en el resto de la app
            DatePicker(
                state = estadoCalendario,
                showModeToggle = false,
                title = {
                    Text(
                        text = etiqueta,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(start = 24.dp, end = 12.dp, top = 16.dp)
                    )
                },
                headline = {
                    val elegida = estadoCalendario.selectedDateMillis?.aFecha()
                    Text(
                        text = elegida?.let { FechasLegibles.fechaTitulo(it) } ?: "Elija un día",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(start = 24.dp, end = 12.dp, bottom = 12.dp)
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CampoFechaPreview() {
    PizarraRuralMovilTheme {
        val hoy = LocalDate.of(2026, 10, 30)
        CampoFecha(
            etiqueta = "Fecha límite",
            fecha = hoy.plusDays(3),
            onFechaElegida = {},
            fechaMinima = hoy,
            fechaMaxima = hoy.plusDays(90)
        )
    }
}
