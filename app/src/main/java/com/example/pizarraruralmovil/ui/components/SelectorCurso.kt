package com.example.pizarraruralmovil.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.data.DatosDePrueba
import com.example.pizarraruralmovil.model.Curso
import com.example.pizarraruralmovil.ui.theme.BlancoTarjeta
import com.example.pizarraruralmovil.ui.theme.BordeMedio
import com.example.pizarraruralmovil.ui.theme.BordeSuave
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.RosaAccion
import com.example.pizarraruralmovil.ui.theme.TextoTitulo
import com.example.pizarraruralmovil.ui.theme.TonoRosado

/**
 * Lista de cursos para elegir uno (al matricular). Column = flex vertical, una opción por fila.
 * El curso elegido lleva borde rosado y fondo rosado suave, como la tarjeta de rol elegida.
 * [detalleCurso] dice qué mostrar debajo de cada curso, por ejemplo el nombre del docente.
 */
@Composable
fun SelectorCurso(
    cursos: List<Curso>,
    cursoSeleccionado: Int?,
    onSeleccionar: (cursoId: Int) -> Unit,
    detalleCurso: (Curso) -> String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        cursos.forEach { curso ->
            val elegido = curso.id == cursoSeleccionado
            val forma = MaterialTheme.shapes.small
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(forma)
                    .selectable(selected = elegido, onClick = { onSeleccionar(curso.id) }, role = Role.RadioButton),
                shape = forma,
                color = if (elegido) TonoRosado.fondo else BlancoTarjeta,
                border = BorderStroke(width = 2.dp, color = if (elegido) RosaAccion else BordeSuave)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RadioButton(
                        selected = elegido,
                        onClick = null,
                        colors = RadioButtonDefaults.colors(selectedColor = RosaAccion, unselectedColor = BordeMedio)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = curso.titulo, style = MaterialTheme.typography.titleSmall, color = TextoTitulo)
                        Text(
                            text = detalleCurso(curso),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFDFBF7)
@Composable
fun SelectorCursoPreview() {
    PizarraRuralMovilTheme {
        SelectorCurso(
            cursos = DatosDePrueba.cursos,
            cursoSeleccionado = 1,
            onSeleccionar = {},
            detalleCurso = { "Docente: Profe Demo" },
            modifier = Modifier.padding(16.dp)
        )
    }
}
