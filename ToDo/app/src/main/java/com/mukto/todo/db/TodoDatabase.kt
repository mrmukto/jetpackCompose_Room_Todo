package com.mukto.todo.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Todo::class, Expense::class], version = 3)
@TypeConverters(Converters::class)
abstract class TodoDatabase : RoomDatabase() {

    companion object {
        const val DATABASE_NAME = "todo_database"
    }

    abstract fun getTodoDao(): TodoDao
    abstract fun getExpenseDao(): ExpenseDao
}
