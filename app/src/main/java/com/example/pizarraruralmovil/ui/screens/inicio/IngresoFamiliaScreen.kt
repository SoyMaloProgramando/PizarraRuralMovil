package com.example.pizarraruralmovil.ui.screens.inicio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.logic.GeneradorClave
import com.example.pizarraruralmovil.logic.Validaciones
import com.example.pizarraruralmovil.ui.components.AvisoInformativo
import com.example.pizarraruralmovil.ui.components.BarraSuperior
import com.example.pizarraruralmovil.ui.components.BotonPrincipal
import com.example.pizarraruralmovil.ui.components.CampoTexto
import com.example.pizarraruralmovil.ui.components.ContenidoPantalla
import com.example.pizarraruralmovil.ui.components.FormatoClave
import com.example.pizarraruralmovil.ui.components.IconoDegradado
import com.example.pizarraruralmovil.ui.components.MensajeTexto
import com.example.pizarraruralmovil.ui.components.TarjetaBase
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.TextoSecundario
import com.example.pizarraruralmovil.ui.theme.TonoFamilia
import com.example.pizarraruralmovil.ui.theme.fondoPizarra

/**
 * Ingreso del padre, madre o acudiente: solo la clave familiar que entregó el colegio (RN-20).
 * No hay registro ni datos que llenar: el colegio ya matriculó a sus hijos. "Solo entrar y ya".
 */
@Composable
fun IngresoFamiliaScreen(
    onEntrar: (clave: String) -> String?,
    onVolver: () -> Unit
) {
    var clave by remember { mutableStateOf("") } // la clave no se guarda en el estado de la actividad
    var mensaje by rememberSaveable { mutableStateOf("") }

    // Aviso inmediato si usa una letra o número que las claves no tienen (RNF-14)
    val errorFormato = if (clave.isNotEmpty() && clave.any { it !in GeneradorClave.ALFABETO }) {
        Validaciones.validarFormatoClave(clave)
    } else {
        null
    }

    Scaffold(
        modifier = Modifier.fondoPizarra(),
        containerColor = Color.Transparent,
        topBar = { BarraSuperior(titulo = "Ingreso de la familia", onVolver = onVolver) }
    ) { relleno ->
        ContenidoPantalla(relleno = relleno) {
            // Row = flex horizontal: ícono de la familia | explicación (weight = flex: 1)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                IconoDegradado(icono = rememberVectorPainter(Icons.Filled.Home), tono = TonoFamilia)
                Text(
                    text = "Escriba la clave familiar que le entregó el colegio. No necesita crear una cuenta.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextoSecundario,
                    modifier = Modifier.weight(1f)
                )
            }
            TarjetaBase {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    CampoTexto(
                        etiqueta = "Clave familiar",
                        valor = clave,
                        onValorCambia = { nuevo ->
                            // Mayúsculas, solo letras y números (el guion lo pone FormatoClave) y máximo 8
                            clave = GeneradorClave.normalizar(nuevo).take(GeneradorClave.LONGITUD)
                            mensaje = ""
                        },
                        textoAyuda = "Son 8 letras y números. Sirve para todos sus hijos.",
                        mensajeError = errorFormato,
                        mayusculas = true,
                        formato = FormatoClave,
                        tecladoPrivado = true
                    )
                    BotonPrincipal(
                        texto = "Entrar",
                        onClick = { mensaje = onEntrar(clave).orEmpty() },
                        habilitado = clave.length == GeneradorClave.LONGITUD && errorFormato == null
                    )
                    MensajeTexto(mensaje = mensaje)
                }
            }
            AvisoInformativo(
                texto = "¿No tiene clave o la perdió? Pídala en la secretaría del colegio. Por seguridad, la entregan en persona.",
                tono = TonoFamilia
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun IngresoFamiliaScreenPreview() {
    PizarraRuralMovilTheme {
        IngresoFamiliaScreen(onEntrar = { null }, onVolver = {})
    }
}
