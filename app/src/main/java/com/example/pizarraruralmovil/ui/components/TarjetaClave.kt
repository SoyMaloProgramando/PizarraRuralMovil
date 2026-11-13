package com.example.pizarraruralmovil.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import com.example.pizarraruralmovil.ui.theme.TextoSecundario
import com.example.pizarraruralmovil.ui.theme.TextoTitulo
import com.example.pizarraruralmovil.ui.theme.TonoColor
import com.example.pizarraruralmovil.ui.theme.TonoFamilia
import com.example.pizarraruralmovil.ui.theme.sombraSuave

/**
 * Clave en letra grande, para leerla o dictarla con calma, en un recuadro del [tono] de quien la recibe:
 * rosado para la clave familiar (como el rol Familia) y menta para la contraseña de un docente
 * (como el rol Docente), así no se confunden. Se entrega en persona (RN-20).
 */
@Composable
fun TarjetaClave(
    titulo: String,
    clave: String,
    detalle: String,
    modifier: Modifier = Modifier,
    tono: TonoColor = TonoFamilia
) {
    val forma = MaterialTheme.shapes.medium
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .sombraSuave(forma, color = tono.fuerte),
        shape = forma,
        color = tono.fondo,
        border = BorderStroke(2.dp, tono.borde)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IconoDegradado(icono = rememberVectorPainter(Icons.Filled.Lock), tono = tono, tamano = 48.dp)
            Text(text = titulo, style = MaterialTheme.typography.titleMedium, color = TextoTitulo, textAlign = TextAlign.Center)
            Text(
                text = clave,
                style = MaterialTheme.typography.displaySmall,
                letterSpacing = 4.sp,
                color = tono.texto,
                textAlign = TextAlign.Center
            )
            Text(text = detalle, style = MaterialTheme.typography.bodyMedium, color = TextoSecundario, textAlign = TextAlign.Center)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFDFBF7)
@Composable
fun TarjetaClavePreview() {
    PizarraRuralMovilTheme {
        TarjetaClave(
            titulo = "Clave familiar de María Pérez",
            clave = "MESA-3482",
            detalle = "Sofía Pérez · 3° · Sede Centro, jornada mañana",
            modifier = Modifier.padding(16.dp)
        )
    }
}
