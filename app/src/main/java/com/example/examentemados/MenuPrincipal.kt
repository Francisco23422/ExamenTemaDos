package com.example.examentemados

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.AlarmClock
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.examentemados.databinding.ActivityMainBinding
import com.example.examentemados.databinding.ActivityMenuPrincipalBinding
import com.example.examentemados.databinding.DialogoUsuarioBinding

class MenuPrincipal : AppCompatActivity() {

    // Referencia a la vista generada mediante View Binding
    private lateinit var binding: ActivityMenuPrincipalBinding

    // Código de permisos para el uso de correo electrónico
    private val CODE_PERMISSIONS_EMAIL = 124

    // Código de permisos para el uso de navegación web
    private val WEB_PERMISSIONS_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialización del objeto de View Binding
        binding = ActivityMenuPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuración de la barra de progreso y eventos iniciales
        progressBar()
        initEvent()
    }

    /**
     * Inicializa los eventos asociados a los elementos de la interfaz de usuario.
     */
    private fun initEvent() {
        binding.btnCall.setOnClickListener { view ->
            val intent = Intent(this, ActivityCall::class.java)
            startActivity(intent)
        }

        binding.btnAlarm.setOnClickListener {
            configurateAlarm()
        }

        binding.btnUrl.setOnClickListener {
            openURL()
        }

        binding.btnEmail.setOnClickListener {
            sendEmail()
        }

        binding.btnChistes.setOnClickListener { view ->
            val intent = Intent(this, Chistes::class.java)
            startActivity(intent)
        }

        binding.btnDados.setOnClickListener { view ->
            val intent = Intent(this, Dados::class.java)
            startActivity(intent)
        }
    }

    /**
     * Configura una alarma para el horario de clases.
     */
    private fun configurateAlarm() {
        intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_MESSAGE, "DESPIERTA QUE EMPIEZAN LAS CLASES")
            putExtra(AlarmClock.EXTRA_HOUR, 6)
            putExtra(AlarmClock.EXTRA_MINUTES, 30)
        }
        startActivity(intent)
    }

    /**
     * Muestra una barra de progreso durante 2 segundos antes de mostrar el cuadro de diálogo de datos de usuario.
     */
    private fun progressBar() {
        val loading = LoadingDialog(this)
        loading.startLoading()
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                loading.dismiss()

                if (intent.getBooleanExtra("mostrarDialogo", false)) {
                    mostrarDialogoDatos()
                }
            }
        }, 2000)
    }

    /**
     * Abre una URL en un navegador web o solicita permisos de internet si aún no están otorgados.
     */
    private fun openURL() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.INTERNET
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET), WEB_PERMISSIONS_CODE)
        } else {
            val googleUri: Uri = Uri.parse("https://www.iesvirgendelcarmen.com/")
            val websiteIntent = Intent(Intent.ACTION_VIEW, googleUri)
            startActivity(websiteIntent)
        }
    }

    /**
     * Envía un correo electrónico a través de la aplicación de correo o solicita permisos de internet si aún no están otorgados.
     */
    private fun sendEmail() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.INTERNET
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.INTERNET),
                CODE_PERMISSIONS_EMAIL
            )
        } else {
            val recipientEmail = "23002401.consultaweb@g.educaand.es"
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:$recipientEmail")
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "NO SE PUEDE ABRIR GMAIL", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Muestra un cuadro de diálogo con los datos de usuario proporcionados anteriormente.
     */
    private fun mostrarDialogoDatos() {
        val builder = AlertDialog.Builder(this)

        // Utiliza la instancia de bindingDialogoUsuarioBinding para inflar el diálogo
        val dialogoUsuarioBinding = DialogoUsuarioBinding.inflate(layoutInflater)
        builder.setView(dialogoUsuarioBinding.root)

        // Configura los datos en las vistas del diálogo
        dialogoUsuarioBinding.txtNombre.text = "Nombre: " + intent.getStringExtra("nombre")
        dialogoUsuarioBinding.txtEdad.text = "Edad : " + intent.getStringExtra("edad")
        dialogoUsuarioBinding.txtGenero.text = "Genero : " + intent.getStringExtra("genero")
        dialogoUsuarioBinding.txtFechaNacimiento.text =
            "Fecha de nacimiento : " + intent.getStringExtra("fechaNacimiento")

        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        dialogoUsuarioBinding.btnSi.setOnClickListener {
            dialog.dismiss()
        }

        dialogoUsuarioBinding.btnNo.setOnClickListener {
            val intent = Intent(this, MenuInicio::class.java)
            startActivity(intent)
        }
    }
}
