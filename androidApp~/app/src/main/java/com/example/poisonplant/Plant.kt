package com.example.poisonplant

//simple class to hold the information about the plants in a bundled/concise manner

class Plant(
    private val commonName: String,
    private val scientificName: String,
    private val imagePath: Int,
    private val description: String
){
    fun getCommonName(): String { return commonName }
    fun getScientificName(): String { return scientificName }
    fun getImagePath(): Int { return imagePath }
    fun getDescription(): String { return description}
}
