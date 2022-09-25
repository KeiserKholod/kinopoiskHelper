package com.example.kinopoiskparser

import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.kinopoiskparser.data.FilmsDbHelper
import com.example.kinopoiskparser.data.FilmsStorageContract
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.net.HttpURLConnection
import java.net.URL


class FindResultsActivity : AppCompatActivity() {

    private val filmsDbHelper: FilmsDbHelper = FilmsDbHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_results)

        val activityType = intent.getSerializableExtra("ActivityType") as ActivityType;
        var title = ""
        var films: ArrayList<Film> = arrayListOf()

        if (activityType == ActivityType.chosen) {
            title = getString(R.string.titleChosen)
            films = getFilmsFromDb()
        }
        if (activityType == ActivityType.findResults) {
            title = getString(R.string.titleFindResults)
            val name = intent.getStringExtra("name")
            val year = intent.getStringExtra("year")
            Log.e("kek", year + "");
            val isStrict = intent.getBooleanExtra("isStrict", false)
            var strict = "false"
            if (isStrict)
                strict = "true"
            var request =
                "https://api.kinopoisk.dev/movie?limit=2&field=name&search=$name&isStrict=$strict&token=ZQQ8GMN-TN54SGK-NB3MKEC-ZKB8V06"
            if (year != "")
                request += "&field=year&search=$year"
            var tmp: ArrayList<Film>? = null
            if (!((name == null || name == "null" || name == "") && (year == null || year == "null" || year == "")))
                tmp =
                    getSearchResult(request)
            if (tmp != null)
                films = tmp
        }
        if (activityType == ActivityType.top2) {
            title = getString(R.string.titleFindResults)
            val tmp =
                getSearchResult("https://api.kinopoisk.dev/movie?limit=2&field=rating.kp&search=8-10&field=year&search=1990-2020&sortField=rating.kp&sortType=-1&field=typeNumber&search=1&token=ZQQ8GMN-TN54SGK-NB3MKEC-ZKB8V06")
            if (tmp != null)
                films = tmp
        }
        val lableView = findViewById<TextView>(R.id.searchResultLabel)
        lableView.text = title
        val recyclerView: RecyclerView = findViewById(R.id.recViewFilms)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CustomRecyclerAdapter(films, activityType, this, savedInstanceState)
    }

    private fun getSearchResult(Url: String): ArrayList<Film>? {
        val data: ArrayList<Film>?
        var responseJSON = ""
        val x = object : Thread() {
            override fun run() {
                println("running from Thread: ${Thread.currentThread()}")
                responseJSON =
                    sendGet(Url)

            }
        }
        x.start()
        x.join()

        data = JSONToFilmsArr(responseJSON, this)
        return data;
    }

    private fun getImage(URI: String): Bitmap? {
        var image: Bitmap? = null

        var t = object : com.squareup.picasso.Target {
            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {

            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                image = bitmap
            }
        }
        Picasso.get()
            .load(URI)
            .error(R.drawable.test)
            .into(t)
        return image
    }

    private fun JSONToFilmsArr(jsonStr: String, context: AppCompatActivity): ArrayList<Film>? {
        Log.e("cum", jsonStr.length.toString())
        Log.e("cum", jsonStr)
        var docs = (JSONTokener(jsonStr).nextValue() as JSONObject).getString("docs")
        val jsonArray = JSONTokener(docs).nextValue() as JSONArray

        val data = ArrayList<Film>()
        for (i in 0 until jsonArray.length()) {
            val posterJson = jsonArray.getJSONObject(i).getString("poster")
            val posterUrl =
                (JSONTokener(posterJson).nextValue() as JSONObject).optString("previewUrl")
            val name = jsonArray.getJSONObject(i).getString("name")
            val year = jsonArray.getJSONObject(i).optString("year")
            year.replace("null", "unknown")
            val rateObj = jsonArray.getJSONObject(i).optString("rating")
            val desc = jsonArray.getJSONObject(i).optString("description")
            desc.replace("null", "unknown")
            val rate = (JSONTokener(rateObj).nextValue() as JSONObject).optString("kp")
            rate.replace("null", "unknown")
            var image = getImage(posterUrl)
            data.add(
                Film(
                    name,
                    year,
                    rate,
                    desc,
                    image,
                    posterUrl
                )
            )
        }
        return data
    }

    private fun sendGet(URLReq: String): String {
        println("kholod")
        val urlTxt = URLReq
        val url = URL(urlTxt)
        val urlConnection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            doInput = true
        }
        urlConnection.connect()
        val inputStream = urlConnection.inputStream
        val answ = inputStream.bufferedReader().readText()
        return answ
    }

    private fun getFilmsFromDb(): ArrayList<Film> {
        val films = ArrayList<Film>()
        // Создадим и откроем для чтения базу данных
        val db: SQLiteDatabase = filmsDbHelper.readableDatabase

        // Зададим условие для выборки - список столбцов
        val projection = arrayOf(
            FilmsStorageContract.FilmStorage._ID,
            FilmsStorageContract.FilmStorage.COLUMN_NAME,
            FilmsStorageContract.FilmStorage.COLUMN_RATE,
            FilmsStorageContract.FilmStorage.COLUMN_YEAR,
            FilmsStorageContract.FilmStorage.COLUMN_DESC,
            FilmsStorageContract.FilmStorage.COLUMN_IMAGE_URL
        )

        // Делаем запрос
        val cursor: Cursor = db.query(
            FilmsStorageContract.FilmStorage.TABLE_NAME,  // таблица
            projection,  // столбцы
            null,  // столбцы для условия WHERE
            null,  // значения для условия WHERE
            null,  // Don't group the rows
            null,  // Don't filter by row groups
            null
        ) // порядок сортировки

        try {
            val a = FilmsStorageContract.FilmStorage._ID + " - " +
                    FilmsStorageContract.FilmStorage.COLUMN_NAME + " - " +
                    FilmsStorageContract.FilmStorage.COLUMN_RATE + " - " +
                    FilmsStorageContract.FilmStorage.COLUMN_YEAR + " - " +
                    FilmsStorageContract.FilmStorage.COLUMN_DESC + " - " +
                    FilmsStorageContract.FilmStorage.COLUMN_IMAGE_URL + "\n"
            // Узнаем индекс каждого столбца
            val idColumnIndex: Int = cursor.getColumnIndex(FilmsStorageContract.FilmStorage._ID)
            val nameColumnIndex: Int =
                cursor.getColumnIndex(FilmsStorageContract.FilmStorage.COLUMN_NAME)
            val rateColumnIndex: Int =
                cursor.getColumnIndex(FilmsStorageContract.FilmStorage.COLUMN_RATE)
            val yearColumnIndex: Int =
                cursor.getColumnIndex(FilmsStorageContract.FilmStorage.COLUMN_YEAR)
            val descColumnIndex: Int =
                cursor.getColumnIndex(FilmsStorageContract.FilmStorage.COLUMN_DESC)
            val imgUrlColumnIndex: Int =
                cursor.getColumnIndex(FilmsStorageContract.FilmStorage.COLUMN_IMAGE_URL)
            // Проходим через все ряды
            while (cursor.moveToNext()) {
                // Используем индекс для получения строки или числа
                val currentID: Int = cursor.getInt(idColumnIndex)
                val currentName: String = cursor.getString(nameColumnIndex)
                val currentRate: String = cursor.getString(rateColumnIndex)
                val currentYear: String = cursor.getString(yearColumnIndex)
                val currentdesc: String = cursor.getString(descColumnIndex)
                val currentUrl: String = cursor.getString(imgUrlColumnIndex)
                // Выводим значения каждого столбца
                val film =
                    Film(currentName, currentYear, currentRate, currentdesc, null, currentUrl)
                film.filmDbId = currentID.toString()
                films.add(film)
            }
        } finally {
            // Всегда закрываем курсор после чтения
            cursor.close()
        }
        return films
    }
}

