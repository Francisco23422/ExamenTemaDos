package com.example.examentemados

import android.app.Activity
import android.app.AlertDialog

class LoadingDialog(val mActivity : Activity) {
    private lateinit var isDialog : AlertDialog
    fun startLoading(){
        val infalter = mActivity.layoutInflater
        val dialog = infalter.inflate(R.layout.activity_progress_bar,null)

        val bulider = AlertDialog.Builder(mActivity)
        bulider.setView(dialog)
        bulider.setCancelable(false)
        isDialog = bulider.create()
        isDialog.show()
    }
    fun dismiss(){
        isDialog.dismiss()
    }
}