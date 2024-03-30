package org.example

import kotlin.math.exp


// относительно работает

fun forwards(input:MutableList<MutableList<Double>>,weights:MutableList<MutableList<Double>>,output: MutableList<MutableList<Double>>){
    val weightsX = weights.size
    val weightsY = weights[0].size

    var y = 0
    while (y<weightsY){
        output[y][0] = 0.0
        var x = 0
        while (x<weightsX){
            output[y][0] = output[y][0] + input[x][0]*weights[x][y]
            x++
        }
        output[y][0] = 1/(1+ exp(-1*output[y][0]))
        y++
    }
}

fun findError(input: MutableList<MutableList<Double>>, weights: MutableList<MutableList<Double>>, output: MutableList<MutableList<Double>>){
    val weightsX = weights.size -1
    val weightsY = weights[0].size

    var x = 0

    while(x<weightsX){
        input[x][1] = 0.0
        var y = 0
        while (y<weightsY){
            input[x][1] = input[x][1] + weights[x][y]*output[y][1]
            y++
        }
        x++
    }

}


fun backwards(
    input:MutableList<MutableList<Double>>,
    weights:MutableList<MutableList<Double>>,
    output: MutableList<MutableList<Double>>,
    k:Double
){
    val weightsX = weights.size
    val weightsY = weights[0].size

    var y = 0
    while (y<weightsY){
        var x = 0
        while (x<weightsX){
            weights[x][y] = weights[x][y] + k * output[y][1] * output[y][0] * ( 1 - output[y][0]) * input[x][0]
            x++
        }
        y++
    }
}

fun fixOutError(IDL:MutableList<MutableList<Double>>, output: MutableList<MutableList<Double>>) {
    val IDLX = IDL[0].size
    var x = 0
    while (x < IDLX){
        output[x][1] = (IDL[x][0] - output[x][0]) * output[x][0] * (1 - output[x][0])
        x++
    }
}
fun main() {
    val N0 = mutableListOf<MutableList<Double>>()
    repeat(2){
        N0.add(mutableListOf(0.0,0.0))
    }
    N0[1][0] = 1.0

    val N1 = mutableListOf<MutableList<Double>>()
    repeat(3){
        N1.add(mutableListOf(0.0,0.0))
    }
    N1[2][0] = 1.0

    val N2 = mutableListOf<MutableList<Double>>()
    repeat(1){
        N2.add(mutableListOf(0.0,0.0))
    }

    var IDL = mutableListOf(mutableListOf(0.1))

    val W01 = mutableListOf<MutableList<Double>>()
    repeat(2){
        W01.add(mutableListOf(0.0,0.0))
    }
    val W12 = mutableListOf<MutableList<Double>>()
    repeat(3){
        W12.add(mutableListOf(0.0))
    }

    while (true) {
        forwards(N0, W01, N1)
        forwards(N1, W12, N2)

        fixOutError(IDL, N2)
        findError(N1, W12, N2)

        backwards(N1, W12, N2, 0.1)
        backwards(N0, W01, N1, 0.1)
        println(N2)

    }

}