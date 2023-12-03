package com.example.examentemados

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.examentemados.databinding.ActivityDadosBinding
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class Dados : AppCompatActivity() {

    // Referencia a la vista generada mediante View Binding
    private lateinit var bindingMain: ActivityDadosBinding

    // Suma de los resultados de los dados
    private var sum: Int = 0

    // Objeto Handler para manejar operaciones en el hilo principal
    private lateinit var handler: Handler

    // Objeto para la síntesis de voz (TextToSpeech)
    private lateinit var textToSpeech: TextToSpeech

    // Indica si el TextToSpeech ha sido inicializado correctamente
    private var isTtsInitialized = false

    // Indica si hay un juego en progreso
    private var isGameInProgress = false

    // Saldo del jugador
    private var saldo = 100

    // Lista para almacenar los resultados de los dados
    private var resultadosDados: MutableList<Int> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialización del objeto de View Binding
        bindingMain = ActivityDadosBinding.inflate(layoutInflater)
        setContentView(bindingMain.root)

        // Inicialización de la barra de progreso y eventos iniciales
        progressBar()
        inicializarTextToSpeech()
        inicializarEventos()
        bindingMain.seekBarValue.text = bindingMain.seekBar.progress.toString()
    }

    /**
     * Muestra una barra de progreso durante 2 segundos antes de mostrar las normas del juego.
     */
    private fun progressBar(){
        val loading = LoadingDialog(this)
        loading.startLoading()
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                loading.dismiss()
                mostrarDialogoNormas()
            }
        }, 2000)
    }

    /**
     * Muestra un cuadro de diálogo con las normas del juego y opciones para continuar o salir.
     */
    private fun mostrarDialogoNormas() {
        val builder = AlertDialog.Builder(this)

        // Infla el diseño del cuadro de diálogo personalizado
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.dialogo_normas, null)

        builder.setView(view)

        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        // Configuración de eventos para los botones del cuadro de diálogo
        val btnSi = view.findViewById<Button>(R.id.btnSi)
        btnSi.setOnClickListener {
            dialog.dismiss()
        }

        val btnNo = view.findViewById<Button>(R.id.btnNo)
        btnNo.setOnClickListener {
            val intent = Intent(this, MenuPrincipal::class.java)
            startActivity(intent)
        }
    }

    /**
     * Inicializa el objeto TextToSpeech para la síntesis de voz.
     */
    private fun inicializarTextToSpeech() {
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsInitialized = true
            }
        }
    }

    /**
     * Inicializa los eventos asociados a los elementos de la interfaz de usuario.
     */
    private fun inicializarEventos() {
        // Configuración del evento de cambio en la barra de progreso
        bindingMain.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                bindingMain.seekBarValue.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // No es necesario implementar en este caso
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // No es necesario implementar en este caso
            }
        })

        // Configuración del evento para el botón de salir
        bindingMain.btnSalir.setOnClickListener() {
            mostrarDialogoSalida()
        }

        // Configuración del evento para el botón de iniciar juego
        bindingMain.btnStartGame.setOnClickListener {
            saldo -= 10
            bindingMain.txtSaldo.text = "Saldo: $saldo"
            bindingMain.txtResultado.setBackgroundResource(
                if (isGameInProgress) R.drawable.bordes_verdes else R.drawable.bordes_rojos
            )
            bindingMain.txtResultado.text = ""
            bindingMain.txtResultado.visibility = View.VISIBLE
            isGameInProgress = true
            jugar()
        }
    }

    /**
     * Inicia el juego llamando a la función programarLanzamientos.
     */
    private fun jugar() {
        programarLanzamientos()
    }

    /**
     * Programa los lanzamientos de los dados con un retraso en el tiempo.
     */
    private fun programarLanzamientos() {
        val schedulerExecutor = Executors.newSingleThreadScheduledExecutor()
        val msc = 1000

        for (i in 1..bindingMain.seekBar.progress) {
            schedulerExecutor.schedule(
                {
                    lanzarDadoEnTiempo()
                },
                msc * i.toLong(), TimeUnit.MILLISECONDS
            )
        }

        schedulerExecutor.schedule({
            mostrarResultado()
        },
            msc * (bindingMain.seekBar.progress + 1).toLong(), TimeUnit.MILLISECONDS)

        schedulerExecutor.shutdown()
    }

    /**
     * Realiza el lanzamiento de los dados y muestra las imágenes correspondientes.
     */
    private fun lanzarDadoEnTiempo() {
        var numDados = Array(3) { kotlin.random.Random.nextInt(1, 7) }
        val imagViews: Array<ImageView> = arrayOf(
            bindingMain.imagviewDado1,
            bindingMain.imagviewDado2,
            bindingMain.imagviewDado3
        )

        sum = numDados.sum()

        for (i in 0..2)
            seleccionarVista(imagViews[i], numDados[i])

        resultadosDados.clear()
        resultadosDados.addAll(numDados)
    }

    /**
     * Asocia las imágenes correspondientes a los valores de los dados en las vistas.
     */
    private fun seleccionarVista(imgV: ImageView, v: Int) {
        when (v) {
            1 -> imgV.setImageResource(R.drawable.dado1)
            2 -> imgV.setImageResource(R.drawable.dado2)
            3 -> imgV.setImageResource(R.drawable.dado3)
            4 -> imgV.setImageResource(R.drawable.dado4)
            5 -> imgV.setImageResource(R.drawable.dado5)
            6 -> imgV.setImageResource(R.drawable.dado6)
        }
    }

    /**
     * Muestra el resultado del juego, actualiza el saldo y llama a la función iniciarHandler.
     */
    private fun mostrarResultado() {
        bindingMain.txtResultado.text = sum.toString()
        println(sum)
        bindingMain.txtResultado.setBackgroundResource(
            if (isGameInProgress) R.drawable.bordes_verdes else R.drawable.bordes_rojos
        )
        iniciarHandler()
        isGameInProgress = false

        if (resultadosDados[0] == resultadosDados[1] && resultadosDados[1] == resultadosDados[2]) {
            saldo += 200
        } else if (resultadosDados[0] == resultadosDados[1] || resultadosDados[1] == resultadosDados[2] || resultadosDados[0] == resultadosDados[2]) {
            saldo += 100
        } else {
            saldo += 0
        }

        bindingMain.txtSaldo.text = "Saldo: $saldo"
    }

    /**
     * Inicia el objeto Handler para ejecutar operaciones en el hilo principal.
     */
    private fun iniciarHandler() {
        handler = Handler(Looper.getMainLooper())

        // Inicia un nuevo hilo para realizar operaciones en segundo plano
        Thread {
            handler.post {
                if (isTtsInitialized) {
                    val descripcion = "El resultado es $sum"
                    hablar(descripcion)
                    Thread.sleep(2000)
                }
            }
        }.start()
    }

    /**
     * Utiliza el TextToSpeech para sintetizar y hablar el texto proporcionado.
     */
    private fun hablar(s: String) {
        textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    /**
     * Muestra un cuadro de diálogo de confirmación para salir del juego.
     */
    private fun mostrarDialogoSalida() {
        val builder = AlertDialog.Builder(this, R.style.DialogBasicCustomStyle)
        builder.setTitle("Salir del juego")
            .setMessage("Si sales se borrará tu saldo actual, ¿Estás seguro de que quieres salir?")
            .setPositiveButton("Sí") { dialog, which ->
                val intent = Intent(this, MenuPrincipal::class.java)
                startActivity(intent)
            }
            .setNegativeButton(android.R.string.cancel) { dialog, which ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }
}
