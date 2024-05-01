package org.example



fun main() {
    val x = mutableListOf(Value(0.0))
    val model = MLP(1, mutableListOf(4,4,1))
    val y = Value(1.0)
    var ypred = model.forward(x)[0]
    var loss = (ypred - y) * (ypred - y)
    println(loss)
    while (loss.data>0.01) {
        println("Loss=${loss.data}")
        ypred = model.forward(x)[0]
        loss = (ypred - y) * (ypred - y)
        loss.backwardAll()
        model.parameters().forEach {
            it.data += -0.01 * it.grad
        }
        println("Predicted=${ypred.data}")
    }
}