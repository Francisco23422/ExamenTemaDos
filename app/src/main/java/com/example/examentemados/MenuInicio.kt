package com.example.examentemados

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.examentemados.databinding.ActivityMenuInicioBinding
import java.text.SimpleDateFormat
import java.util.*

class MenuInicio : AppCompatActivity() {

    // Referencia a la vista generada mediante View Binding
    private lateinit var binding: ActivityMenuInicioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialización del objeto de View Binding
        binding = ActivityMenuInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicialización de eventos
        initEvent()
    }

    /**
     * Inicializa los eventos asociados a los elementos de la interfaz de usuario.
     */
    private fun initEvent() {
        // Configurar el Spinner
        val numeros = mutableListOf<String>()
        for (i in 13..100) {
            numeros.add(i.toString())
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, numeros)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter

        // Configurar el botón para mostrar el DatePickerDialog
        binding.buttonDatePicker.setOnClickListener {
            showDatePickerDialog()
        }

        // Configurar el botón de envío
        binding.btnEnviar.setOnClickListener {
            if (isFormValid()) {
                // Crear un Intent para pasar datos a la actividad MenuPrincipal
                val intent = Intent(this, MenuPrincipal::class.java)
                intent.putExtra("edad", binding.spinner.selectedItem.toString())
                intent.putExtra("nombre", binding.editTextNombre.text.toString())
                intent.putExtra("fechaNacimiento", binding.buttonDatePicker.text.toString())
                intent.putExtra("genero", getSelectedGender())
                intent.putExtra("mostrarDialogo", true)
                startActivity(intent)
            }
        }
    }

    /**
     * Muestra un DatePickerDialog para seleccionar la fecha de nacimiento.
     */
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = sdf.format(selectedDate.time)

                binding.buttonDatePicker.text = "$formattedDate"
            },
            year, month, day
        )

        datePickerDialog.show()
    }

    /**
     * Valida el formulario y muestra un mensaje de error si es necesario.
     */
    private fun isFormValid(): Boolean {
        if (binding.editTextNombre.text.isNullOrBlank()) {
            showToast("Por favor, ingresa tu nombre")
            return false
        }

        if (binding.buttonDatePicker.text == "FECHA DE NACIMIENTO") {
            showToast("Por favor, selecciona tu fecha de nacimiento")
            return false
        }

        if (!binding.checkBoxExtraOption.isChecked || !binding.checkBoxExtraOption1.isChecked) {
            showToast("Debes aceptar nuestras condiciones")
            return false
        }

        return true
    }

    /**
     * Muestra un mensaje Toast con el mensaje proporcionado.
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Obtiene el género seleccionado en el formulario.
     */
    private fun getSelectedGender(): String {
        return when (binding.radioGroup.checkedRadioButtonId) {
            R.id.radioButton1 -> "HOMBRE"
            R.id.radioButton2 -> "MUJER"
            else -> ""
        }
    }
}
