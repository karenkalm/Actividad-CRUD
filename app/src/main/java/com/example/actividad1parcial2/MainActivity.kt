package com.example.actividad1parcial2

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.actividad1parcial2.databinding.ActivityMainBinding
import com.example.actividad1parcial2.databinding.DialogAlumnoBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DBHelperAlumno
    private lateinit var adapter: AlumnoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DBHelperAlumno(this)

        // Insertar datos de prueba iniciales si la BD está vacía
        insertarDatosInicialesSiEsNecesario()

        setupRecyclerView()

        binding.fabAgregar.setOnClickListener {
            mostrarDialogoAlumno(null)
        }
    }

    private fun setupRecyclerView() {
        adapter = AlumnoAdapter(
            listaAlumnos = emptyList(),
            onEditClick = { alumno -> mostrarDialogoAlumno(alumno) },
            onDeleteClick = { alumno -> confirmarEliminacion(alumno) }
        )
        binding.rvAlumnos.layoutManager = LinearLayoutManager(this)
        binding.rvAlumnos.adapter = adapter

        cargarListaAlumnos()
    }

    private fun cargarListaAlumnos() {
        val lista = dbHelper.getAllAlumnos()
        adapter.updateLista(lista)

        if (lista.isEmpty()) {
            binding.tvSinAlumnos.visibility = View.VISIBLE
            binding.rvAlumnos.visibility = View.GONE
        } else {
            binding.tvSinAlumnos.visibility = View.GONE
            binding.rvAlumnos.visibility = View.VISIBLE
        }
    }

    private fun mostrarDialogoAlumno(alumnoAEditar: Alumno?) {
        val dialogBinding = DialogAlumnoBinding.inflate(layoutInflater)
        val esEdicion = alumnoAEditar != null

        if (esEdicion) {
            dialogBinding.etNombre.setText(alumnoAEditar.nombre)
            dialogBinding.etCuenta.setText(alumnoAEditar.cuenta)
            dialogBinding.etCuenta.isEnabled = false // Número de cuenta actúa como clave
            dialogBinding.etCorreo.setText(alumnoAEditar.correo)
            dialogBinding.etImagen.setText(alumnoAEditar.imagen)
        }

        val titulo = if (esEdicion) "Editar Alumno" else "Agregar Alumno"

        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setView(dialogBinding.root)
            .setPositiveButton("Guardar") { dialog, _ ->
                val nombre = dialogBinding.etNombre.text.toString().trim()
                val cuenta = dialogBinding.etCuenta.text.toString().trim()
                val correo = dialogBinding.etCorreo.text.toString().trim()
                val imagen = dialogBinding.etImagen.text.toString().trim()

                if (nombre.isEmpty() || cuenta.isEmpty() || correo.isEmpty()) {
                    Toast.makeText(this, "Por favor llena los campos obligatorios", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val alumno = Alumno(
                    nombre = nombre,
                    cuenta = cuenta,
                    correo = correo,
                    imagen = imagen
                )

                if (esEdicion) {
                    val res = dbHelper.updateAlumno(alumno)
                    if (res > 0) {
                        Toast.makeText(this, "Alumno actualizado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Error al actualizar alumno", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val res = dbHelper.insertAlumno(alumno)
                    if (res != -1L) {
                        Toast.makeText(this, "Alumno registrado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Error o número de cuenta ya registrado", Toast.LENGTH_SHORT).show()
                    }
                }

                cargarListaAlumnos()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun confirmarEliminacion(alumno: Alumno) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Alumno")
            .setMessage("¿Estás seguro de que deseas eliminar a ${alumno.nombre}?")
            .setPositiveButton("Eliminar") { dialog, _ ->
                val res = dbHelper.deleteAlumno(alumno.cuenta)
                if (res > 0) {
                    Toast.makeText(this, "Alumno eliminado", Toast.LENGTH_SHORT).show()
                    cargarListaAlumnos()
                } else {
                    Toast.makeText(this, "Error al eliminar alumno", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun insertarDatosInicialesSiEsNecesario() {
        val lista = dbHelper.getAllAlumnos()
        if (lista.isEmpty()) {
            dbHelper.insertAlumno(
                Alumno(
                    nombre = "Carlos Eduardo Mendoza",
                    cuenta = "20201001234",
                    correo = "carlos.mendoza@unah.hn",
                    imagen = "https://picsum.photos/id/1005/200/200"
                )
            )
            dbHelper.insertAlumno(
                Alumno(
                    nombre = "María Fernanda Gómez",
                    cuenta = "20211005678",
                    correo = "maria.gomez@unah.hn",
                    imagen = "https://picsum.photos/id/1027/200/200"
                )
            )
            dbHelper.insertAlumno(
                Alumno(
                    nombre = "José Luis Rodríguez",
                    cuenta = "20191009876",
                    correo = "jose.rodriguez@unah.hn",
                    imagen = "https://picsum.photos/id/1012/200/200"
                )
            )
        }
    }
}
