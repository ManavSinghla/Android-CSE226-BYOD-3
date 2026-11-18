import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = NoteDatabase.getDatabase(this)
        val repository = NoteRepository(database.noteDao())

        val factory = NoteViewModelFactory(repository)

        setContent {
            MaterialTheme {
                val noteViewModel: NoteViewModel = viewModel(
                    factory = factory
                )

                NotesScreen(noteViewModel)
            }
        }
    }
}

@Composable
fun NotesScreen(viewModel: NoteViewModel) {

    val notes by viewModel.notes.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Notes") }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Note Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Note Content") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    viewModel.addNote(title, content)
                    title = ""
                    content = ""
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Note")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "All Notes (${notes.size})",
                style = MaterialTheme.typography.titleLarge
            )

            LazyColumn {
                items(
                    items = notes,
                    key = { it.id }
                ) { note ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = note.title,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(text = note.content)

                            Text(
                                text = SimpleDateFormat(
                                    "dd MMM yyyy, hh:mm a",
                                    Locale.getDefault()
                                ).format(note.createdAt),
                                style = MaterialTheme.typography.bodySmall
                            )

                            TextButton(
                                onClick = {
                                    viewModel.deleteNote(note)
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
