package com.example.cruditemapp.data.repository

import com.example.cruditemapp.data.model.Task
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class TaskRepository {
    private val db = Firebase.firestore // instancia do firestore
    suspend fun addTask(task: Task) { // adiciona a nova task
        db.collection("tasks").add(task).await()
    }
}