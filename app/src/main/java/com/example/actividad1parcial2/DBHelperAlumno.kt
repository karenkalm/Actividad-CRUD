package com.example.actividad1parcial2

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelperAlumno(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "Alumnos.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_ALUMNOS = "Alumnos"
        const val COLUMN_CUENTA = "cuenta"
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_CORREO = "correo"
        const val COLUMN_IMAGEN = "imagen"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_ALUMNOS (
                $COLUMN_CUENTA TEXT PRIMARY KEY,
                $COLUMN_NOMBRE TEXT NOT NULL,
                $COLUMN_CORREO TEXT NOT NULL,
                $COLUMN_IMAGEN TEXT
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ALUMNOS")
        onCreate(db)
    }

    /**
     * Inserta un nuevo alumno en la base de datos.
     */
    fun insertAlumno(alumno: Alumno): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CUENTA, alumno.cuenta)
            put(COLUMN_NOMBRE, alumno.nombre)
            put(COLUMN_CORREO, alumno.correo)
            put(COLUMN_IMAGEN, alumno.imagen)
        }
        val result = db.insert(TABLE_ALUMNOS, null, values)
        db.close()
        return result
    }

    /**
     * Obtiene la lista de todos los alumnos guardados.
     */
    fun getAllAlumnos(): List<Alumno> {
        val alumnosList = mutableListOf<Alumno>()
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_ALUMNOS", null)

        if (cursor.moveToFirst()) {
            val idxCuenta = cursor.getColumnIndex(COLUMN_CUENTA)
            val idxNombre = cursor.getColumnIndex(COLUMN_NOMBRE)
            val idxCorreo = cursor.getColumnIndex(COLUMN_CORREO)
            val idxImagen = cursor.getColumnIndex(COLUMN_IMAGEN)

            do {
                val cuenta = if (idxCuenta != -1) cursor.getString(idxCuenta) else ""
                val nombre = if (idxNombre != -1) cursor.getString(idxNombre) else ""
                val correo = if (idxCorreo != -1) cursor.getString(idxCorreo) else ""
                val imagen = if (idxImagen != -1) cursor.getString(idxImagen) else ""

                alumnosList.add(Alumno(nombre = nombre, cuenta = cuenta, correo = correo, imagen = imagen))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return alumnosList
    }

    /**
     * Actualiza la información de un alumno en la base de datos.
     */
    fun updateAlumno(alumno: Alumno, cuentaOriginal: String = alumno.cuenta): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CUENTA, alumno.cuenta)
            put(COLUMN_NOMBRE, alumno.nombre)
            put(COLUMN_CORREO, alumno.correo)
            put(COLUMN_IMAGEN, alumno.imagen)
        }
        val rowsAffected = db.update(TABLE_ALUMNOS, values, "$COLUMN_CUENTA = ?", arrayOf(cuentaOriginal))
        db.close()
        return rowsAffected
    }

    /**
     * Elimina un alumno según su número de cuenta.
     */
    fun deleteAlumno(cuenta: String): Int {
        val db = writableDatabase
        val rowsDeleted = db.delete(TABLE_ALUMNOS, "$COLUMN_CUENTA = ?", arrayOf(cuenta))
        db.close()
        return rowsDeleted
    }
}
