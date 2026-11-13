package com.example.pizarraruralmovil.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Tema claro único para el MVP (RNF-20): sin modo oscuro ni colores dinámicos,
// para que la app siempre se vea con los colores del diseño.
private val EsquemaClaro = lightColorScheme(
    primary = RosaAccion,                  // casillas, foco de los campos y botones de texto de las ventanas
    onPrimary = BlancoTarjeta,
    primaryContainer = TonoRosado.fondo,
    onPrimaryContainer = TonoRosado.texto,
    secondary = AmarilloEncabezado,        // encabezado
    onSecondary = TextoEncabezado,
    secondaryContainer = TonoAmarillo.fondo,
    onSecondaryContainer = TonoAmarillo.texto,
    tertiary = TonoMenta.fuerte,
    onTertiary = BlancoTarjeta,
    tertiaryContainer = TonoMenta.fondo,
    onTertiaryContainer = TonoMenta.texto,
    background = FondoCrema,
    onBackground = TextoPrincipal,
    surface = BlancoTarjeta,
    onSurface = TextoPrincipal,
    surfaceVariant = GrisChip,
    onSurfaceVariant = TextoSecundario,
    surfaceContainerLowest = BlancoTarjeta,
    surfaceContainerLow = BlancoTarjeta,
    surfaceContainer = BlancoTarjeta,
    surfaceContainerHigh = BlancoTarjeta,  // ventanas de confirmación blancas
    surfaceContainerHighest = GrisChip,
    outline = BordeMedio,
    outlineVariant = BordeSuave,
    error = RosaError,
    onError = BlancoTarjeta,
    errorContainer = TonoRojo.fondo,
    onErrorContainer = TonoRojo.texto,
    inverseSurface = TextoPrincipal,       // avisos de abajo ("Entrega validada · Deshacer")
    inverseOnSurface = BlancoTarjeta,
    inversePrimary = TonoRosado.borde
)

@Composable
fun PizarraRuralMovilTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaClaro,
        typography = Typography,
        shapes = Formas,
        content = content
    )
}
