package com.example.pizarraruralmovil.ui.screens.colegio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.R
import com.example.pizarraruralmovil.data.DatosDePrueba
import com.example.pizarraruralmovil.model.Curso
import com.example.pizarraruralmovil.model.Estudiante
import com.example.pizarraruralmovil.navigation.PestanaColegio
import com.example.pizarraruralmovil.ui.components.AvisoInformativo
import com.example.pizarraruralmovil.ui.components.BarraNavegacion
import com.example.pizarraruralmovil.ui.components.BarraSuperior
import com.example.pizarraruralmovil.ui.components.BotonCuadrado
import com.example.pizarraruralmovil.ui.components.BotonFlotante
import com.example.pizarraruralmovil.ui.components.MensajeVacio
import com.example.pizarraruralmovil.ui.components.Pildora
import com.example.pizarraruralmovil.ui.components.TarjetaPersona
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.TextoPrincipal
import com.example.pizarraruralmovil.ui.theme.TextoSecundario
import com.example.pizarraruralmovil.ui.theme.TextoTitulo
import com.example.pizarraruralmovil.ui.theme.TonoAmarillo
import com.example.pizarraruralmovil.ui.theme.TonosGrado
import com.example.pizarraruralmovil.ui.theme.fondoPizarra

/**
 * Inicio del colegio, como el diseño de Stitch: la sede, un aviso de la regla de matrícula y los
 * estudiantes matriculados agrupados por curso (RN-19). Solo el colegio ve esta lista completa;
 * las familias nunca la ven (RN-21).
 */
@Composable
fun EstudiantesScreen(
    nombreColegio: String,
    cursos: List<Curso>,
    estudiantesDe: (cursoId: Int) -> List<Estudiante>,
    nombreDocente: (docenteId: Int) -> String,
    nombreAcudiente: (acudienteId: Int) -> String,
    onAbrirEstudiante: (estudianteId: Int) -> Unit,
    onMatricular: () -> Unit,
    onIrADocentes: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fondoPizarra(),
        containerColor = Color.Transparent,
        topBar = {
            BarraSuperior(
                titulo = "Estudiantes",
                subtitulo = "Pizarra Escolar",
                icono = painterResource(R.drawable.ic_escuela),
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
                pestanaActual = PestanaColegio.ESTUDIANTES,
                onElegir = { onIrADocentes() }
            )
        },
        floatingActionButton = {
            if (cursos.isNotEmpty()) BotonFlotante(texto = "Matricular", onClick = onMatricular)
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
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Row = flex horizontal: ícono de la sede | nombre de la sede
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(TonoAmarillo.borde, MaterialTheme.shapes.extraSmall),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Filled.LocationOn, contentDescription = null, tint = TonoAmarillo.texto)
                        }
                        Text(text = nombreColegio, style = MaterialTheme.typography.titleMedium, color = TextoTitulo)
                    }
                    AvisoInformativo(
                        texto = "Solo el colegio matricula estudiantes, después de revisar en persona sus documentos y quién es su acudiente."
                    )
                }
            }
            if (cursos.isEmpty()) {
                item {
                    MensajeVacio(mensaje = "Aún no hay cursos. Cada docente crea los suyos; después podrá matricular estudiantes en ellos.")
                }
            }
            cursos.forEach { curso ->
                val estudiantes = estudiantesDe(curso.id)
                item(key = "curso-${curso.id}") {
                    EncabezadoCurso(
                        curso = curso,
                        docente = nombreDocente(curso.docenteId),
                        cantidad = estudiantes.size,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                if (estudiantes.isEmpty()) {
                    item(key = "vacio-${curso.id}") {
                        Text(
                            text = "Aún no hay estudiantes matriculados en este curso.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextoSecundario,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
                items(estudiantes, key = { "estudiante-${it.id}" }) { estudiante ->
                    TarjetaPersona(
                        nombre = estudiante.nombre,
                        detalle = "Acudiente: ${nombreAcudiente(estudiante.acudienteId)}",
                        avatar = estudiante.avatar,
                        onClick = { onAbrirEstudiante(estudiante.id) }
                    )
                }
            }
        }
    }
}

/**
 * Encabezado de cada curso, como en el diseño: un punto del color del grado, el curso,
 * el docente y una pastilla con cuántos estudiantes tiene.
 */
@Composable
private fun EncabezadoCurso(curso: Curso, docente: String, cantidad: Int, modifier: Modifier = Modifier) {
    val tono = TonosGrado.getOrElse(curso.grado - 1) { TonoAmarillo }
    Column(modifier = modifier.padding(horizontal = 4.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.size(10.dp).background(tono.fuerte, CircleShape))
            Text(text = curso.titulo, style = MaterialTheme.typography.titleSmall, color = TextoTitulo)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = TextoSecundario)) { append("Docente: ") }
                    withStyle(SpanStyle(color = TextoPrincipal, fontWeight = FontWeight.Bold)) { append(docente) }
                },
                style = MaterialTheme.typography.bodyMedium
            )
            Pildora(texto = if (cantidad == 1) "1 estudiante" else "$cantidad estudiantes")
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun EstudiantesScreenPreview() {
    PizarraRuralMovilTheme {
        EstudiantesScreen(
            nombreColegio = "Secretaría · Sede Centro",
            cursos = DatosDePrueba.cursos,
            estudiantesDe = { id -> DatosDePrueba.estudiantes.filter { it.cursoId == id } },
            nombreDocente = { "Profe Demo" },
            nombreAcudiente = { id -> listOf("María Pérez", "Andrés Rojas", "Carmen Díaz").getOrElse(id - 1) { "" } },
            onAbrirEstudiante = {},
            onMatricular = {},
            onIrADocentes = {},
            onCerrarSesion = {}
        )
    }
}
