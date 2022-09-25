package com.example.kinopoiskparser

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class FilmInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film_info)
        val filmName = intent.getStringExtra("name")
        var filmDesc = intent.getStringExtra("desc")
        if(filmDesc=="null"||filmDesc==null)
            filmDesc = "No description for this film "
        val imgUrl = intent.getStringExtra("imgUrl")
        val twName = findViewById<TextView>(R.id.filmInfoNameTW)
        val twDesc = findViewById<TextView>(R.id.textViewDescr)
        val iwPoster = findViewById<ImageView>(R.id.imageView2)
        Picasso.get()
            .load(imgUrl)
            .error(R.drawable.test)
            .into(iwPoster)
        twName.text = filmName
        twDesc.text = filmDesc
    }
}