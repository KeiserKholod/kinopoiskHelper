package com.example.kinopoiskparser

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val buttonFindByName = findViewById<Button>(R.id.buttonFindByName)
        buttonFindByName.setOnClickListener {
            val findResultsActivityIntent = Intent(this, FindResultsActivity::class.java)
            val name = findViewById<EditText>(R.id.editTextTextFilmName).text.toString()
            val year = findViewById<EditText>(R.id.editTextTextFilmYear).text.toString()
            val isStrict = findViewById<CheckBox>(R.id.checkBoxIsStrict).isChecked
            findResultsActivityIntent.putExtra("ActivityType",ActivityType.findResults)
            findResultsActivityIntent.putExtra("name",name)
            findResultsActivityIntent.putExtra("year",year)
            findResultsActivityIntent.putExtra("isStrict",isStrict)
            startActivity(findResultsActivityIntent)
        }

        val buttonTopNFilms = findViewById<Button>(R.id.buttonTopFilms)
        buttonTopNFilms.setOnClickListener {
            val findResultsActivityIntent = Intent(this, FindResultsActivity::class.java)
            findResultsActivityIntent.putExtra("ActivityType",ActivityType.top2)
            startActivity(findResultsActivityIntent)
        }

        val buttonChoosenFilms = findViewById<Button>(R.id.choosenFilms)
        buttonChoosenFilms.setOnClickListener {
            val choosenActivityIntent = Intent(this,  FindResultsActivity::class.java)
            choosenActivityIntent.putExtra("ActivityType",ActivityType.chosen)
            startActivity(choosenActivityIntent)
        }
    }
}