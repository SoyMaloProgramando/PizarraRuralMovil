package com.example.pizarraruralmovil.ui.screens.colegio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.pizarraruralmovil.logic.Validaciones
import com.example.pizarraruralmovil.model.Avatares
import com.example.pizarraruralmovil.model.Curso
import com.example.pizarraruralmovil.ui.components.AvisoInformativo
import com.example.pizarraruralmovil.ui.components.BarraSuperior
import com.example.pizarraruralmovil.ui.components.BotonPrincipal
import com.example.pizarraruralmovil.ui.components.CampoTexto
import com.example.pizarraruralmovil.ui.components.CasillaConfirmacion
import com.example.pizarraruralmovil.ui.components.ContenidoPantalla
import com.example.pizarraruralmovil.ui.components.MensajeTexto
import com.example.pizarraruralmovil.ui.components.MensajeVacio
import com.example.pizarraruralmovil.ui.components.SelectorAvatar
import com.example.pizarraruralmovil.ui.components.SelectorCurso
import com.example.pizarraruralmovil.ui.components.TarjetaPersona
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.TonoColegio
import com.example.pizarraruralmovil.ui.theme.fondoPizarra

/**
 * Matrícula de un estudiante (RN-19). Solo la hace el colegio y solo después de revisar en persona
 * los documentos: sin marcar esa casilla no se puede matricular.
 * Si [acudienteFijo] trae un nombre, es un hermano o hermana: usa la clave que su familia ya tiene.
 * Los nombres deben ser completos (nombre y apellido): si falta el apellido, el aviso sale debajo
 * del campo apenas la persona pasa al siguiente, y también al tocar el botón.
 */
@Composable
fun MatricularScreen(
    cursos: List<Curso>,
    nombreDocente: (docenteId: Int) -> String,
    avatares: List<String>,
    acudienteFijo: String?,
    onMatricular: (nombre: String, cursoId: Int?, avatar: String?, nombreAcudiente: String, verificado: Boolean) -> String?,
    onVolver: () -> Unit
) {
    // Todo con rememberSaveable: si la actividad se recrea, el formulario conserva lo escrito
    var nombre by rememberSaveable { mutableStateOf("") }
    var cursoId by rememberSaveable { mutableStateOf<Int?>(null) }
    var avatar by rememberSaveable { mutableStateOf<String?>(null) }
    var nombreAcudiente by rememberSaveable { mutableStateOf("") }
    var verificado by rememberSaveable { mutableStateOf(false) }
    var mensaje by rememberSaveable { mutableStateOf("") }
    // Se revisa un nombre cuando la persona sale del campo o intenta matricular, no mientras escribe
    var revisarNombre by rememberSaveable { mutableStateOf(false) }
    var revisarAcudiente by rememberSaveable { mutableStateOf(false) }

    val errorNombre = if (revisarNombre) Validaciones.validarNombrePersona(nombre) else null
    val errorAcudiente = if (revisarAcudiente && acudienteFijo == null) Validaciones.validarNombrePersona(nombreAcudiente) else null

    val faltaAcudiente = acudienteFijo == null && nombreAcudiente.isBlank()

    Scaffold(
        modifier = Modifier.fondoPizarra(),
        containerColor = Color.Transparent,
        topBar = {
            BarraSuperior(titulo = if (acudienteFijo == null) "Matricular" else "Matricular hermano(a)", onVolver = onVolver)
        }
    ) { relleno ->
        ContenidoPantalla(relleno = relleno) {
            if (cursos.isEmpty()) {
                MensajeVacio(mensaje = "Aún no hay cursos. Cada docente crea los suyos; después podrá matricular estudiantes en ellos.")
                return@ContenidoPantalla
            }

            AvisoInformativo(
                texto = "Antes de matricular, revise en persona el documento del estudiante y que el acudiente sea quien dice ser."
            )

            // ---------- Estudiante ----------
            Text(text = "Estudiante", style = MaterialTheme.typography.titleMedium)
            CampoTexto(
                etiqueta = "Nombre completo del estudiante",
                valor = nombre,
                onValorCambia = { nombre = it; mensaje = "" },
                textoAyuda = "Nombre y apellido, como aparecen en su documento",
                mensajeError = errorNombre,
                contador = "${nombre.trim().length}/${Validaciones.MAX_NOMBRE_PERSONA}",
                onPierdeFoco = { if (nombre.isNotBlank()) revisarNombre = true }
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Curso", style = MaterialTheme.typography.labelMedium)
                SelectorCurso(
                    cursos = cursos,
                    cursoSeleccionado = cursoId,
                    onSeleccionar = { cursoId = it; mensaje = "" },
                    detalleCurso = { "Docente: ${nombreDocente(it.docenteId)}" }
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Dibujo para que se reconozca en la pizarra", style = MaterialTheme.typography.labelMedium)
                SelectorAvatar(avatares = avatares, seleccionado = avatar, onSeleccionar = { avatar = it; mensaje = "" })
            }

            // ---------- Acudiente ----------
            Text(text = "Acudiente", style = MaterialTheme.typography.titleMedium)
            if (acudienteFijo != null) {
                TarjetaPersona(nombre = acudienteFijo, detalle = "Usará la misma clave familiar que ya tiene")
            } else {
                CampoTexto(
                    etiqueta = "Nombre completo del acudiente",
                    valor = nombreAcudiente,
                    onValorCambia = { nombreAcudiente = it; mensaje = "" },
                    textoAyuda = "Nombre y apellido del padre, la madre o quien responde por el estudiante. " +
                        "Recibirá una clave familiar nueva.",
                    mensajeError = errorAcudiente,
                    contador = "${nombreAcudiente.trim().length}/${Validaciones.MAX_NOMBRE_PERSONA}",
                    onPierdeFoco = { if (nombreAcudiente.isNotBlank()) revisarAcudiente = true }
                )
                AvisoInformativo(
                    texto = "¿Tiene un hermano o hermana ya matriculado? Entonces matricúlelo desde la ficha de ese estudiante, " +
                        "con «Matricular hermano(a)», para que la familia use la misma clave.",
                    tono = TonoColegio
                )
            }

            CasillaConfirmacion(
                texto = "Revisé en persona el documento del estudiante y la identidad del acudiente.",
                marcada = verificado,
                onCambiar = { verificado = it; mensaje = "" }
            )

            BotonPrincipal(
                texto = if (acudienteFijo == null) "Matricular y generar clave" else "Matricular",
                onClick = {
                    revisarNombre = true
                    revisarAcudiente = true
                    mensaje = onMatricular(nombre, cursoId, avatar, nombreAcudiente, verificado).orEmpty()
                },
                habilitado = nombre.isNotBlank() && cursoId != null && avatar != null && !faltaAcudiente && verificado
            )
            MensajeTexto(mensaje = mensaje)
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 1100)
@Composable
fun MatricularScreenPreview() {
    PizarraRuralMovilTheme {
        MatricularScreen(
            cursos = DatosDePrueba.cursos,
            nombreDocente = { "Profe Demo" },
            avatares = Avatares.lista,
            acudienteFijo = null,
            onMatricular = { _, _, _, _, _ -> null },
            onVolver = {}
        )
    }
}
