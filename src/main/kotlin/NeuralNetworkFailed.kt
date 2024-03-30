package org.example

import kotlin.math.exp
import kotlin.random.Random


// не работает
val n = 0.01

class Neuron{
    var error: Double = 0.0
    var weights = mutableListOf<Double>()
    fun fit(input:List<Double>): Double {
        val out:Double = input.mapIndexed{index, value -> calculate(index, value)}.sum()
        return 1/1+(exp(-1*out))
    }


    private fun calculate(index:Int,value:Double):Double{
        if (index >= weights.size){
            weights.add(Random.nextDouble())
        }
        return weights[index] * value +1
    }

    override fun toString(): String {
        return "Neuron=(${weights})"
    }
}

class Layer(val count:Int){
    val layer = mutableListOf<Neuron>().apply {
        repeat(count){ this.add(Neuron()) }
    }

    fun fit(inputs:List<Double>):List<Double>{
        val out = mutableListOf<Double>()
        layer.forEachIndexed{index,it  ->
            out.add(it.fit(inputs))
        }
        return out
    }

    fun findError(lastError: List<Double>): List<Double> {
        lastError.forEachIndexed { index, error ->
            layer[index].error += error
        }
        val out = mutableListOf<Double>()
        for (i in 0..<count){
            var temp = 0.0
            for (j in layer){
                if (i >=j.weights.size){
                    println("uups")
                    continue
                }
                temp += j.weights[i]*j.error
            }
            out.add(temp)
        }
        return out
    }

    fun backward() {
        layer.forEach { it.weights= it.weights.map { weight-> weight+it.error * n}.toMutableList() }
    }
    override fun toString(): String {
        return "Layer=(${layer})"
    }
}

class NeuralNetwork(val count:Int){
    val model = mutableListOf<Layer>().apply {
        repeat(count) {
            this.add(Layer(count))
        }
        this.add(Layer(1))
    }

    fun fit(inputs:List<Double>):List<Double>{
        val out = mutableListOf<Double>()
        var lastLayerOuts = inputs
        model.forEachIndexed{index,it  ->
            lastLayerOuts = it.fit(lastLayerOuts)
        }

        return lastLayerOuts
    }
    fun findError(lastNeuronError:Double){
        val reversedModel = model.reversed()
        var lastError = reversedModel[0].layer[0].weights.map {it * lastNeuronError}
        reversedModel[0].layer[0].error = lastNeuronError
        reversedModel.slice(1..<reversedModel.size).forEachIndexed { index, layer ->
            lastError = layer.findError(lastError)
        }
    }

    fun backward(){
        model.forEach { it.backward() }
    }

    override fun toString(): String {
        return "Model=${model}"
    }

}
