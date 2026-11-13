package com.example.pizarraruralmovil.model

/**
 * Los tres roles que entran a la app. El niño no tiene cuenta: usa el celular de su acudiente.
 * - COLEGIO: secretaría o rectoría. Verifica a los estudiantes y crea todas las cuentas.
 * - DOCENTE: publica las tareas de sus cursos.
 * - ACUDIENTE: padre, madre o acudiente. Entra con la clave familiar y valida las entregas.
 */
enum class Rol { COLEGIO, DOCENTE, ACUDIENTE }
