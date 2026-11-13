package com.example.pizarraruralmovil.ui.screens.colegio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.data.DatosDePrueba
import com.example.pizarraruralmovil.model.Acudiente
import com.example.pizarraruralmovil.model.Estudiante
import com.example.pizarraruralmovil.ui.components.AvatarPersona
import com.example.pizarraruralmovil.ui.components.AvisoInformativo
import com.example.pizarraruralmovil.ui.components.BarraSuperior
import com.example.pizarraruralmovil.ui.components.BotonPrincipal
import com.example.pizarraruralmovil.ui.components.BotonSecundario
import com.example.pizarraruralmovil.ui.components.ContenidoPantalla
import com.example.pizarraruralmovil.ui.components.Pildora
import com.example.pizarraruralmovil.ui.components.TarjetaBase
import com.example.pizarraruralmovil.ui.components.TarjetaPersona
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.TextoTitulo
import com.example.pizarraruralmovil.ui.theme.fondoPizarra

/**
 * Ficha de un estudiante para el colegio: su curso, su acudiente y los hermanos que usan la misma clave.
 * Desde aquí el colegio genera una clave nueva (RN-20), matricula a un hermano o retira al estudiante.
 */
@Composable
fun FichaEstudianteScreen(
    estudiante: Estudiante,
    tituloCurso: String,
    acudiente: Acudiente,
    hermanos: List<Estudiante>,
    tituloCursoDe: (cursoId: Int) -> String,
    onAbrirEstudiante: (estudianteId: Int) -> Unit,
    onGenerarClave: () -> Unit,
    onMatricularHermano: () -> Unit,
    onRetirar: () -> Unit,
    onVolver: () -> Unit
) {
    var confirmarClave by rememberSaveable { mutableStateOf(false) }
    var confirmarRetiro by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fondoPizarra(),
        containerColor = Color.Transparent,
        topBar = { BarraSuperior(titulo = "Ficha del estudiante", onVolver = onVolver) }) { relleno ->
        ContenidoPantalla(relleno = relleno) {
            // Tarjeta blanca; Row = flex horizontal: dibujo grande | nombre y curso
            TarjetaBase {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AvatarPersona(nombre = estudiante.nombre, avatar = estudiante.avatar, tamano = 72.dp)
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = estudiante.nombre, style = MaterialTheme.typography.titleLarge, color = TextoTitulo)
                        Pildora(texto = tituloCurso)
                    }
                }
            }

            // ---------- Acudiente y clave ----------
            Text(text = "Acudiente", style = MaterialTheme.typography.titleMedium)
            TarjetaPersona(nombre = acudiente.nombre, detalle = "Entra a la app con la clave familiar")
            AvisoInformativo(
                texto = "Por seguridad, la clave no se vuelve a mostrar. Si la familia la perdió o alguien más la conoce, " +
                    "genere una nueva: la anterior deja de funcionar en todos los celulares."
            )
            BotonSecundario(texto = "Generar nueva clave", onClick = { confirmarClave = true })

            // ---------- Hermanos ----------
            if (hermanos.isNotEmpty()) {
                Text(text = "Hermanos con la misma clave", style = MaterialTheme.typography.titleMedium)
                hermanos.forEach { hermano ->
                    TarjetaPersona(
                        nombre = hermano.nombre,
                        detalle = tituloCursoDe(hermano.cursoId),
                        avatar = hermano.avatar,
                        onClick = { onAbrirEstudiante(hermano.id) }
                    )
                }
            }
            BotonPrincipal(texto = "Matricular hermano(a)", onClick = onMatricularHermano, icono = Icons.Filled.Add)

            TextButton(
                onClick = { confirmarRetiro = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .padding(top = 8.dp),
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text(text = "Retirar estudiante")
            }
        }
    }

    // Confirmación antes de cambiar la clave, porque la anterior deja de servir (RNF-14)
    if (confirmarClave) {
        AlertDialog(
            onDismissRequest = { confirmarClave = false },
            title = { Text(text = "¿Generar una clave nueva?") },
            text = {
                Text(text = "La clave actual de ${acudiente.nombre} dejará de funcionar. Tendrá que entregarle la nueva en persona.")
            },
            confirmButton = {
                TextButton(onClick = {
                    confirmarClave = false
                    onGenerarClave()
                }) { Text(text = "Sí, generar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarClave = false }) { Text(text = "Cancelar") }
            }
        )
    }

    if (confirmarRetiro) {
        val avisoClave = if (hermanos.isEmpty()) " La clave de ${acudiente.nombre} también dejará de funcionar." else ""
        AlertDialog(
            onDismissRequest = { confirmarRetiro = false },
            title = { Text(text = "¿Retirar a ${estudiante.nombre}?") },
            text = { Text(text = "Dejará de aparecer en su curso y en la app de su familia.$avisoClave") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarRetiro = false
                    onRetirar()
                }) { Text(text = "Sí, retirar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarRetiro = false }) { Text(text = "Cancelar") }
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun FichaEstudianteScreenPreview() {
    PizarraRuralMovilTheme {
        FichaEstudianteScreen(
            estudiante = DatosDePrueba.estudiantes[0],
            tituloCurso = "3° · Sede Centro, jornada mañana",
            acudiente = DatosDePrueba.acudientes[0],
            hermanos = listOf(DatosDePrueba.estudiantes[1]),
            tituloCursoDe = { "5° · Sede Centro, jornada mañana" },
            onAbrirEstudiante = {},
            onGenerarClave = {},
            onMatricularHermano = {},
            onRetirar = {},
            onVolver = {}
        )
    }
}
