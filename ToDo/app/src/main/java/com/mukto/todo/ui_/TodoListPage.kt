package com.mukto.todo.ui_

import android.app.Activity
import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mukto.todo.R
import com.mukto.todo.db.Todo
import com.mukto.todo.viewModel.TodoViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodoListPage(viewModel: TodoViewModel) {
    val todoList by viewModel.todoList.observeAsState()
    var inputText by remember { mutableStateOf("") }
    var inputTextDescription by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedTodo by remember { mutableStateOf<Todo?>(null) }
    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler {
        showExitDialog = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "Todo List",
                fontSize = 22.sp
            )
            todoList?.let { list ->
                if (list.isEmpty()) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = "No items yet",
                        fontSize = 16.sp
                    )
                } else {
                    LazyColumn(content = {
                        itemsIndexed(list) { index: Int, item: Todo ->
                            TodoItem(
                                item = item,
                                onDelete = { viewModel.deleteTodo(item.id) },
                                onUpdate = {
                                    selectedTodo = item
                                    inputText = item.title
                                    inputTextDescription = item.description
                                    showDialog = true
                                }
                            )
                        }
                    })
                }
            }
        }

        FloatingActionButton(
            onClick = {
                selectedTodo = null
                inputText = ""
                inputTextDescription = ""
                showDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Todo")
        }

        if (showDialog) {
            var titleError by rememberSaveable { mutableStateOf<String?>(null) }
            var descriptionError by rememberSaveable { mutableStateOf<String?>(null) }

            titleError = when {
                inputText.isBlank() -> "Please enter Title name."
                inputText.length < 2 -> "Title name is too short."
                inputText.length > 20 -> "Title name is too long."
                else -> null
            }
            descriptionError = when {
                inputTextDescription.isBlank() -> "Please enter Description."
                inputTextDescription.length < 2 -> "Description is too short."
                inputTextDescription.length > 120 -> "Description is too long."
                else -> null
            }

            AlertDialog(
                onDismissRequest = { },
                title = { Text(text = if (selectedTodo == null) "Add Todo" else "Update Todo") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            label = { Text("Title") },
                            supportingText = { Text(text = titleError.orEmpty()) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = inputTextDescription,
                            onValueChange = { inputTextDescription = it },
                            label = { Text("Description") },
                            supportingText = { Text(text = descriptionError.orEmpty()) }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        enabled = titleError == null && descriptionError == null,
                        onClick = {
                            if (selectedTodo == null) {
                                viewModel.addTodo(
                                    title = inputText,
                                    description = inputTextDescription
                                )
                            } else {
                                viewModel.updateTodo(
                                    selectedTodo!!.copy(
                                        title = inputText,
                                        description = inputTextDescription
                                    )
                                )
                            }
                            showDialog = false
                            inputText = ""
                            inputTextDescription = ""
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showDialog = false
                            inputText = ""
                            inputTextDescription = ""
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { showExitDialog = false },
                title = { Text("Exit App") },
                text = { Text("Are you sure you want to exit?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showExitDialog = false
                            (LocalContext as Activity).finish()
                        }
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showExitDialog = false }
                    ) {
                        Text("No")
                    }
                }
            )
        }
    }
}


@Composable
fun TodoItem(item: Todo, onDelete: () -> Unit, onUpdate: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.DarkGray)
            .padding(16.dp)
            .clickable { onUpdate() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = SimpleDateFormat(
                    "EEE MMM dd, hh:mm:ss a, yyyy", java.util.Locale.ENGLISH
                ).format(item.createdAt),
                color = Color.LightGray,
                fontSize = 12.sp
            )
            Text(
                text = item.title,
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = item.description,
                fontSize = 14.sp,
                color = Color.Yellow
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                painter = painterResource(id = R.drawable.ic_delete),
                contentDescription = "Delete",
                tint = Color.Red
            )
        }
    }
}
