package com.example.pizarraruralmovil.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

/** Una pestaña de la barra inferior: texto e ícono. La usa el componente BarraNavegacion. */
interface Pestana {
    val titulo: String
    val icono: ImageVector
}

/** Pestañas del padre, madre o acudiente (RNF-8). */
enum class PestanaFamilia(override val titulo: String, override val icono: ImageVector) : Pestana {
    PIZARRA("Pizarra", Icons.AutoMirrored.Filled.List),
    MIS_HIJOS("Mis hijos", Icons.Filled.Face)
}

/** Pestañas del colegio. */
enum class PestanaColegio(override val titulo: String, override val icono: ImageVector) : Pestana {
    ESTUDIANTES("Estudiantes", Icons.Filled.Face),
    DOCENTES("Docentes", Icons.Filled.Person)
}
