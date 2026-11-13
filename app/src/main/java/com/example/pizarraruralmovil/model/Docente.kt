package com.example.pizarraruralmovil.model

/**
 * Cuenta del docente (ReqF-1). Ya no hay registro abierto: la crea el colegio (RN-23).
 * La contraseña nunca se guarda tal cual: solo su [huellaContrasena] (ver Huella).
 * En el servidor se guardará con bcrypt (RNF-26).
 */
data class Docente(
    val id: Int,
    val nombre: String,
    val correo: String,            // se guarda en minúsculas (RN-01)
    val huellaContrasena: String
)
