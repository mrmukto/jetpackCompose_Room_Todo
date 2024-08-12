package com.mukto.todo

import android.app.Application
import androidx.room.Room
import com.mukto.todo.db.TodoDatabase

class MainApplication : Application() {

    companion object {
        lateinit var todoDatabase: TodoDatabase
    }

    override fun onCreate() {
        super.onCreate()
        todoDatabase = Room.databaseBuilder(
            this,
            TodoDatabase::class.java,
            TodoDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // This will reset the database if no migration strategy is provided
            .build()
    }
}
