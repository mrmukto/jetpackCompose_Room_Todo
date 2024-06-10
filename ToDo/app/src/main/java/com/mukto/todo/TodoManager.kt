package com.mukto.todo

import android.os.Build
import androidx.annotation.RequiresApi
import com.mukto.todo.db.Todo
import java.time.Instant
import java.util.Date

object TodoManager {
   private val todoList = mutableListOf<Todo>()
    fun getAllTodos(): List<Todo> {
    return todoList
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addTodo(tittle:String, description: String) {
     todoList.add(Todo(System.currentTimeMillis().toInt(),tittle,description, Date.from(Instant.now())))
    }

    fun deleteTodo(id: Int) {
      todoList.removeIf { it.id == id }

    }
}