package com.example.pizarraruralmovil

import com.example.pizarraruralmovil.data.Almacen
import com.example.pizarraruralmovil.data.DatosDePrueba
import com.example.pizarraruralmovil.data.DatosGuardados
import com.example.pizarraruralmovil.logic.FechasLegibles
import com.example.pizarraruralmovil.logic.GeneradorClave
import com.example.pizarraruralmovil.logic.Huella
import com.example.pizarraruralmovil.logic.LimiteIntentos
import com.example.pizarraruralmovil.logic.ReglasTareas
import com.example.pizarraruralmovil.logic.Resultado
import com.example.pizarraruralmovil.logic.TextosClave
import com.example.pizarraruralmovil.logic.Validaciones
import com.example.pizarraruralmovil.model.EstadoTarea
import com.example.pizarraruralmovil.model.Tarea
import com.example.pizarraruralmovil.viewmodel.PizarraViewModel
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/** Pruebas unitarias de la lógica clave: estados de tarea, fechas, claves y reparto de responsabilidades (RNF-36). */
class ReglasNegocioTest {

    private val hoy = LocalDate.of(2026, 10, 30) // viernes

    @Test
    fun estadosDeTarea_RN11() {
        assertEquals(EstadoTarea.PENDIENTE, ReglasTareas.calcularEstado(hoy, validada = false, hoy = hoy))           // CA-15.1
        assertEquals(EstadoTarea.VENCIDA, ReglasTareas.calcularEstado(hoy.minusDays(1), validada = false, hoy = hoy)) // CA-15.2
        assertEquals(EstadoTarea.VALIDADA, ReglasTareas.calcularEstado(hoy.minusDays(1), validada = true, hoy = hoy)) // CA-15.3
    }

    @Test
    fun pizarraOrdenaYResume_RN13_ReqF16() {
        val tareas = listOf(
            Tarea(1, 1, "Vencida", hoy.minusDays(2)),
            Tarea(2, 1, "Pendiente mañana", hoy.plusDays(1)),
            Tarea(3, 1, "Pendiente hoy", hoy),
            Tarea(4, 1, "Validada 1", hoy.plusDays(2)),
            Tarea(5, 1, "Validada 2", hoy.minusDays(1)),
            Tarea(6, 1, "Muy vieja, no se muestra", hoy.minusDays(20))
        )
        val pizarra = ReglasTareas.pizarra(tareas, tareasValidadas = setOf(4, 5), hoy = hoy)

        assertEquals(listOf(1, 3, 2, 5, 4), pizarra.map { it.tarea.id }) // no validadas por fecha y validadas al final
        val resumen = ReglasTareas.resumen(pizarra)
        assertEquals(3, resumen.pendientes) // CA-16.1: "3 pendientes"
        assertEquals(2, resumen.validadas)  // "2 validadas"
        assertEquals(1, resumen.vencidas)   // "1 vencida"
    }

    @Test
    fun fechasLegibles_RNF13() {
        assertEquals("Vence hoy", FechasLegibles.vencimiento(hoy, hoy))
        assertEquals("Vence mañana", FechasLegibles.vencimiento(hoy.plusDays(1), hoy))
        assertEquals("Vence el lunes 2", FechasLegibles.vencimiento(hoy.plusDays(3), hoy))
        assertEquals("Vence el lunes 9 de noviembre", FechasLegibles.vencimiento(hoy.plusDays(10), hoy))
        assertEquals("Venció ayer", FechasLegibles.vencimiento(hoy.minusDays(1), hoy))
    }

    @Test
    fun claveFamiliar_RN20() {
        repeat(200) {
            val clave = GeneradorClave.generar(emptyList())
            assertEquals(8, clave.length)
            assertNull(Validaciones.validarFormatoClave(clave))
            assertFalse(clave.any { it in "ILO01" })     // no se confunden al leerlas
            assertTrue(clave.any { it.isLetter() } && clave.any { it.isDigit() })
        }
        assertEquals("MESA3482", GeneradorClave.normalizar(" mesa-3482 "))
        assertEquals("MESA-3482", GeneradorClave.formatear("MESA3482"))
        assertNotNull(Validaciones.validarFormatoClave("MESA348"))   // muy corta
        assertNotNull(Validaciones.validarFormatoClave("MESA3480"))  // usa el número 0
        // El aviso por un carácter que las claves no tienen sale aunque la clave esté incompleta
        assertEquals(Validaciones.validarFormatoClave("MEÑA3482"), Validaciones.validarFormatoClave("MES0"))
        assertTrue(Validaciones.validarFormatoClave("MES0")!!.contains("Ñ"))
    }

