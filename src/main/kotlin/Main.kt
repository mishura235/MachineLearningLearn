package org.example

fun main() {
    val model = NeuralNetwork(3)
    val inputs = mutableListOf<Double>(0.0, 0.0)
    val output = mutableListOf<Double>(0.0)
    var out = model.fit(inputs)
    var error = output[0] - out[0]
    while (error > 0.1 || error < -0.1){
        model.findError(error)
        model.backward()
        out = model.fit(inputs)
        error =  out[0]-output[0]
        println(error)
        println(out)
    }
}