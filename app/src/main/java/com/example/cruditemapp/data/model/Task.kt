package com.example.cruditemapp.data.model

data class Task(
    val id : String = generatedId().toString(),
    val title : String = "",
    val description : String = "",
){
    companion object{
        private var idAtual = 0
        fun generatedId() : Int {
            idAtual++
            return idAtual
        }
    }
}