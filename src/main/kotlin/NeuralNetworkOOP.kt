package org.example

import kotlin.math.exp
import kotlin.random.Random


// работает
// https://youtu.be/VMj-3S1tku0?list=LL
data class Value(
    var data:Double,
    val children:MutableList<Value> = mutableListOf(),
    var grad:Double= 0.0,
    var backward: () -> Unit = {  }
    ){

    operator fun plus(other: Value):Value {
        val out = Value(data + other.data,
            mutableListOf(this, other))
        out.backward = {
            this.grad += 1.0*out.grad
            other.grad += 1.0*out.grad
        }
        return out
    }

    operator fun times(other: Value): Value{
        val out = Value(data * other.data,
            mutableListOf(this, other))
        out.backward = {
            this.grad += other.data*out.grad
            other.grad += this.data*out.grad
        }
        return out

    }

    fun tanh(): Value {
        val n = data
        val t = (exp(2*n) - 1) / (exp(2*n)+1)
        val out = Value(t, mutableListOf(this))
        out.backward = {
            this.grad += (1-t*t) * out.grad
        }
        return out

    }
    fun backwardAll(){
        val topo = mutableListOf<Value>()
        val visited = mutableListOf<Value>()
        fun buildTopo(v:Value){
            if (v !in visited){
                visited.add(v)
                for (child in v.children){
                    buildTopo(child)
                }
                topo.add(v)
            }
        }
        buildTopo(this)

        this.grad = 1.0
        for (node in topo.reversed()){
            node.backward()
        }

    }

    override fun toString(): String {
        return "Value(data=${data})"
    }

    operator fun minus(other: Value): Value {
        return this + (-other)
    }

    private operator fun unaryMinus(): Value {
        return this * Value(-1.0)
    }
}

class Neuron(numberOfInputs:Int){
    val weights = MutableList<Value>(size = numberOfInputs) { Value(Random.nextDouble(-1.0, 1.0)) }
    val b = Value(Random.nextDouble(-1.0,1.0))


    fun forward(x:MutableList<Value>): Value {
        var act = b.copy()
        for(i in x.zip(weights)){
            act+=i.second*i.first
        }
        val out = act.tanh()
        return out
    }
    fun parameters() = weights + b

}
class Layer(val numberOfInputs: Int,val numberOfNeurons: Int){
    val neurons = MutableList<Neuron>(size = numberOfNeurons) { Neuron(numberOfInputs)}

    fun forward(x:MutableList<Value>): MutableList<Value> {
        val outs = mutableListOf<Value>()
        neurons.forEach { outs.add(it.forward(x)) }
        return outs
    }
    fun parameters(): MutableList<Value> {
        val params = mutableListOf<Value>()
        neurons.forEach {params.addAll(it.parameters()) }
        return params
    }
}

class MLP(val numberOfInputs: Int,val numberOfOuts: List<Int>){
    val size = intArrayOf(numberOfInputs) + numberOfOuts
    val layers = mutableListOf<Layer>()
    init {
        for (i in 0..<numberOfOuts.size){
            layers.add(Layer(size[i],size[i+1]))
        }
    }
    fun forward(x:MutableList<Value>): MutableList<Value> {
        var x = x
        layers.forEach { layer ->
            x = layer.forward(x)
        }
        return x
    }
    fun parameters(): MutableList<Value> {
        val params = mutableListOf<Value>()
        layers.forEach {params.addAll(it.parameters()) }
        return params
    }

}