    @Test
    fun contrasenaYNombres_RN02_RN19() {
        assertNotNull(Validaciones.validarContrasena("abc1234"))   // 7 caracteres
        assertNotNull(Validaciones.validarContrasena("abcdefgh"))  // sin número
        assertNotNull(Validaciones.validarContrasena("12345678"))  // sin letra
        assertNull(Validaciones.validarContrasena("pizarra123"))
        assertNull(Validaciones.validarContrasena(GeneradorClave.generar(emptyList()))) // la contraseña generada cumple RN-02

        assertNull(Validaciones.validarNombrePersona("Sofía Pérez"))
        assertNull(Validaciones.validarNombrePersona("  María  José Núñez "))
        assertNotNull(Validaciones.validarNombrePersona("Sofi"))        // falta el apellido
        assertNotNull(Validaciones.validarNombrePersona("Sofía P3rez")) // números
    }

    @Test
    fun cincoFallosBloqueanUnMinuto_RN06() {
        var ahora = 0L
        val limite = LimiteIntentos(reloj = { ahora })
        repeat(5) {
            assertTrue(limite.puedeIntentar())
            limite.registrarFallo()
            ahora += 1_000L
        }
        assertFalse(limite.puedeIntentar()) // sexto intento en el mismo minuto: bloqueado (CA-01.6)
        ahora += 58_000L
        assertFalse(limite.puedeIntentar()) // el bloqueo dura un minuto completo
        ahora += 2_000L
        assertTrue(limite.puedeIntentar())  // pasó el minuto
    }

    @Test
    fun textoDeTareaSinSaltosDeLinea_RN10() {
        assertEquals("Leer la página 20 Dibujar el final", Validaciones.limpiarTexto("  Leer la página 20\nDibujar el final "))
    }

    // ---------- Reparto de responsabilidades (revisión del profesor) ----------

    @Test
    fun soloElColegioMatricula_RN19() {
        val vm = PizarraViewModel()
        // Sin sesión del colegio nadie puede matricular
        val sinSesion = vm.matricular("Pedro Gómez", 1, "🐶", null, "Luz Gómez", verificado = true)
        assertTrue(sinSesion is Resultado.Error)

        vm.iniciarSesionColegio(DatosDePrueba.CORREO_COLEGIO, DatosDePrueba.CONTRASENA_COLEGIO)
        // Sin revisar los documentos en persona tampoco
        assertTrue(vm.matricular("Pedro Gómez", 1, "🐶", null, "Luz Gómez", verificado = false) is Resultado.Error)

        val matricula = vm.matricular("Pedro Gómez", 1, "🐶", null, "Luz Gómez", verificado = true)
        assertTrue(matricula is Resultado.Exito)
        val clave = vm.claveParaEntregar
        assertNotNull(clave) // la clave nueva se muestra una vez para entregarla en persona
        vm.descartarClave()
        assertNull(vm.claveParaEntregar)

        // La familia entra con la clave, sin registrarse, y solo ve a su hijo
        assertTrue(vm.ingresarConClave(GeneradorClave.formatear(clave!!.clave)) is Resultado.Exito)
        assertEquals(listOf("Pedro Gómez"), vm.hijos.map { it.nombre })
    }

    @Test
    fun laFamiliaNoVeAOtrosEstudiantes_RN21() {
        val vm = PizarraViewModel()
        assertTrue(vm.ingresarConClave(DatosDePrueba.CLAVE_FAMILIA_PEREZ) is Resultado.Exito)
        assertEquals(listOf("Sofía Pérez", "Juan Pérez"), vm.hijos.map { it.nombre })
        // La lista del curso de 3° (donde también están Valentina y Mateo) no está disponible para la familia
        assertTrue(vm.estudiantesDelCurso(1).isEmpty())
        assertNull(vm.buscarEstudiante(3))
    }

