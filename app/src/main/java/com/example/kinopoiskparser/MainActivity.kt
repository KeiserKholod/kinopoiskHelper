package com.example.kinopoiskparser

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bt = findViewById<Button>(R.id.submitButton)
        bt.setOnClickListener {
            val menuActivityIntent = Intent(this, MenuActivity::class.java)
            val twErr = findViewById<TextView>(R.id.textViewError0)
            val login = findViewById<TextView>(R.id.editTextLogin).text.toString()
            val pass = findViewById<TextView>(R.id.editTextTextPass).text.toString()

            if (tryLogIn(login, pass))
                startActivity(menuActivityIntent)
            else
                twErr.visibility = TextView.VISIBLE
        }
    }

    private fun tryLogIn(login: String, pass: String): Boolean {

        return true
    }
}