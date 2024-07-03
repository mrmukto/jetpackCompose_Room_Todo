package com.mukto.todo.viewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukto.todo.MainApplication
import com.mukto.todo.db.Todo
import com.mukto.todo.db.TodoDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Date

class TodoViewModel : ViewModel() {

    val todoDao = MainApplication.todoDatabase.getTodoDao()
    val todoList: LiveData<List<Todo>> = todoDao.getAllTodo()



    @RequiresApi(Build.VERSION_CODES.O)
    fun addTodo(title: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.addTodo(Todo(title= title, description = description, createdAt = Date.from(Instant.now())))
        }

    }

    fun deleteTodo(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.deleteTodo(id)
        }
    }

    fun updateTodo(todo : Todo){
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.updateTodo(todo)
        }
    }

}