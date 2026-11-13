package com.example.pizarraruralmovil.model

/**
 * Cuenta del colegio (secretaría o rectoría). Es la única que matricula estudiantes
 * y crea las cuentas de docentes y acudientes (RN-19 y RN-23).
 * La crea quien administra la app cuando el colegio empieza a usarla.
 */
data class CuentaColegio(
    val id: Int,
    val nombre: String,
    val correo: String,      // se guarda en minúsculas
    val contrasena: String   // en el servidor irá con hash bcrypt (RNF-26)
)