    @Test
    fun claveNuevaInvalidaLaAnterior_RN20() {
        val vm = PizarraViewModel()
        vm.iniciarSesionColegio(DatosDePrueba.CORREO_COLEGIO, DatosDePrueba.CONTRASENA_COLEGIO)
        val nueva = (vm.generarNuevaClave(acudienteId = 1) as Resultado.Exito).valor.clave
        assertNotEquals(DatosDePrueba.CLAVE_FAMILIA_PEREZ, nueva)

        assertTrue(vm.ingresarConClave(DatosDePrueba.CLAVE_FAMILIA_PEREZ) is Resultado.Error) // la anterior ya no sirve
        assertTrue(vm.ingresarConClave(nueva) is Resultado.Exito)
    }

    @Test
    fun soloElAcudienteValidaASusHijos_RN22() {
        val vm = PizarraViewModel()
        // Sin sesión de familia no se puede validar
        vm.cambiarValidacion(hijoId = 1, tareaId = 2, validada = true)
        vm.ingresarConClave(DatosDePrueba.CLAVE_FAMILIA_PEREZ)
        assertFalse(2 in vm.hijos.first { it.id == 1 }.tareasValidadas)

        vm.cambiarValidacion(hijoId = 1, tareaId = 2, validada = true)
        assertTrue(2 in vm.hijos.first { it.id == 1 }.tareasValidadas)

        // No puede validar a un estudiante de otra familia (Valentina, id 3) ni una tarea de otro curso
        vm.cambiarValidacion(hijoId = 3, tareaId = 2, validada = true)
        vm.cambiarValidacion(hijoId = 1, tareaId = 5, validada = true)
        assertFalse(5 in vm.hijos.first { it.id == 1 }.tareasValidadas)

        // El docente ve cuántas familias validaron: tarea 2 → Sofía; tarea 3 → Sofía y Valentina
        vm.iniciarSesionDocente(DatosDePrueba.CORREO_DOCENTE, DatosDePrueba.CONTRASENA_DOCENTE)
        val validaciones = vm.validacionesDelCurso(1)
        assertEquals(1, validaciones[2])
        assertEquals(2, validaciones[3])
    }

    @Test
    fun cincoClavesFallidasBloquean_RN06() {
        val vm = PizarraViewModel()
        repeat(5) { assertTrue(vm.ingresarConClave("ABCD2345") is Resultado.Error) }
        // Aunque ahora escriba la clave correcta, debe esperar el minuto
        val bloqueado = vm.ingresarConClave(DatosDePrueba.CLAVE_FAMILIA_PEREZ)
        assertEquals(PizarraViewModel.MENSAJE_DEMASIADOS_INTENTOS, (bloqueado as Resultado.Error).mensaje)
    }

    @Test
    fun elColegioCreaLaCuentaDelDocente_RN23() {
        val vm = PizarraViewModel()
        vm.iniciarSesionColegio(DatosDePrueba.CORREO_COLEGIO, DatosDePrueba.CONTRASENA_COLEGIO)
        assertTrue(vm.crearCuentaDocente("Ana Gómez", "ANA@pizarra.co") is Resultado.Exito)
        val generada = vm.claveParaEntregar!!.clave
        assertTrue(vm.crearCuentaDocente("Otra Persona", "ana@pizarra.co") is Resultado.Error) // correo repetido (RN-01)
        assertTrue(vm.iniciarSesionDocente("ana@pizarra.co", generada) is Resultado.Exito)
    }

    // ---------- Arreglos de la revisión ----------

    @Test
    fun acudientesConElMismoNombre_tienenClavesDistintas_RN21() {
        val vm = PizarraViewModel()
        vm.iniciarSesionColegio(DatosDePrueba.CORREO_COLEGIO, DatosDePrueba.CONTRASENA_COLEGIO)
        // Otra María Pérez (otra persona) matricula a su hijo: no se mezcla con la familia de Sofía y Juan
        assertTrue(vm.matricular("Pedro Gómez", 1, "🐶", null, "María Pérez", verificado = true) is Resultado.Exito)
        val claveNueva = vm.claveParaEntregar!!.clave
        assertNotEquals(DatosDePrueba.CLAVE_FAMILIA_PEREZ, claveNueva)

        assertTrue(vm.ingresarConClave(DatosDePrueba.CLAVE_FAMILIA_PEREZ) is Resultado.Exito)
        assertEquals(listOf("Sofía Pérez", "Juan Pérez"), vm.hijos.map { it.nombre })
        assertTrue(vm.ingresarConClave(claveNueva) is Resultado.Exito)
        assertEquals(listOf("Pedro Gómez"), vm.hijos.map { it.nombre })
    }

