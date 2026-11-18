package com.example.filmcollection

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                MovieScreen()
            }
        }
    }
}

@Composable
fun MovieScreen() {
    val context = LocalContext.current
    val db = remember {
        MovieDatabaseHelper(context.applicationContext)
    }
    var movies by remember {
        mutableStateOf(db.getAllMovies())
    }

    var title by remember {
        mutableStateOf("")
    }
    var genre by remember {
        mutableStateOf("")
    }
    var rating by remember {
        mutableIntStateOf(0)
    }
    var editingId by remember {
        mutableStateOf<Int?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp)
    ) {
        Text("Movie Collection", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            singleLine = true
        )
        OutlinedTextField(
            value = genre,
            onValueChange = { genre = it },
            label = { Text("Genre") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            singleLine = true
        )
        OutlinedTextField(
            value = rating.toString(),
            onValueChange = { rating = it.toIntOrNull() ?: 0 },
            label = { Text("Rating (1-10)") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            singleLine = true
        )
        Button(
            enabled = title.isNotBlank() && genre.isNotBlank() && rating in 1..10,
            onClick = {
                if (editingId == null) {
                    db.addMovie(Movie(0, title.trim(), genre.trim(), rating))
                } else {
                    db.updateMovie(Movie(editingId!!, title.trim(), genre.trim(), rating))
                    editingId = null
                }
                movies = db.getAllMovies()
                title = ""
                genre = ""
                rating = 0
                editingId = null
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (editingId == null) "Add" else "Update")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Filmek (${movies.size} db)", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (movies.isEmpty()) {
                item {
                    Text("Nincsenek filmek az adatbázisban.")
                }
            } else {
                items(movies, key = { it.id }) { movie ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(text = movie.title, style = MaterialTheme.typography.titleMedium)
                            Text(text = movie.genre)
                            Text(text = "Rating: ${movie.rating} / 10")
                            Row {
                                TextButton(
                                    onClick = {
                                        editingId = movie.id
                                        title = movie.title
                                        genre = movie.genre
                                        rating = movie.rating
                                    }
                                ) {
                                    Text("Edit")
                                }
                                TextButton(
                                    onClick = {
                                        db.deleteMovie(movie.id)
                                        movies = db.getAllMovies()
                                    }
                                ) {
                                    Text("Delete")
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}
