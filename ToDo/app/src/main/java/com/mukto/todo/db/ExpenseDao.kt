package com.mukto.todo.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM Expense ORDER BY date DESC")
    fun getAllExpenses(): LiveData<List<Expense>>

    @Insert
    fun addExpense(expense: Expense)

    @Query("DELETE FROM Expense WHERE id = :id")
    fun deleteExpense(id: Int)

    @Update
    fun updateExpense(expense: Expense)

    @Query("SELECT SUM(amount) FROM Expense")
    fun getTotalCost(): LiveData<Double>

    @Query("SELECT SUM(amount) FROM Expense WHERE strftime('%Y-%m', date) = :monthYear")
    fun getMonthlyCost(monthYear: String): LiveData<Double>
}
