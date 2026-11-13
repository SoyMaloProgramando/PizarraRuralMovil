package com.example.pizarraruralmovil.ui.screens.colegio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.data.DatosDePrueba
import com.example.pizarraruralmovil.model.Docente
import com.example.pizarraruralmovil.navigation.PestanaColegio
import com.example.pizarraruralmovil.ui.components.AvisoInformativo
import com.example.pizarraruralmovil.ui.components.BarraNavegacion
import com.example.pizarraruralmovil.ui.components.BarraSuperior
import com.example.pizarraruralmovil.ui.components.BotonCuadrado
import com.example.pizarraruralmovil.ui.components.BotonFlotante
import com.example.pizarraruralmovil.ui.components.TarjetaPersona
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.TonoDocente
import com.example.pizarraruralmovil.ui.theme.fondoPizarra

/**
 * Docentes del colegio. Sus cuentas las crea el colegio: nadie se registra por su cuenta (RN-23).
 * Al tocar un docente se le puede generar una contraseña nueva (si la olvidó o si ya no trabaja aquí).
 */
@Composable
fun DocentesScreen(
    docentes: List<Docente>,
    cantidadCursos: (docenteId: Int) -> Int,
    onAgregarDocente: () -> Unit,
    onNuevaContrasena: (docenteId: Int) -> Unit,
    onIrAEstudiantes: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    // Docente al que se le va a cambiar la contraseña (null = no hay ventana abierta)
    var docentePorCambiarId by rememberSaveable { mutableStateOf<Int?>(null) }

    Scaffold(
        modifier = Modifier.fondoPizarra(),
        containerColor = Color.Transparent,
        topBar = {
            BarraSuperior(
                titulo = "Docentes",
                subtitulo = "Pizarra Escolar",
                icono = rememberVectorPainter(Icons.Filled.Person),
                acciones = {
                    BotonCuadrado(
                        icono = rememberVectorPainter(Icons.AutoMirrored.Filled.ExitToApp),
                        descripcion = "Cerrar sesión",
                        onClick = onCerrarSesion
                    )
                }
            )
        },
        bottomBar = {
            BarraNavegacion(
                pestanas = PestanaColegio.entries,
                pestanaActual = PestanaColegio.DOCENTES,
                onElegir = { onIrAEstudiantes() }
            )
        },
        floatingActionButton = { BotonFlotante(texto = "Agregar docente", onClick = onAgregarDocente) }
    ) { relleno ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno),
            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                AvisoInformativo(
                    texto = "El colegio crea la cuenta de cada docente y le entrega su contraseña en persona. Toque un docente si necesita una contraseña nueva.",
                    tono = TonoDocente
                )
            }
            items(docentes, key = { it.id }) { docente ->
                val cursos = cantidadCursos(docente.id)
                TarjetaPersona(
                    nombre = docente.nombre,
                    detalle = "${docente.correo} · ${if (cursos == 1) "1 curso" else "$cursos cursos"}",
                    onClick = { docentePorCambiarId = docente.id }
                )
            }
        }
    }

    // Confirmación antes de cambiar la contraseña, porque la anterior deja de servir
    val docentePorCambiar = docentes.find { it.id == docentePorCambiarId }
    if (docentePorCambiar != null) {
        AlertDialog(
            onDismissRequest = { docentePorCambiarId = null },
            title = { Text(text = "¿Contraseña nueva para ${docentePorCambiar.nombre}?") },
            text = {
                Text(
                    text = "La contraseña actual dejará de funcionar. Úselo si la olvidó, o si ya no trabaja " +
                        "en el colegio: en ese caso no le entregue la nueva y ya no podrá entrar."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    docentePorCambiarId = null
                    onNuevaContrasena(docentePorCambiar.id)
                }) { Text(text = "Sí, generar") }
            },
            dismissButton = {
                TextButton(onClick = { docentePorCambiarId = null }) { Text(text = "Cancelar") }
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun DocentesScreenPreview() {
    PizarraRuralMovilTheme {
        DocentesScreen(
            docentes = DatosDePrueba.docentes,
            cantidadCursos = { 2 },
            onAgregarDocente = {},
            onNuevaContrasena = {},
            onIrAEstudiantes = {},
            onCerrarSesion = {}
        )
    }
}
