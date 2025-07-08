package com.example.cruditemapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cruditemapp.data.model.Task
import com.example.cruditemapp.ui.viewmodel.TaskViewModel

@Composable
fun TaskScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = viewModel()
) {
    val tasks by viewModel.tasks // sempre que o "tasks" mudar, a  ui atualiza

    // cria titulo e descrição
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<Task?>(null) } // tarefa escolhida para edicao

    Column(modifier = modifier.padding(16.dp)) {
        OutlinedTextField(
            value = title, // titulo da tarefa
            onValueChange = { title = it },
            label = { Text("Título da Tarefa") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = description, // descrição da tarefa
            onValueChange = { description = it },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (title.isNotEmpty() && description.isNotEmpty()) { // verifica se os campos estao vazios
                    viewModel.addTask(Task(title = title, description = description))
                    title = ""
                    description = ""
                } // limpa os campos depois de adicionar
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Adicionar Tarefa")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(tasks) { task ->
                TaskCard(
                    task = task,
                    onDelete = { viewModel.deleteTask(task.id!!) },
                    onUpdate = {
                        selectedTask = task // seleciona a tarefa para edição
                        showDialog = true // abre a caixinha de dialogo
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { // força o crash e manda para o firebase
                throw RuntimeException("Testando Crashlytics")
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Forçar Crash")
        }
    }
    if (showDialog) {
        UpdateTaskDialog(
            task = selectedTask,
            onDismiss = { showDialog = false },
            onUpdate = { updatedTask ->
                viewModel.updateTask(updatedTask)
                showDialog = false
            }
        )
    }
}

@Composable
fun TaskCard(
    task: Task,
    onDelete: () -> Unit,
    onUpdate: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(text = task.title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = task.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onDelete) {
                    Text("Apagar")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onUpdate) {
                    Text("Editar")
                }
            }
        }
    }
}
@Composable
fun UpdateTaskDialog(
    task: Task?,
    onDismiss: () -> Unit,
    onUpdate: (Task) -> Unit
) {
    if (task == null) return
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Tarefa") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição") },
                )
            }
        },
        confirmButton = {
            Button(onClick = { // botao de salvar as alterações
                val updatedTask = task.copy(title = title, description = description)
                onUpdate(updatedTask)
            }) {
                Text("Salvar")
            }
        },
        dismissButton = { // cancelar edição
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}