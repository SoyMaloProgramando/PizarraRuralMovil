package com.example.pizarraruralmovil.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.model.EstadoTarea
import com.example.pizarraruralmovil.ui.theme.BlancoTarjeta
import com.example.pizarraruralmovil.ui.theme.BordeMedio
import com.example.pizarraruralmovil.ui.theme.DegradadoValidar
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.TextoPrincipal
import com.example.pizarraruralmovil.ui.theme.TextoSecundario
import com.example.pizarraruralmovil.ui.theme.TextoTitulo
import com.example.pizarraruralmovil.ui.theme.TonoMenta
import com.example.pizarraruralmovil.ui.theme.TonoRojo

/**
 * Tarjeta de tarea con el estilo del diseño: tarjeta blanca de 24 dp con sombra suave,
 * el estado y la fecha en pastillas arriba y el texto de la tarea en letra grande (RNF-5).
 * - En la pizarra de la familia recibe [estado], [onValidar] y [onQuitarValidacion]:
 *   el acudiente valida la entrega (la pantalla pide confirmación antes, RN-22).
 * - En la lista del docente va sin estado y muestra [validaciones]: "Entregas validadas: 2 de 3".
 *   Con [onEditar] y [onEliminar] aparecen los botones para corregir o eliminar la tarea.
 */
@Composable
fun TarjetaTarea(
    texto: String,
    vencimiento: String,
    modifier: Modifier = Modifier,
    estado: EstadoTarea? = null,
    vencida: Boolean = estado == EstadoTarea.VENCIDA,
    validaciones: String? = null,
    onValidar: (() -> Unit)? = null,
    onQuitarValidacion: (() -> Unit)? = null,
    onEditar: (() -> Unit)? = null,
    onEliminar: (() -> Unit)? = null
) {
    val validada = estado == EstadoTarea.VALIDADA
    TarjetaBase(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Fila superior (flex horizontal con flex-wrap): estado a la izquierda y fecha legible a la derecha.
            // Si las dos pastillas no caben juntas, la fecha baja entera a otra línea en vez de partirse.
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                itemVerticalAlignment = Alignment.CenterVertically
            ) {
                if (estado != null) ChipEstado(estado = estado)
                Spacer(modifier = Modifier.weight(1f))
                Pildora(
                    texto = vencimiento,
                    tono = if (vencida) TonoRojo else null,
                    icono = rememberVectorPainter(Icons.Filled.DateRange)
                )
            }

            Text(
                text = texto,
                style = MaterialTheme.typography.bodyLarge, // 18 sp: texto de la tarea
                color = if (validada) TextoSecundario else TextoTitulo
            )

            // Docente: cuántas familias ya validaron la entrega
            if (validaciones != null) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = null, tint = TonoMenta.texto, modifier = Modifier.size(20.dp))
                    Text(text = validaciones, style = MaterialTheme.typography.bodyMedium, color = TextoPrincipal)
                }
            }

            // Docente: eliminar (en rojo, pide confirmación) o editar la tarea
            if (onEditar != null || onEliminar != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onEliminar != null) {
                        TextButton(
                            onClick = onEliminar,
                            shape = CircleShape,
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .semantics { contentDescription = "Eliminar tarea: $texto" }
                        ) {
                            Icon(imageVector = Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Eliminar")
                        }
                    }
                    if (onEditar != null) {
                        OutlinedButton(
                            onClick = onEditar,
                            shape = CircleShape,
                            border = BorderStroke(1.dp, BordeMedio),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = BlancoTarjeta, contentColor = TextoPrincipal),
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .semantics { contentDescription = "Editar tarea: $texto" }
                        ) {
                            Icon(imageVector = Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Editar")
                        }
                    }
                }
            }

            // Acudiente: validar la entrega o quitar la validación
            if (estado != null && (onValidar != null || onQuitarValidacion != null)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    if (validada && onQuitarValidacion != null) {
                        OutlinedButton(
                            onClick = onQuitarValidacion,
                            shape = CircleShape,
                            border = BorderStroke(1.dp, BordeMedio),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = BlancoTarjeta, contentColor = TextoPrincipal),
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .semantics { contentDescription = "Quitar validación: $texto" }
                        ) {
                            Text(text = "Quitar validación")
                        }
                    } else if (!validada && onValidar != null) {
                        // Verde, el mismo color de una tarea validada
                        BotonPrincipal(
                            texto = "Validar entrega",
                            onClick = onValidar,
                            icono = Icons.Filled.Check,
                            degradado = DegradadoValidar,
                            anchoCompleto = false,
                            altoMinimo = 48.dp,
                            modifier = Modifier.semantics { contentDescription = "Validar entrega: $texto" }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFDFBF7)
@Composable
fun TarjetaTareaPreview() {
    PizarraRuralMovilTheme {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            TarjetaTarea(
                texto = "Leer el cuento de la página 20 y dibujar el final",
                vencimiento = "Vence mañana",
                estado = EstadoTarea.PENDIENTE,
                onValidar = {},
                onQuitarValidacion = {}
            )
            TarjetaTarea(
                texto = "Repasar las tablas de multiplicar del 2 al 5",
                vencimiento = "Venció el miércoles 28",
                estado = EstadoTarea.VALIDADA,
                onValidar = {},
                onQuitarValidacion = {}
            )
            TarjetaTarea(
                texto = "Traer una hoja seca",
                vencimiento = "Vence el martes 3",
                validaciones = "Entregas validadas: 2 de 3",
                onEditar = {},
                onEliminar = {}
            )
        }
    }
}
