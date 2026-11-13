package com.example.pizarraruralmovil.model

/**
 * Estados de una tarea vista desde la pizarra de un estudiante (RN-11).
 * "Validada" reemplaza a "Hecha": quien confirma la entrega es el acudiente, no el niño (RN-22).
 */
enum class EstadoTarea(val etiqueta: String) {
    PENDIENTE("Pendiente"),
    VALIDADA("Validada"),
    VENCIDA("Vencida")
}
