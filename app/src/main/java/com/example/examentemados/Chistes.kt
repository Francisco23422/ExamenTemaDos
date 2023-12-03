package com.example.examentemados

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import com.example.examentemados.databinding.ActivityChistesBinding
import java.util.Locale

class Chistes : AppCompatActivity() {

    // Referencia al binding
    private lateinit var binding: ActivityChistesBinding

    // Objeto para la síntesis de voz
    private lateinit var textToSpeech: TextToSpeech

    // Duración máxima entre dos toques para considerarlos como un doble toque
    private val TOUCH_MAX_TIME = 500

    // Tiempo del último toque
    private var touchLastTime: Long = 0

    // Objeto Handler para manejar operaciones en el hilo principal
    private lateinit var handler: Handler

    // Lista de chistes cortos y largos obtenidos desde ChistesRepository
    private val chistesCortos = ChistesRepository.chistesCortos
    private val chistesLargos = ChistesRepository.chistesLargos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialización del objeto de View Binding
        binding = ActivityChistesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicialización y visualización de la barra de progreso
        progressBar()

        // Configuración del TextToSpeech
        configureTextToSpeech()

        // Inicialización del Handler y eventos
        initHandler()
        initEvent()
    }

    /**
     * Muestra el progress bar durante 3s
     */
    private fun progressBar() {
        val loading = LoadingDialog(this)
        loading.startLoading()
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                loading.dismiss()
            }
        }, 3000)
    }

    /**
     * Inicializa el Handler y programa la aparición de botones después de 1 segundo.
     */
    private fun initHandler() {
        handler = Handler(Looper.getMainLooper())
        binding.btnCorto.visibility = View.GONE
        binding.btnLargo.visibility = View.GONE

        handler.postDelayed({
            val description = getString(R.string.describe)
            speakMeDescription(description)
            binding.btnCorto.visibility = View.VISIBLE
            binding.btnLargo.visibility = View.VISIBLE
        }, 1000)
    }

    /**
     * Configura el objeto TextToSpeech.
     */
    private fun configureTextToSpeech() {
        textToSpeech = TextToSpeech(applicationContext, TextToSpeech.OnInitListener {
            if (it != TextToSpeech.ERROR) {
                textToSpeech.language = Locale.getDefault()
            }
        })
    }

    /**
     * Inicializa los eventos de los botones.
     */
    private fun initEvent() {
        binding.btnCorto.setOnClickListener {
            handleButtonClick(chistesCortos.random(), "Botón para contar chiste corto")
        }
        binding.btnSalir.setOnClickListener {
            val intent = Intent(this, MenuPrincipal::class.java)
            startActivity(intent)
        }
        binding.btnLargo.setOnClickListener {
            handleButtonClick(chistesLargos.random(), "Botón para contar chiste largo")
        }
    }

    /**
     * Maneja el clic en el botón, describiendo o ejecutando el chiste dependiendo de la situación.
     */
    private fun handleButtonClick(chiste: String, description: String) {
        val currentTime = System.currentTimeMillis()

        if (currentTime - touchLastTime < TOUCH_MAX_TIME) {
            // Segunda pulsación, ejecutar chiste
            executorDoubleTouch(chiste)
        } else {
            // Primera pulsación, describir el botón
            speakMeDescription(description)
        }

        touchLastTime = currentTime
    }

    /**
     * Utilizado para la descripción de objetos mediante síntesis de voz.
     */
    private fun speakMeDescription(s: String) {
        textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    /**
     * Ejecuta la síntesis de voz del chiste.
     */
    private fun executorDoubleTouch(chiste: String) {
        speakMeDescription(chiste)
    }

    /**
     * Se llama cuando la actividad está a punto de ser destruida.
     */
    override fun onDestroy() {
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroy()
    }
}
