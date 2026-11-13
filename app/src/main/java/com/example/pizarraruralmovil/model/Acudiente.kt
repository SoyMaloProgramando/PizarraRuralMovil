package com.example.pizarraruralmovil.model

/**
 * Padre, madre o acudiente de uno o varios estudiantes.
 * No se registra: el colegio lo crea al matricular a su hijo y le entrega en persona
 * la clave familiar (RN-20). Con esa clave entra a la app sin crear cuenta.
 * La app no guarda la clave: solo su [huellaClave] (ver Huella), que sirve para comprobarla.
 */
data class Acudiente(
    val id: Int,
    val nombre: String,
    val huellaClave: String   // huella de la clave de 8 caracteres (sin I, L, O, 0 ni 1), única en todo el sistema
)
