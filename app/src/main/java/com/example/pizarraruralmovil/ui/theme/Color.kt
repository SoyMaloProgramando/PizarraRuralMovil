package com.example.pizarraruralmovil.ui.theme

import androidx.compose.ui.graphics.Color

// Guía visual de la app (RNF-1 y RNF-2), con el estilo del diseño de Stitch para básica primaria:
// fondo crema con puntitos, tarjetas blancas muy redondeadas con sombra suave, encabezado amarillo,
// botones rosados en degradado y un color por rol (familia rosado, docente menta y colegio cielo).
// Todo el texto oscuro o blanco que va sobre un color se revisó para que se lea bien (contraste WCAG).

// ---------- Base ----------
val FondoCrema = Color(0xFFFDFBF7)      // fondo de todas las pantallas
val PuntoFondo = Color(0xFFF3ECE2)      // puntitos del fondo, como en el diseño
val BlancoTarjeta = Color(0xFFFFFFFF)
val TextoTitulo = Color(0xFF1E293B)     // títulos y nombres
val TextoPrincipal = Color(0xFF2D3748)  // texto normal
val TextoSecundario = Color(0xFF5A6A80) // descripciones y ayudas
val BordeSuave = Color(0xFFE2E8F0)      // borde de tarjetas y campos
val BordeMedio = Color(0xFFCBD5E1)
val BordeCampo = Color(0xFF7C8AA0)      // borde de los campos de texto: se distingue del fondo (contraste 3:1)
val GrisChip = Color(0xFFF1F5F9)        // fondo de chips neutros y de la flecha de las tarjetas
val GrisIcono = Color(0xFF94A3B8)

// ---------- Encabezado amarillo ----------
val AmarilloEncabezado = Color(0xFFFFCA28)
val TextoEncabezado = Color(0xFF451A03)       // café muy oscuro sobre el amarillo
val SubtituloEncabezado = Color(0xFF78350F)
val BordeBotonEncabezado = Color(0xFFFCD34D)

// ---------- Acción principal (botones y botón flotante) ----------
val RosaAccion = Color(0xFFE91E63)       // también es el color "primary" del tema
val RosaAccionClaro = Color(0xFFF06292)
val RosaNavegacion = Color(0xFFD81B60)   // pestaña elegida
val RosaIndicador = Color(0xFFFCE4EC)    // pastilla detrás del ícono de la pestaña elegida
val RosaError = Color(0xFFBE123C)        // mensajes de error

/** Degradado de los botones principales, de izquierda a derecha (texto blanco encima). */
val DegradadoAccion = listOf(RosaAccion, RosaAccionClaro)

/** Degradado del botón "Validar entrega": verde, el color de una tarea validada. */
val DegradadoValidar = listOf(Color(0xFF047857), Color(0xFF059669))

/**
 * Un color de la app en sus usos: [fondo] suave, [borde], color [fuerte], [texto] legible sobre el fondo
 * suave, y el [degradado] de su ícono. Así cada chip, aviso, avatar o tarjeta de rol usa el mismo juego.
 */
data class TonoColor(
    val fondo: Color,
    val borde: Color,
    val fuerte: Color,
    val texto: Color,
    val degradado: List<Color>
)

val TonoAmarillo = TonoColor(
    fondo = Color(0xFFFFFBEB), borde = Color(0xFFFDE68A), fuerte = Color(0xFFFFCA28), texto = Color(0xFF92400E),
    degradado = listOf(Color(0xFFFBBF24), Color(0xFFFFCA28))
)
val TonoRosado = TonoColor(
    fondo = Color(0xFFFFF0F3), borde = Color(0xFFFFD1DC), fuerte = Color(0xFFFF6B8B), texto = Color(0xFFBE123C),
    degradado = listOf(Color(0xFFFB7185), Color(0xFFFF6B8B))
)
val TonoMenta = TonoColor(
    fondo = Color(0xFFECFDF5), borde = Color(0xFFA7F3D0), fuerte = Color(0xFF10B981), texto = Color(0xFF065F46),
    degradado = listOf(Color(0xFF10B981), Color(0xFF34D399))
)
val TonoCielo = TonoColor(
    fondo = Color(0xFFF0F9FF), borde = Color(0xFFBAE6FD), fuerte = Color(0xFF0EA5E9), texto = Color(0xFF075985),
    degradado = listOf(Color(0xFF0EA5E9), Color(0xFF22D3EE))
)
val TonoRojo = TonoColor(
    fondo = Color(0xFFFFF1F2), borde = Color(0xFFFECDD3), fuerte = Color(0xFFFB7185), texto = Color(0xFF9F1239),
    degradado = listOf(Color(0xFFF43F5E), Color(0xFFFB7185))
)
val TonoLila = TonoColor(
    fondo = Color(0xFFF5F3FF), borde = Color(0xFFDDD6FE), fuerte = Color(0xFFA78BFA), texto = Color(0xFF5B21B6),
    degradado = listOf(Color(0xFF8B5CF6), Color(0xFFA78BFA))
)
val TonoNaranja = TonoColor(
    fondo = Color(0xFFFFF7ED), borde = Color(0xFFFED7AA), fuerte = Color(0xFFFB923C), texto = Color(0xFF9A3412),
    degradado = listOf(Color(0xFFF97316), Color(0xFFFB923C))
)
val TonoTurquesa = TonoColor(
    fondo = Color(0xFFF0FDFA), borde = Color(0xFF99F6E4), fuerte = Color(0xFF14B8A6), texto = Color(0xFF115E59),
    degradado = listOf(Color(0xFF14B8A6), Color(0xFF2DD4BF))
)

/** Color de cada rol, igual en la primera pantalla, en las claves y en el diagrama de flujo. */
val TonoFamilia = TonoRosado
val TonoDocente = TonoMenta
val TonoColegio = TonoCielo

/** Un tono por grado, de 1° a 5°. */
val TonosGrado = listOf(TonoAmarillo, TonoRosado, TonoCielo, TonoMenta, TonoNaranja)

/** Tonos de los dibujos y de las iniciales: cada dibujo (o persona) siempre sale con el mismo. */
val TonosAvatar = listOf(TonoMenta, TonoCielo, TonoLila, TonoTurquesa, TonoAmarillo, TonoRosado, TonoNaranja)
