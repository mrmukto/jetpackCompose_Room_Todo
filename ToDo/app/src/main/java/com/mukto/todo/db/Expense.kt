package com.mukto.todo.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Expense(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var amount: Double,
    var description: String,
    var date: Date,
    val category: String? = null// Optional: You can use this field to categorize expenses
)

