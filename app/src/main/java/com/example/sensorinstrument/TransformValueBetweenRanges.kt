package com.example.sensorinstrument

val minDegreeBackward = -25.0
val maxDegreeForward = 15.0

val minColorValue = 63.0

fun transformValueBetweenRanges(
    value: Double,
    inputMin: Double, inputMax: Double,
    outputForMin: Double, outputForMax: Double
): Double {
    return when {
        value <= inputMin -> outputForMin
        value >= inputMax -> outputForMax
        else -> {
            val dY = outputForMax - outputForMin
            val dX = inputMax - inputMin
            val a = dY / dX

            val b = outputForMin - a * inputMin

            a * value + b
        }
    }
}