    @Test
    fun elMismoNinoNoSeMatriculaDosVecesConSuAcudiente() {
        val vm = PizarraViewModel()
        vm.iniciarSesionColegio(DatosDePrueba.CORREO_COLEGIO, DatosDePrueba.CONTRASENA_COLEGIO)
        // Sofía ya está en 3° con María Pérez (acudiente 1): no puede quedar también en 5°
        assertTrue(vm.matricular("Sofía Pérez", 2, "🐰", 1, "", verificado = true) is Resultado.Error)
    }

    @Test
    fun unIngresoCorrectoNoBorraLosFallos_RN06() {
        val vm = PizarraViewModel()
        repeat(4) { vm.ingresarConClave("ABCD2345") }
        assertTrue(vm.ingresarConClave(DatosDePrueba.CLAVE_FAMILIA_ROJAS) is Resultado.Exito) // su propia clave
        vm.ingresarConClave("ABCD2346") // quinto fallo dentro del minuto
        val bloqueado = vm.ingresarConClave(DatosDePrueba.CLAVE_FAMILIA_ROJAS)
        assertEquals(PizarraViewModel.MENSAJE_DEMASIADOS_INTENTOS, (bloqueado as Resultado.Error).mensaje)
    }

    @Test
    fun losIdNoSeReutilizanAlRetirar() {
        val vm = PizarraViewModel()
        vm.iniciarSesionColegio(DatosDePrueba.CORREO_COLEGIO, DatosDePrueba.CONTRASENA_COLEGIO)
        val pedro = (vm.matricular("Pedro Gómez", 1, "🐶", null, "Luz Gómez", verificado = true) as Resultado.Exito).valor
        vm.retirarEstudiante(pedro.id)
        val ana = (vm.matricular("Ana Gómez", 1, "🐱", null, "Luz Gómez", verificado = true) as Resultado.Exito).valor
        assertNotEquals(pedro.id, ana.id)
        assertNull(vm.buscarEstudiante(pedro.id))
    }

    @Test
    fun contrasenaNuevaDelDocente_laAnteriorDejaDeServir_RN23() {
        val vm = PizarraViewModel()
        vm.iniciarSesionColegio(DatosDePrueba.CORREO_COLEGIO, DatosDePrueba.CONTRASENA_COLEGIO)
        val nueva = (vm.generarNuevaContrasena(docenteId = 1) as Resultado.Exito).valor.clave
        assertNull(Validaciones.validarContrasena(nueva)) // cumple RN-02
        assertTrue(vm.iniciarSesionDocente(DatosDePrueba.CORREO_DOCENTE, DatosDePrueba.CONTRASENA_DOCENTE) is Resultado.Error)
        assertTrue(vm.iniciarSesionDocente(DatosDePrueba.CORREO_DOCENTE, nueva) is Resultado.Exito)
    }

    @Test
    fun sinSesionNoHayTareasNiPizarraAjena_RN03_RN21() {
        val vm = PizarraViewModel()
        assertTrue(vm.tareasDelCurso(1).isEmpty())
        vm.ingresarConClave(DatosDePrueba.CLAVE_FAMILIA_ROJAS)
        val sofia = DatosDePrueba.estudiantes.first { it.nombre == "Sofía Pérez" }
        assertTrue(vm.pizarraDe(sofia).isEmpty())         // Sofía es de otra familia
        assertTrue(vm.pizarraDe(vm.hijos.first()).isNotEmpty()) // Valentina sí es su hija
    }


    // ---------- Cambios del 12 de noviembre ----------

    /** Almacén de prueba: guarda la última foto en memoria, como si fuera el archivo del celular. */
    private class AlmacenDePrueba : Almacen {
        var datos: DatosGuardados? = null
        override fun cargar(): DatosGuardados? = datos
        override fun guardar(datos: DatosGuardados) {
            this.datos = datos
        }
    }

