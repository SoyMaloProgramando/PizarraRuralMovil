package com.example.pizarraruralmovil.ui.screens.colegio

import android.content.ClipData
import android.os.PersistableBundle
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizarraruralmovil.R
import com.example.pizarraruralmovil.logic.GeneradorClave
import com.example.pizarraruralmovil.logic.TextosClave
import com.example.pizarraruralmovil.model.ClaveParaEntregar
import com.example.pizarraruralmovil.model.TipoClave
import com.example.pizarraruralmovil.ui.components.BarraSuperior
import com.example.pizarraruralmovil.ui.components.BotonPrincipal
import com.example.pizarraruralmovil.ui.components.BotonSecundario
import com.example.pizarraruralmovil.ui.components.ContenidoPantalla
import com.example.pizarraruralmovil.ui.components.IconoDegradado
import com.example.pizarraruralmovil.ui.components.MensajeTexto
import com.example.pizarraruralmovil.ui.components.TarjetaBase
import com.example.pizarraruralmovil.ui.components.TarjetaClave
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.TextoPrincipal
import com.example.pizarraruralmovil.ui.theme.TextoTitulo
import com.example.pizarraruralmovil.ui.theme.TonoDocente
import com.example.pizarraruralmovil.ui.theme.TonoFamilia
import com.example.pizarraruralmovil.ui.theme.fondoPizarra
import kotlinx.coroutines.launch

/** Marca de Android para que el texto copiado no se vea en la vista previa del portapapeles (Android 13 o más nuevo). */
private const val EXTRA_TEXTO_SENSIBLE = "android.content.extra.IS_SENSITIVE"

/**
 * Muestra una sola vez la clave recién generada para que el colegio la entregue en persona:
 * la clave familiar de un acudiente (RN-20) o la contraseña de un docente (RN-23).
 * - El encabezado grande y el color dicen de quién es: rosado para la familia, menta para el docente.
 * - La clave familiar se puede copiar junto con el acudiente, los estudiantes y los avisos de qué no hacer.
 * Al tocar "Listo" (o volver) la app la olvida. Mientras se ve, Android no deja tomar capturas
 * de pantalla ni la muestra en la lista de apps recientes (FLAG_SECURE).
 */
@Composable
fun ClaveParaEntregarScreen(
    clave: ClaveParaEntregar,
    onListo: () -> Unit
) {
    // El botón "atrás" del celular hace lo mismo que "Listo"
    BackHandler(onBack = onListo)

    // Protege la clave solo mientras esta pantalla está visible
    val actividad = LocalActivity.current
    DisposableEffect(actividad) {
        actividad?.window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        onDispose { actividad?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE) }
    }

    val esFamiliar = clave.esFamiliar
    val portapapeles = LocalClipboard.current
    val alcance = rememberCoroutineScope()
    var copiado by rememberSaveable { mutableStateOf(false) }

    fun copiar() {
        val datos = ClipData.newPlainText("Clave familiar de ${clave.nombre}", TextosClave.textoParaCopiar(clave))
        datos.description.extras = PersistableBundle().apply { putBoolean(EXTRA_TEXTO_SENSIBLE, true) }
        alcance.launch {
            portapapeles.setClipEntry(ClipEntry(datos))
            copiado = true
        }
    }

    Scaffold(
        modifier = Modifier.fondoPizarra(),
        containerColor = Color.Transparent,
        topBar = {
            BarraSuperior(titulo = if (esFamiliar) "Clave familiar" else "Contraseña del docente", onVolver = onListo)
        }
    ) { relleno ->
        ContenidoPantalla(relleno = relleno) {
            EncabezadoClave(clave = clave)

            TarjetaClave(
                titulo = if (esFamiliar) "Clave familiar de ${clave.nombre}" else "Contraseña de ${clave.nombre} (docente)",
                clave = if (esFamiliar) GeneradorClave.formatear(clave.clave) else clave.clave,
                // Familia: los estudiantes con su curso; docente: el correo con el que inicia sesión
                detalle = if (esFamiliar) clave.estudiantes.joinToString("\n").ifEmpty { clave.detalle }
                else "Correo para iniciar sesión: ${clave.detalle}",
                tono = if (esFamiliar) TonoFamilia else TonoDocente
            )

            // Tarjeta blanca con las indicaciones, una por línea (Column = flex vertical)
            TarjetaBase {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Antes de entregarla", style = MaterialTheme.typography.titleSmall, color = TextoTitulo)
                    TextosClave.indicaciones(clave).forEach { paso ->
                        Text(text = "• $paso", style = MaterialTheme.typography.bodyLarge, color = TextoPrincipal)
                    }
                }
            }

            if (esFamiliar) {
                BotonSecundario(
                    texto = "Copiar clave y avisos",
                    onClick = ::copiar,
                    icono = painterResource(R.drawable.ic_copiar)
                )
                if (copiado) {
                    MensajeTexto(
                        mensaje = "Copiado: la clave, el acudiente, los estudiantes y los avisos de qué no hacer con ella.",
                        esError = false
                    )
                }
            }
            BotonPrincipal(texto = "Listo, ya la entregué", onClick = onListo)
        }
    }
}

