package com.example.pizarraruralmovil.data

import android.util.AtomicFile
import android.util.Log
import com.example.pizarraruralmovil.model.Acudiente
import com.example.pizarraruralmovil.model.Curso
import com.example.pizarraruralmovil.model.Docente
import com.example.pizarraruralmovil.model.Estudiante
import com.example.pizarraruralmovil.model.Tarea
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.time.LocalDate
import org.json.JSONArray
import org.json.JSONObject

/**
 * Guarda los datos en un archivo JSON en la memoria interna del celular (carpeta privada de la app).
 * - Solo esta app puede leerlo, y no se sube a la copia de seguridad en la nube (res/xml).
 * - Las claves familiares y las contraseñas van como huella, nunca tal cual (ver Huella).
 * - AtomicFile escribe primero una copia nueva y luego la cambia por la vieja: si el celular se apaga
 *   en medio, queda la versión anterior completa y no un archivo dañado.
 */
class AlmacenEnArchivo(archivo: File) : Almacen {

    private val atomico = AtomicFile(archivo)

    override fun cargar(): DatosGuardados? = try {
        desdeJson(JSONObject(atomico.readFully().toString(Charsets.UTF_8)))
    } catch (sinArchivo: FileNotFoundException) {
        null // primera vez: todavía no hay nada guardado
    } catch (error: Exception) {
        Log.w(ETIQUETA, "No se pudieron leer los datos guardados; se usan los de prueba", error)
        null
    }

    override fun guardar(datos: DatosGuardados) {
        val salida = try {
            atomico.startWrite()
        } catch (error: IOException) {
            Log.w(ETIQUETA, "No se pudo abrir el archivo de datos", error)
            return
        }
        try {
            salida.write(aJson(datos).toString().toByteArray(Charsets.UTF_8))
            atomico.finishWrite(salida)
        } catch (error: IOException) {
            atomico.failWrite(salida)
            Log.w(ETIQUETA, "No se pudieron guardar los datos", error)
        }
    }

    companion object {
        /** Carpeta dentro de la memoria interna de la app; res/xml la deja fuera de la copia en la nube. */
        const val CARPETA = "datos"
        const val NOMBRE_ARCHIVO = "pizarra_escolar.json"

        /** El archivo de datos dentro de la carpeta privada de la app (context.filesDir). */
        fun archivoEn(carpetaPrivada: File): File = File(File(carpetaPrivada, CARPETA), NOMBRE_ARCHIVO)

        private const val VERSION = 1
        private const val ETIQUETA = "AlmacenEnArchivo"

        fun aJson(datos: DatosGuardados): JSONObject = JSONObject()
            .put("version", VERSION)
            .put("docentes", JSONArray(datos.docentes.map {
                JSONObject().put("id", it.id).put("nombre", it.nombre).put("correo", it.correo)
                    .put("huellaContrasena", it.huellaContrasena)
            }))
            .put("cursos", JSONArray(datos.cursos.map {
                JSONObject().put("id", it.id).put("docenteId", it.docenteId).put("grado", it.grado).put("nombre", it.nombre)
            }))
            .put("tareas", JSONArray(datos.tareas.map {
                JSONObject().put("id", it.id).put("cursoId", it.cursoId).put("texto", it.texto)
                    .put("fechaLimite", it.fechaLimite.toString()) // "2026-11-12"
            }))
            .put("acudientes", JSONArray(datos.acudientes.map {
                JSONObject().put("id", it.id).put("nombre", it.nombre).put("huellaClave", it.huellaClave)
            }))
            .put("estudiantes", JSONArray(datos.estudiantes.map {
                JSONObject().put("id", it.id).put("nombre", it.nombre).put("avatar", it.avatar)
                    .put("cursoId", it.cursoId).put("acudienteId", it.acudienteId)
                    .put("tareasValidadas", JSONArray(it.tareasValidadas.sorted()))
            }))
            .put("proximoIdEstudiante", datos.proximoIdEstudiante)
            .put("proximoIdAcudiente", datos.proximoIdAcudiente)
            .put("proximoIdTarea", datos.proximoIdTarea)

        fun desdeJson(json: JSONObject): DatosGuardados = DatosGuardados(
            docentes = json.getJSONArray("docentes").objetos {
                Docente(it.getInt("id"), it.getString("nombre"), it.getString("correo"), it.getString("huellaContrasena"))
            },
            cursos = json.getJSONArray("cursos").objetos {
                Curso(it.getInt("id"), it.getInt("docenteId"), it.getInt("grado"), it.getString("nombre"))
            },
            tareas = json.getJSONArray("tareas").objetos {
                Tarea(it.getInt("id"), it.getInt("cursoId"), it.getString("texto"), LocalDate.parse(it.getString("fechaLimite")))
            },
            acudientes = json.getJSONArray("acudientes").objetos {
                Acudiente(it.getInt("id"), it.getString("nombre"), it.getString("huellaClave"))
            },
            estudiantes = json.getJSONArray("estudiantes").objetos {
                val validadas = it.getJSONArray("tareasValidadas")
                Estudiante(
                    id = it.getInt("id"),
                    nombre = it.getString("nombre"),
                    avatar = it.getString("avatar"),
                    cursoId = it.getInt("cursoId"),
                    acudienteId = it.getInt("acudienteId"),
                    tareasValidadas = (0 until validadas.length()).map { i -> validadas.getInt(i) }.toSet()
                )
            },
            proximoIdEstudiante = json.getInt("proximoIdEstudiante"),
            proximoIdAcudiente = json.getInt("proximoIdAcudiente"),
            proximoIdTarea = json.getInt("proximoIdTarea")
        )

        private fun <T> JSONArray.objetos(convertir: (JSONObject) -> T): List<T> =
            (0 until length()).map { convertir(getJSONObject(it)) }
    }
}
