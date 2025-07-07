package com.example.cruditemapp.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.cruditemapp.data.model.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class TaskViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance() // instancia do firestore
    private var listenerRegistration: ListenerRegistration? = null // listener para ouvir as alterações
    var tasks = mutableStateOf<List<Task>>(emptyList())
        private set
    init {
        startListeningForTasks() // começar a ouvir as alterações nas taskas
    }

    private fun startListeningForTasks() {
        val tasksCollection = db.collection("tasks")// aponta a coleção de tasks no firestore
        listenerRegistration = tasksCollection.addSnapshotListener { snapshot, exception ->
            if (exception != null) { // pegando erros, tipo sem internet, etc
                Log.w("TaskViewModel", "Erro ao ouvir as tarefas.", exception)
                return@addSnapshotListener
            }
            if (snapshot != null) { // se tiver dados
                val fetchedTasks = snapshot.documents.map { document ->
                    document.toObject(Task::class.java)?.copy(id = document.id)
                }.filterNotNull()
                tasks.value = fetchedTasks  // atualiza a lista de tarefas
            }
        }
    }

    fun addTask(task: Task) { // CREATE
        db.collection("tasks").add(task)
            .addOnSuccessListener { Log.d("TaskViewModel", "Tarefa adicionada") }
            .addOnFailureListener { e -> Log.w("TaskViewModel", "Erro ao adicionar tarefa", e) }
    }

    fun deleteTask(taskId: String) { // DELETE
        db.collection("tasks").document(taskId).delete()
            .addOnSuccessListener { Log.d("TaskViewModel", "Tarefa deletada") }
            .addOnFailureListener { e -> Log.w("TaskViewModel", "Erro ao deletar", e) }
    }

    fun updateTask(task: Task) { // UPDATE
        if (task.id != null) {
            db.collection("tasks").document(task.id)
                .set(task)
                .addOnSuccessListener { Log.d("TaskViewModel", "Tarefa atualizada") }
                .addOnFailureListener { e -> Log.w("TaskViewModel", "Erro ao atualizar", e) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove() // remove o listener para evitar problemas de memória
        Log.d("TaskViewModel", "Listener removido.")
    }
}