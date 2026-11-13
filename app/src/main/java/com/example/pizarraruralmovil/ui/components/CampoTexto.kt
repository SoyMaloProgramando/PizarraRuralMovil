package com.example.pizarraruralmovil.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.ui.theme.BlancoTarjeta
import com.example.pizarraruralmovil.ui.theme.BordeCampo
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.RosaAccion

/**
 * Campo de texto SIN estado propio (elevación de estados): recibe el [valor] y avisa cada cambio
 * con [onValorCambia]. Quien lo usa decide qué guardar. La etiqueta va visible arriba (RNF-5).
 * [formato] cambia solo cómo se ve lo escrito (por ejemplo, la clave familiar con guion).
 * [tecladoPrivado] pide un teclado sin sugerencias, para que no aprenda claves.
 * [mensajeError] se muestra en rojo con un ícono de aviso debajo del campo.
 * [onPierdeFoco] avisa cuando la persona sale del campo, para revisar lo escrito sin interrumpirla mientras escribe.
 */
@Composable
fun CampoTexto(
    etiqueta: String,
    valor: String,
    onValorCambia: (String) -> Unit,
    modifier: Modifier = Modifier,
    textoAyuda: String? = null,
    mensajeError: String? = null,
    contador: String? = null,
    esContrasena: Boolean = false,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    mayusculas: Boolean = false,
    unaLinea: Boolean = true,
    formato: VisualTransformation = VisualTransformation.None,
    tecladoPrivado: Boolean = false,
    onPierdeFoco: (() -> Unit)? = null
) {
    val colorMensaje = if (mensajeError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
    // Solo importa a este campo saber si tuvo el foco: no se eleva
    var teniaFoco by remember { mutableStateOf(false) }

    // Column = flex vertical: etiqueta, campo y mensaje uno debajo del otro
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = etiqueta, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onBackground)
        OutlinedTextField(
            value = valor,
            onValueChange = onValorCambia,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { foco ->
                    if (teniaFoco && !foco.isFocused) onPierdeFoco?.invoke()
                    teniaFoco = foco.isFocused
                },
            singleLine = unaLinea,
            minLines = if (unaLinea) 1 else 3,
            isError = mensajeError != null,
            textStyle = MaterialTheme.typography.bodyLarge,
            visualTransformation = if (esContrasena) PasswordVisualTransformation() else formato,
            keyboardOptions = KeyboardOptions(
                capitalization = when {
                    mayusculas -> KeyboardCapitalization.Characters
                    esContrasena || tipoTeclado == KeyboardType.Email -> KeyboardCapitalization.None
                    else -> KeyboardCapitalization.Sentences
                },
                autoCorrectEnabled = if (esContrasena || tecladoPrivado) false else null,
                keyboardType = if (esContrasena || tecladoPrivado) KeyboardType.Password else tipoTeclado
            ),
            shape = MaterialTheme.shapes.small,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BlancoTarjeta,
                unfocusedContainerColor = BlancoTarjeta,
                errorContainerColor = BlancoTarjeta,
                unfocusedBorderColor = BordeCampo,
                focusedBorderColor = RosaAccion,
                cursorColor = RosaAccion
            )
        )
        // Row = flex horizontal: mensaje a la izquierda (crece con weight) y contador a la derecha
        val mensaje = mensajeError ?: textoAyuda
        if (mensaje != null || contador != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Aviso: ícono además del color rojo, para no depender solo del color (RNF-16)
                if (mensajeError != null) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = null,
                        tint = colorMensaje,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = mensaje.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = colorMensaje,
                    modifier = Modifier.weight(1f)
                )
                if (contador != null) {
                    Text(text = contador, style = MaterialTheme.typography.bodySmall, color = colorMensaje)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CampoTextoPreview() {
    PizarraRuralMovilTheme {
        CampoTexto(
            etiqueta = "Nombre del curso",
            valor = "Sede Centro",
            onValorCambia = {},
            textoAyuda = "Por ejemplo: Sede Centro, jornada mañana",
            contador = "11/40"
        )
    }
}
