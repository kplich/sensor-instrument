package com.example.sensorinstrument



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