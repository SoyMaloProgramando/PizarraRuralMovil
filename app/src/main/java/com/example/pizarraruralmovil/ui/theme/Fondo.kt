package com.example.pizarraruralmovil.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Fondo de las pantallas: crema con puntitos cada 18 dp, como en el diseño.
 * Se dibuja una sola baldosa con un punto y se repite (ImageShader), así no hay que pintar cada punto.
 * Va en el Scaffold de cada pantalla, que deja su propio color transparente.
 */
fun Modifier.fondoPizarra(): Modifier = drawWithCache {
    val lado = 18.dp.toPx().roundToInt().coerceAtLeast(1)
    val baldosa = ImageBitmap(lado, lado)
    Canvas(baldosa).apply {
        drawRect(0f, 0f, lado.toFloat(), lado.toFloat(), Paint().apply { color = FondoCrema })
        drawCircle(Offset(lado / 2f, lado / 2f), 1.5.dp.toPx(), Paint().apply { color = PuntoFondo; isAntiAlias = true })
    }
    val pincel = ShaderBrush(ImageShader(baldosa, TileMode.Repeated, TileMode.Repeated))
    onDrawBehind { drawRect(pincel) }
}

/**
 * Sombra suave y cálida de las tarjetas y botones del diseño (no gris dura).
 * El color de la sombra se ve desde Android 9; en versiones anteriores sale gris.
 */
fun Modifier.sombraSuave(
    forma: Shape,
    elevacion: Dp = 10.dp,
    color: Color = Color(0xFFDCD2BE)
): Modifier = shadow(elevation = elevacion, shape = forma, clip = false, ambientColor = color, spotColor = color)
