package com.example.pizarraruralmovil.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.pizarraruralmovil.R

// Tipografía Andika (RNF-3): diseñada para niños que están aprendiendo a leer.
// Se usa en todo el texto que se lee: tareas, descripciones, campos y botones de texto.
val Andika = FontFamily(
    Font(R.font.andika_regular, FontWeight.Normal),
    Font(R.font.andika_bold, FontWeight.Bold)
)

// Fredoka: letra redondeada del diseño de Stitch, solo para títulos, nombres y botones grandes.
// Las dos fuentes van incluidas en la app (res/font), no se descargan. Licencias en assets/licencias.
val Fredoka = FontFamily(
    Font(R.font.fredoka_semibold, FontWeight.SemiBold),
    Font(R.font.fredoka_bold, FontWeight.Bold)
)

private val base = Typography()

// Escala tipográfica: títulos de pantalla 26 sp, encabezado 24 sp, texto de la tarea 18 sp,
// texto secundario 16 sp y etiquetas 14 sp (mínimo de la app).
val Typography = Typography(
    displayLarge = base.displayLarge.copy(fontFamily = Fredoka, fontWeight = FontWeight.Bold),
    displayMedium = base.displayMedium.copy(fontFamily = Fredoka, fontWeight = FontWeight.Bold),
    displaySmall = base.displaySmall.copy(fontFamily = Fredoka, fontWeight = FontWeight.Bold),
    headlineLarge = TextStyle(fontFamily = Fredoka, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 38.sp),
    headlineMedium = TextStyle(fontFamily = Fredoka, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 34.sp),
    headlineSmall = TextStyle(fontFamily = Fredoka, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 30.sp),
    titleLarge = TextStyle(fontFamily = Fredoka, fontWeight = FontWeight.Bold, fontSize = 26.sp, lineHeight = 32.sp),
    titleMedium = TextStyle(fontFamily = Fredoka, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 26.sp),
    titleSmall = TextStyle(fontFamily = Fredoka, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 24.sp),
    bodyLarge = TextStyle(fontFamily = Andika, fontWeight = FontWeight.Normal, fontSize = 18.sp, lineHeight = 26.sp),
    bodyMedium = TextStyle(fontFamily = Andika, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodySmall = TextStyle(fontFamily = Andika, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontFamily = Andika, fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 22.sp),
    labelMedium = TextStyle(fontFamily = Andika, fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 20.sp),
    labelSmall = TextStyle(fontFamily = Andika, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp)
)
