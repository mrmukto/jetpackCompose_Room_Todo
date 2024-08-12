 package com.mukto.todo

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.mukto.todo.ui.theme.ToDoTheme
import com.mukto.todo.ui_.ExpenseListPage
import com.mukto.todo.ui_.TodoListPage
import com.mukto.todo.viewModel.ExpenseViewModel
import com.mukto.todo.viewModel.TodoViewModel

 class MainActivity : ComponentActivity() {
     @RequiresApi(Build.VERSION_CODES.O)
     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)

         // Initialize ViewModels
         val todoListViewModel = ViewModelProvider(this)[TodoViewModel::class.java]
         val expenseViewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]

         setContent {
             ToDoTheme {
                 Surface(
                     modifier = Modifier.fillMaxSize(),
                     color = MaterialTheme.colorScheme.background
                 ) {
                     var selectedPage by remember { mutableStateOf("todo") }

                     Column {
                         Row(
                             modifier = Modifier
                                 .fillMaxWidth()
                                 .padding(16.dp),
                             horizontalArrangement = Arrangement.SpaceAround
                         ) {
                             Button(
                                 onClick = { selectedPage = "todo" },
                                 colors = ButtonDefaults.buttonColors(
                                     containerColor = if (selectedPage == "todo") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                     contentColor = if (selectedPage == "todo") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                 )
                             ) {
                                 Text(text = "Todo List")
                             }
                             Button(
                                 onClick = { selectedPage = "expense" },
                                 colors = ButtonDefaults.buttonColors(
                                     containerColor = if (selectedPage == "expense") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                     contentColor = if (selectedPage == "expense") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                 )
                             ) {
                                 Text(text = "Expenses")
                             }
                         }

                         when (selectedPage) {
                             "todo" -> TodoListPage(todoListViewModel)
                             "expense" -> ExpenseListPage(expenseViewModel)
                         }
                     }
                 }
             }
         }
     }
 }