/**
 * Encabezado grande que dice de quién es la clave, para no confundir la contraseña de un docente
 * con la clave de una familia: ícono y color del rol, el tipo de clave en letra grande y para quién es.
 */
@Composable
private fun EncabezadoClave(clave: ClaveParaEntregar) {
    val esFamiliar = clave.esFamiliar
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        // Row = flex horizontal: círculo con el ícono del rol y el tipo de clave en letra grande
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconoDegradado(
                icono = rememberVectorPainter(if (esFamiliar) Icons.Filled.Home else Icons.Filled.Edit),
                tono = if (esFamiliar) TonoFamilia else TonoDocente,
                tamano = 52.dp
            )
            Text(
                text = if (esFamiliar) "Clave familiar" else "Contraseña del docente",
                style = MaterialTheme.typography.headlineMedium,
                color = TextoTitulo,
                modifier = Modifier.semantics { heading() }
            )
        }
        Text(
            text = if (esFamiliar) "Para ${clave.nombre}, ${clave.detalle.replaceFirstChar { it.lowercase() }}"
            else "Para ${clave.nombre}, que entra a la app como docente. No es una clave familiar.",
            style = MaterialTheme.typography.titleMedium,
            color = if (esFamiliar) TonoFamilia.texto else TonoDocente.texto
        )
        Text(
            text = when (clave.tipo) {
                TipoClave.FAMILIA_NUEVA -> "Matrícula lista"
                TipoClave.CLAVE_CAMBIADA -> "Clave nueva: la anterior ya no sirve"
                TipoClave.DOCENTE_NUEVO -> "Cuenta creada"
                TipoClave.CONTRASENA_CAMBIADA -> "Contraseña nueva: la anterior ya no sirve"
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 900)
@Composable
fun ClaveParaEntregarScreenPreview() {
    PizarraRuralMovilTheme {
        ClaveParaEntregarScreen(
            clave = ClaveParaEntregar(
                tipo = TipoClave.FAMILIA_NUEVA,
                nombre = "Luz Gómez",
                detalle = "Acudiente de Pedro Gómez",
                clave = "K7M4QX2P",
                estudiantes = listOf("Pedro Gómez · 3° · Sede Centro, jornada mañana")
            ),
            onListo = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun ContrasenaDocentePreview() {
    PizarraRuralMovilTheme {
        ClaveParaEntregarScreen(
            clave = ClaveParaEntregar(TipoClave.DOCENTE_NUEVO, "Ana Gómez", "ana@pizarra.co", "H4TR9WK2"),
            onListo = {}
        )
    }
}