    @Test
    fun nombreCompleto_unSoloNombreNoBasta_RN19() {
        assertEquals(Validaciones.MENSAJE_NOMBRE_COMPLETO, Validaciones.validarNombrePersona("Luz"))
        assertEquals(Validaciones.MENSAJE_NOMBRE_COMPLETO, Validaciones.validarNombrePersona("   "))
        assertTrue(Validaciones.MENSAJE_NOMBRE_COMPLETO.contains("apellido"))

        val vm = PizarraViewModel()
        vm.iniciarSesionColegio(DatosDePrueba.CORREO_COLEGIO, DatosDePrueba.CONTRASENA_COLEGIO)
        val soloNombre = vm.matricular("Pedro Gómez", 1, "🐶", null, "Luz", verificado = true)
        assertTrue((soloNombre as Resultado.Error).mensaje.startsWith("Nombre del acudiente: escriba el nombre completo"))
        assertNull(vm.claveParaEntregar) // no se creó ningún acudiente ni clave
        assertTrue(vm.crearCuentaDocente("Ana", "ana@pizarra.co") is Resultado.Error)
    }

    @Test
    fun claves_seGuardanComoHuella() {
        val huella = Huella.deClave(DatosDePrueba.CLAVE_FAMILIA_PEREZ)
        assertEquals(64, huella.length)                                   // 256 bits en hexadecimal
        assertEquals(huella, Huella.deClave(DatosDePrueba.CLAVE_FAMILIA_PEREZ)) // siempre la misma para la misma clave
        assertNotEquals(huella, Huella.deClave(DatosDePrueba.CLAVE_FAMILIA_ROJAS))
        assertFalse(huella.contains(DatosDePrueba.CLAVE_FAMILIA_PEREZ, ignoreCase = true))
        // La contraseña del docente no distingue mayúsculas: las generadas solo tienen mayúsculas y números
        assertEquals(Huella.deContrasena("pizarra123"), Huella.deContrasena("PIZARRA123"))
    }

    @Test
    fun docente_entraAunqueEscribaLaContrasenaEnMinusculas_RN23() {
        val vm = PizarraViewModel()
        vm.iniciarSesionColegio(DatosDePrueba.CORREO_COLEGIO, DatosDePrueba.CONTRASENA_COLEGIO)
        vm.crearCuentaDocente("Ana Gómez", "ana@pizarra.co")
        val generada = vm.claveParaEntregar!!.clave
        assertTrue(vm.iniciarSesionDocente("ana@pizarra.co", generada.lowercase()) is Resultado.Exito)
    }

    @Test
    fun textoParaCopiar_traeAcudienteEstudianteClaveYAvisos() {
        val vm = PizarraViewModel()
        vm.iniciarSesionColegio(DatosDePrueba.CORREO_COLEGIO, DatosDePrueba.CONTRASENA_COLEGIO)
        vm.matricular("Pedro Gómez", 1, "🐶", null, "Luz Gómez", verificado = true)
        val clave = vm.claveParaEntregar!!
        val texto = TextosClave.textoParaCopiar(clave)

        assertTrue(texto.contains("Acudiente: Luz Gómez"))
        assertTrue(texto.contains("Estudiante: Pedro Gómez · 3° · Sede Centro, jornada mañana"))
        assertTrue(texto.contains("Clave familiar: ${GeneradorClave.formatear(clave.clave)}"))
        assertTrue(texto.contains("Qué no hacer con esta clave"))
        assertTrue(texto.contains("No la envíe a grupos de WhatsApp"))
        // Lo que se copia repite el aviso que se ve en la pantalla
        assertTrue(TextosClave.indicaciones(clave).any { it.contains("WhatsApp") })

        // Clave nueva de una familia con dos hijos: se copian los dos
        val nueva = (vm.generarNuevaClave(acudienteId = 1) as Resultado.Exito).valor
        val textoNueva = TextosClave.textoParaCopiar(nueva)
        assertTrue(textoNueva.contains("CLAVE FAMILIAR NUEVA"))
        assertTrue(textoNueva.contains("• Sofía Pérez · 3°") && textoNueva.contains("• Juan Pérez · 5°"))
    }

