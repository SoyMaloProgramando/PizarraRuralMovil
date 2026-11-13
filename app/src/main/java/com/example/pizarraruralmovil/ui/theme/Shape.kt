package com.example.pizarraruralmovil.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Esquinas muy redondeadas, como en el diseño: 16 dp en campos y botones cuadrados,
// 24 dp en las tarjetas y 28-32 dp en ventanas y en el encabezado.
val Formas = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

/** Forma del encabezado amarillo: esquinas redondeadas solo abajo. */
val FormaEncabezado = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
