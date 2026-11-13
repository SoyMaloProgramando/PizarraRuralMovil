package com.example.pizarraruralmovil.ui.screens.colegio

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.pizarraruralmovil.logic.Validaciones
import com.example.pizarraruralmovil.ui.components.AvisoInformativo
import com.example.pizarraruralmovil.ui.components.BarraSuperior
import com.example.pizarraruralmovil.ui.components.BotonPrincipal
import com.example.pizarraruralmovil.ui.components.CampoTexto
import com.example.pizarraruralmovil.ui.components.ContenidoPantalla
import com.example.pizarraruralmovil.ui.components.MensajeTexto
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.TonoDocente
import com.example.pizarraruralmovil.ui.theme.fondoPizarra

/**
 * El colegio crea la cuenta de un docente (RN-23). La app genera su contraseña para entregarla en persona.
 * Como en la matrícula, el nombre debe ser completo: el aviso sale debajo del campo.
 */
@Composable
fun AgregarDocenteScreen(
    onCrear: (nombre: String, correo: String) -> String?,
    onVolver: () -> Unit
) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var mensaje by rememberSaveable { mutableStateOf("") }
    var revisarNombre by rememberSaveable { mutableStateOf(false) }
    var revisarCorreo by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fondoPizarra(),
        containerColor = Color.Transparent,
        topBar = { BarraSuperior(titulo = "Agregar docente", onVolver = onVolver) }) { relleno ->
        ContenidoPantalla(relleno = relleno) {
            AvisoInformativo(
                texto = "Al crear la cuenta, la app genera una contraseña para entregársela en persona.",
                tono = TonoDocente
            )
            CampoTexto(
                etiqueta = "Nombre completo del docente",
                valor = nombre,
                onValorCambia = { nombre = it; mensaje = "" },
                textoAyuda = "Nombre y apellido",
                mensajeError = if (revisarNombre) Validaciones.validarNombrePersona(nombre) else null,
                onPierdeFoco = { if (nombre.isNotBlank()) revisarNombre = true }
            )
            CampoTexto(
                etiqueta = "Correo",
                valor = correo,
                onValorCambia = { correo = it; mensaje = "" },
                tipoTeclado = KeyboardType.Email,
                textoAyuda = "Con este correo iniciará sesión",
                mensajeError = if (revisarCorreo) Validaciones.validarCorreo(correo) else null,
                onPierdeFoco = { if (correo.isNotBlank()) revisarCorreo = true }
            )
            BotonPrincipal(
                texto = "Crear cuenta",
                onClick = {
                    revisarNombre = true
                    revisarCorreo = true
                    mensaje = onCrear(nombre, correo).orEmpty()
                },
                habilitado = nombre.isNotBlank() && correo.isNotBlank()
            )
            MensajeTexto(mensaje = mensaje)
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun AgregarDocenteScreenPreview() {
    PizarraRuralMovilTheme {
        AgregarDocenteScreen(onCrear = { _, _ -> null }, onVolver = {})
    }
}
