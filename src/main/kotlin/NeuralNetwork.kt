package org.example

import kotlin.math.exp


/**
 * работает
 *  сделано по https://www.youtube.com/watch?v=lDxyGKGVq3s&t=345s
 */


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
    var N0 = mutableListOf<MutableList<Double>>() // слой нейронов
    // 2 - число нейронов
    repeat(2){
        N0.add(mutableListOf(0.0,0.0)) // первое число - выходное значение нейрона, второе - ошибка
    }
    N0[1][0] = 1.0 // нейрон смещения (зачем то)

    val N1 = mutableListOf<MutableList<Double>>()
    repeat(3){
        N1.add(mutableListOf(0.0,0.0))
    }
    N1[2][0] = 1.0

    val N2 = mutableListOf<MutableList<Double>>()
    repeat(1){
        N2.add(mutableListOf(0.0,0.0))
    }

    var IDL: MutableList<MutableList<Double>>

    val W01 = mutableListOf<MutableList<Double>>() //веса между 0 и 1 слоем
    //число 2 -  число нейронов в выходном слое(без нейрона смещения т.к к нему связи не идут)
    repeat(2){
        W01.add(mutableListOf(0.0,0.0)) // кол-во элементов - число нейронов в входном слое(с учетом нейрона смещения т.к. от него связи идут)
    }
    val W12 = mutableListOf<MutableList<Double>>()
    repeat(3){
        W12.add(mutableListOf(0.0))
    }
    /*
    * P.S. при обучении неронки на более чем 1 примере важно их чередовать,
    * т.к. в противном случае нейронка будет работать правильно только с последним примером из выборки
    * (проверено на горьком опыте)
    * */
    // надо придумать условие выхода
    repeat(500000){
        /* пример один ( входное число 0.0 надо получить 1.0)*/
        // задаем входные значения
        N0 = mutableListOf<MutableList<Double>>()
        repeat(2){
            N0.add(mutableListOf(0.0,0.0))
        }
        IDL = mutableListOf(mutableListOf(1.0))// желаемое значение
        // проход
        forwards(N0, W01, N1)
        forwards(N1, W12, N2)
        // ошибки
        fixOutError(IDL, N2)
        findError(N1, W12, N2)
        // проход назад
        backwards(N1, W12, N2, 0.1)
        backwards(N0, W01, N1, 0.1)
        println(N2)
        /* пример два ( входное число 1.0 надо получить 0.0)*/
        IDL = mutableListOf(mutableListOf(0.0))

        N0 = mutableListOf<MutableList<Double>>()
        repeat(2){
            N0.add(mutableListOf(1.0,0.0))
        }

        forwards(N0, W01, N1)
        forwards(N1, W12, N2)

        fixOutError(IDL, N2)
        findError(N1, W12, N2)

        backwards(N1, W12, N2, 0.1)
        backwards(N0, W01, N1, 0.1)

        println(N2)

        /* пример три ( входное число 0.5 надо получить 0.5)*/
        IDL = mutableListOf(mutableListOf(0.5))

        N0 = mutableListOf<MutableList<Double>>()
        repeat(2){
            N0.add(mutableListOf(0.5,0.0))
        }

        forwards(N0, W01, N1)
        forwards(N1, W12, N2)

        fixOutError(IDL, N2)
        findError(N1, W12, N2)

        backwards(N1, W12, N2, 0.1)
        backwards(N0, W01, N1, 0.1)

        println(N2)
    }
    // проверяем на значениях из тренировочной выборки
    println("---------------1.0---------------")
    N0 = mutableListOf<MutableList<Double>>()
    repeat(2){
        N0.add(mutableListOf(1.0,0.0))
    }
    forwards(N0, W01, N1)
    forwards(N1, W12, N2)
    println(N2)
    println("---------------0.0---------------")

    N0 = mutableListOf<MutableList<Double>>()
    repeat(2){
        N0.add(mutableListOf(0.0,0.0))
    }
    forwards(N0, W01, N1)
    forwards(N1, W12, N2)
    println(N2)
    println("---------------0.5---------------")

    N0 = mutableListOf<MutableList<Double>>()
    repeat(2){
        N0.add(mutableListOf(0.5,0.0))
    }
    // проверяем на левых значениях
    forwards(N0, W01, N1)
    forwards(N1, W12, N2)
    println(N2)
    println("---------------0.4---------------")

    N0 = mutableListOf<MutableList<Double>>()
    repeat(2){
        N0.add(mutableListOf(0.4,0.0))
    }
    forwards(N0, W01, N1)
    forwards(N1, W12, N2)
    println(N2)
    println("---------------0.7---------------")

    N0 = mutableListOf<MutableList<Double>>()
    repeat(2){
        N0.add(mutableListOf(0.7,0.0))
    }
    forwards(N0, W01, N1)
    forwards(N1, W12, N2)
    println(N2)
    println("---------------0.2---------------")

    N0 = mutableListOf<MutableList<Double>>()
    repeat(2){
        N0.add(mutableListOf(0.2,0.0))
    }
    forwards(N0, W01, N1)
    forwards(N1, W12, N2)
    println(N2)


}


