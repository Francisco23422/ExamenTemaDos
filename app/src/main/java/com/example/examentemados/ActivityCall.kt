package com.example.examentemados

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
class ActivityCall : AppCompatActivity() {

    private lateinit var btnVolver: ImageButton
    private lateinit var btnCall : ImageButton
    private fun PermissionPhone(): Boolean = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)
        btnVolver = findViewById(R.id.btn_volver)
        btnCall = findViewById(R.id.btn_activity_call)
        btnVolver.setOnClickListener {
            val intent = Intent(this, MenuPrincipal::class.java)
            startActivity(intent)
        }
        btnCall.setOnClickListener {
            requestPermissions()
        }
    }



    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) {
            isGranted->
        if (isGranted) {
            call()
        }
        else {
            Toast.makeText( this, "Necesitas habilitar los permisos",
                Toast.LENGTH_LONG).show()
        }
    }

    companion object{
        const val PHONE = "953366942"
    }
    private fun requestPermissions(){
        if (Build.VERSION. SDK_INT >= Build.VERSION_CODES. M){
            if (PermissionPhone()){
                call()
            }
            else{
                requestPermissionLauncher.launch(Manifest.permission. CALL_PHONE)
            }
        }else{
            call()
        }
    }
    private fun call() {
        val intent = Intent(Intent. ACTION_CALL).apply {
            data = Uri.parse( "tel:$PHONE")
        }
        startActivity(intent)
    }
}