    @Test
    fun editarTarea_soloSuDocente_yConservaLasValidaciones_RN03() {
        val sinSesion = PizarraViewModel()
        assertTrue(sinSesion.editarTarea(3, "Otra cosa", sinSesion.hoy) is Resultado.Error)

        val vm = PizarraViewModel()
        vm.iniciarSesionDocente(DatosDePrueba.CORREO_DOCENTE, DatosDePrueba.CONTRASENA_DOCENTE)
        val tarea3 = vm.buscarTareaDelDocente(3)!!
        val editada = vm.editarTarea(3, "  Leer el cuento de la página 22  ", tarea3.fechaLimite.plusDays(1))
        assertEquals("Leer el cuento de la página 22", (editada as Resultado.Exito).valor.texto)
        assertEquals(tarea3.fechaLimite.plusDays(1), vm.buscarTareaDelDocente(3)!!.fechaLimite)
        assertEquals(2, vm.validacionesDelCurso(1)[3]) // Sofía y Valentina la siguen teniendo validada

        // Una tarea vencida se puede corregir sin cambiar su fecha, pero no se le pone una fecha pasada
        val vencida = vm.buscarTareaDelDocente(1)!!
        assertTrue(vm.editarTarea(1, "Repasar las tablas del 2 al 6", vencida.fechaLimite) is Resultado.Exito)
        assertTrue(vm.editarTarea(1, "Repasar", vencida.fechaLimite.minusDays(1)) is Resultado.Error)
        assertTrue(vm.editarTarea(1, "   ", vencida.fechaLimite) is Resultado.Error)
    }

    @Test
    fun eliminarTarea_borraSusValidaciones_yNoReutilizaElId() {
        val vm = PizarraViewModel()
        vm.iniciarSesionDocente(DatosDePrueba.CORREO_DOCENTE, DatosDePrueba.CONTRASENA_DOCENTE)
        assertTrue(vm.eliminarTarea(3) is Resultado.Exito)
        assertNull(vm.buscarTareaDelDocente(3))
        assertFalse(vm.tareasDelCurso(1).any { it.id == 3 })
        assertTrue(vm.estudiantesDelCurso(1).none { 3 in it.tareasValidadas })

        val nueva = (vm.publicarTarea(1, "Tarea nueva", vm.hoy) as Resultado.Exito).valor
        assertEquals(7, nueva.id) // la 3 no se reutiliza (la última de prueba es la 6)
        assertEquals(0, vm.validacionesDelCurso(1)[nueva.id])

        // Otra persona sin sesión de docente no puede eliminar
        assertTrue(PizarraViewModel().eliminarTarea(4) is Resultado.Error)
    }

    @Test
    fun losDatosSiguenAhiAlVolverAAbrirLaApp() {
        val almacen = AlmacenDePrueba()
        val antes = PizarraViewModel(almacen, Dispatchers.Unconfined)
        antes.iniciarSesionColegio(DatosDePrueba.CORREO_COLEGIO, DatosDePrueba.CONTRASENA_COLEGIO)
        antes.crearCuentaDocente("Ana Gómez", "ana@pizarra.co")
        val contrasena = antes.claveParaEntregar!!.clave
        antes.matricular("Pedro Gómez", 1, "🐶", null, "Luz Gómez", verificado = true)
        val claveFamiliar = antes.claveParaEntregar!!.clave

        // Lo guardado no tiene las claves tal cual, solo su huella
        val guardado = almacen.datos.toString()
        assertFalse(guardado.contains(contrasena))
        assertFalse(guardado.contains(claveFamiliar))

        // "Cerrar y abrir la app": un ViewModel nuevo con el mismo almacén
        val despues = PizarraViewModel(almacen, Dispatchers.Unconfined)
        assertNull(despues.colegioActual) // la sesión no se guarda
        despues.iniciarSesionColegio(DatosDePrueba.CORREO_COLEGIO, DatosDePrueba.CONTRASENA_COLEGIO)
        assertTrue(despues.docentesDelColegio().any { it.nombre == "Ana Gómez" })
        assertTrue(despues.iniciarSesionDocente("ana@pizarra.co", contrasena) is Resultado.Exito)
        assertTrue(despues.ingresarConClave(claveFamiliar) is Resultado.Exito)
        assertEquals(listOf("Pedro Gómez"), despues.hijos.map { it.nombre })

        // Los id siguen donde iban: el siguiente estudiante no repite el de Pedro
        despues.iniciarSesionColegio(DatosDePrueba.CORREO_COLEGIO, DatosDePrueba.CONTRASENA_COLEGIO)
        val otro = (despues.matricular("Ana Ruiz", 1, "🐱", null, "Rosa Ruiz", verificado = true) as Resultado.Exito).valor
        assertEquals(6, otro.id)
    }
}
