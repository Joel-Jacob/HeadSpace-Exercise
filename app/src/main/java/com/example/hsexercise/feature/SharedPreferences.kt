package com.example.hsexercise.feature

import android.content.Context
import android.util.Log

class SharedPreferences(val context: Context) {
    private val PREFS_NAME = "PAGES"
    val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)


    fun getPageSharedPref(KEY_NAME:String):Int{
        return sharedPref.getInt(KEY_NAME, 1)
    }

    fun savePageSharedPref(KEY_NAME:String, value:Int){
        val editor = sharedPref.edit()
        editor.putInt(KEY_NAME, value)
        editor.apply()
    }
}