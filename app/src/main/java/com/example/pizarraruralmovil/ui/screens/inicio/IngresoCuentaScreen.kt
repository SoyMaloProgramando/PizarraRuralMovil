package com.example.pizarraruralmovil.ui.screens.inicio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.ui.components.AvisoInformativo
import com.example.pizarraruralmovil.ui.components.BarraSuperior
import com.example.pizarraruralmovil.ui.components.BotonPrincipal
import com.example.pizarraruralmovil.ui.components.CampoTexto
import com.example.pizarraruralmovil.ui.components.ContenidoPantalla
import com.example.pizarraruralmovil.ui.components.IconoDegradado
import com.example.pizarraruralmovil.ui.components.MensajeTexto
import com.example.pizarraruralmovil.ui.components.TarjetaBase
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.TextoSecundario
import com.example.pizarraruralmovil.ui.theme.TonoColor
import com.example.pizarraruralmovil.ui.theme.TonoDocente
import com.example.pizarraruralmovil.ui.theme.fondoPizarra

/**
 * Ingreso con correo y contraseña. La misma pantalla sirve para el colegio y para el docente:
 * solo cambian los textos, el [icono] y el [tono] del rol que recibe (componente reutilizable a nivel de pantalla).
 * No hay botón "Crear cuenta": las cuentas las crea el colegio (RN-23).
 * Igual que en el ejemplo de clase, el formulario guarda lo escrito y le pasa los datos hacia arriba;
 * [onIngresar] devuelve el mensaje de error, o null si todo salió bien.
 */
@Composable
fun IngresoCuentaScreen(
    titulo: String,
    descripcion: String,
    nota: String,
    icono: Painter,
    tono: TonoColor,
    onIngresar: (correo: String, contrasena: String) -> String?,
    onVolver: () -> Unit
) {
    var correo by rememberSaveable { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") } // la contraseña no se guarda en el estado de la actividad
    var mensaje by rememberSaveable { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fondoPizarra(),
        containerColor = Color.Transparent,
        topBar = { BarraSuperior(titulo = titulo, onVolver = onVolver) }
    ) { relleno ->
        ContenidoPantalla(relleno = relleno) {
            // Row = flex horizontal: ícono del rol | descripción (weight = flex: 1)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                IconoDegradado(icono = icono, tono = tono)
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextoSecundario,
                    modifier = Modifier.weight(1f)
                )
            }
            // El formulario en una tarjeta blanca, como en el diseño
            TarjetaBase {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    CampoTexto(
                        etiqueta = "Correo",
                        valor = correo,
                        onValorCambia = { correo = it; mensaje = "" },
                        tipoTeclado = KeyboardType.Email
                    )
                    CampoTexto(
                        etiqueta = "Contraseña",
                        valor = contrasena,
                        onValorCambia = { contrasena = it; mensaje = "" },
                        esContrasena = true
                    )
                    BotonPrincipal(
                        texto = "Iniciar sesión",
                        onClick = { mensaje = onIngresar(correo, contrasena).orEmpty() },
                        habilitado = correo.isNotBlank() && contrasena.isNotEmpty()
                    )
                    MensajeTexto(mensaje = mensaje)
                }
            }
            AvisoInformativo(texto = nota, tono = tono)
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun IngresoCuentaScreenPreview() {
    PizarraRuralMovilTheme {
        IngresoCuentaScreen(
            titulo = "Ingreso del docente",
            descripcion = "Inicie sesión para ver sus cursos y publicar tareas.",
            nota = "¿No tiene cuenta? La crea el colegio: pídala en la secretaría.",
            icono = rememberVectorPainter(Icons.Filled.Edit),
            tono = TonoDocente,
            onIngresar = { _, _ -> "Correo o contraseña incorrectos" },
            onVolver = {}
        )
    }
}
