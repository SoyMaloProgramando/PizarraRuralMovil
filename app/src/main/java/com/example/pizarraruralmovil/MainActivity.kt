package com.example.pizarraruralmovil

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.pizarraruralmovil.navigation.AppNavegacion
import com.example.pizarraruralmovil.ui.theme.PizarraRuralMovilTheme
import java.util.Locale

class MainActivity : ComponentActivity() {

    // La app está en español: así el calendario y los textos del sistema también salen en español (RNF-12)
    override fun attachBaseContext(newBase: Context) {
        val configuracion = Configuration(newBase.resources.configuration)
        configuracion.setLocale(Locale.forLanguageTag("es-CO"))
        super.attachBaseContext(newBase.createConfigurationContext(configuracion))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Íconos oscuros en la barra de estado, porque el encabezado de la app es amarillo
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT))
        // Programación declarativa: no se crean vistas en XML ni se modifican a mano.
        // setContent recibe funciones @Composable que describen la interfaz según el estado.
        setContent {
            PizarraRuralMovilTheme {
                AppNavegacion()
            }
        }
    }
}