class CustomRecyclerAdapter(
    private var films: ArrayList<Film>,
    private val activityType: ActivityType,
    val fr: FindResultsActivity,
    val savedInstanceState: Bundle?
) :
    RecyclerView.Adapter<CustomRecyclerAdapter.MyViewHolder>() {

    private val filmsDbHelper: FilmsDbHelper = FilmsDbHelper(fr)

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val filmName: TextView = itemView.findViewById(R.id.filmNameTextView)
        val filmRate: TextView = itemView.findViewById(R.id.filmGenreTextView)
        val filmYear: TextView = itemView.findViewById(R.id.filmTimeTextView)
        val filmImage: ImageView = itemView.findViewById(R.id.imageViewFilm)
        val bt: Button = itemView.findViewById(R.id.operationButton)
        var IV = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.res_view_element, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (films.size == 0)
            return
        holder.filmName.text = "Name: ${films[position].filmName}"
        holder.filmRate.text = "Rate kp: ${films[position].filmRate}"
        holder.filmYear.text = "Year: ${films[position].filmYear}"
        if (films[position].filmImage != null) {
            holder.filmImage.setImageBitmap(films[position].filmImage)

        } else
            Picasso.get()
                .load(films[position].filmImageURL)
                .error(R.drawable.test)
                .into(holder.filmImage)
        if (activityType == ActivityType.chosen)
            holder.bt.text = "delete"
        else
            holder.bt.text = "add"

        holder.IV.setOnClickListener {
            val FilmInfoActivityIntent = Intent(fr, FilmInfoActivity::class.java)
            FilmInfoActivityIntent.putExtra("name", films[position].filmName)
            FilmInfoActivityIntent.putExtra("year", films[position].filmYear)
            FilmInfoActivityIntent.putExtra("desc", films[position].filmDesc)
            FilmInfoActivityIntent.putExtra("imgUrl", films[position].filmImageURL)
            startActivity(fr, FilmInfoActivityIntent, savedInstanceState)
        }

        holder.bt.setOnClickListener {
            if (activityType == ActivityType.chosen) {
                delFilmDb(films[position])
                films.remove(films[position])
                this.notifyDataSetChanged()
            } else {
                addFilmDb(films[position])
            }
        }
    }

    private fun addFilmDb(film: Film) {
        // Gets the database in write mode
        val db: SQLiteDatabase = filmsDbHelper.writableDatabase
        // Создаем объект ContentValues, где имена столбцов ключи,
        // а информация о госте является значениями ключей
        val values = ContentValues()
        values.put(FilmsStorageContract.FilmStorage.COLUMN_NAME, film.filmName)
        values.put(FilmsStorageContract.FilmStorage.COLUMN_RATE, film.filmRate)
        values.put(FilmsStorageContract.FilmStorage.COLUMN_YEAR, film.filmYear)
        values.put(FilmsStorageContract.FilmStorage.COLUMN_DESC, film.filmDesc)
        values.put(FilmsStorageContract.FilmStorage.COLUMN_IMAGE_URL, film.filmImageURL)
        val newRowId = db.insert(FilmsStorageContract.FilmStorage.TABLE_NAME, null, values)
        Log.e("dbAdd", newRowId.toString())
    }

    private fun delFilmDb(film: Film) {
        // Gets the database in write mode
        val db: SQLiteDatabase = filmsDbHelper.writableDatabase
        db.delete(
            FilmsStorageContract.FilmStorage.TABLE_NAME, "_ID=?", arrayOf(film.filmDbId)
        )
    }

    override fun getItemCount(): Int {
        return films.size
    }
}
