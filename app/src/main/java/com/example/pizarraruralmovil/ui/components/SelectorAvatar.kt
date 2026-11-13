package com.example.pizarraruralmovil.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.model.Avatares
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme

/**
 * Grilla de dibujos para que el colegio elija el del estudiante al matricularlo: cuadros redondeados
 * de colores suaves, y el elegido con aro rosado.
 * FlowRow funciona como flex-wrap: cuando no caben más en la fila, bajan a la siguiente.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectorAvatar(
    avatares: List<String>,
    seleccionado: String?,
    onSeleccionar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        avatares.forEachIndexed { posicion, avatar ->
            AvatarPersona(
                nombre = "",
                avatar = avatar,
                tamano = 56.dp,
                seleccionado = avatar == seleccionado,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .selectable(
                        selected = avatar == seleccionado,
                        onClick = { onSeleccionar(avatar) },
                        role = Role.RadioButton
                    )
                    .semantics { contentDescription = "Dibujo ${posicion + 1}" }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SelectorAvatarPreview() {
    PizarraRuralMovilTheme {
        SelectorAvatar(avatares = Avatares.lista, seleccionado = "🦊", onSeleccionar = {})
    }
}
