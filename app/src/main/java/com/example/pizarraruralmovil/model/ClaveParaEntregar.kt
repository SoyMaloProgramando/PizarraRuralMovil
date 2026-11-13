package com.example.pizarraruralmovil.model

/** Por qué se generó la clave: cambia el título y las indicaciones de la pantalla que la muestra. */
enum class TipoClave {
    FAMILIA_NUEVA,        // clave familiar al matricular al primer hijo de un acudiente
    CLAVE_CAMBIADA,       // clave familiar nueva porque la anterior se perdió (la anterior deja de servir)
    DOCENTE_NUEVO,        // contraseña de un docente al crear su cuenta
    CONTRASENA_CAMBIADA   // contraseña nueva de un docente (la anterior deja de servir)
}

/**
 * Clave recién generada que el colegio debe entregar en persona.
 * La app la muestra una sola vez y no la guarda; si se pierde, el colegio genera otra (RN-20).
 */
data class ClaveParaEntregar(
    val tipo: TipoClave,
    val nombre: String,                          // quién la recibe: "María Pérez"
    val detalle: String,                         // "Acudiente de Sofía Pérez" o el correo del docente
    val clave: String,
    val estudiantes: List<String> = emptyList()  // familia: "Sofía Pérez · 3° · Sede Centro, jornada mañana"
) {
    /** Clave familiar (acudiente) o contraseña de un docente. */
    val esFamiliar: Boolean get() = tipo == TipoClave.FAMILIA_NUEVA || tipo == TipoClave.CLAVE_CAMBIADA
}
