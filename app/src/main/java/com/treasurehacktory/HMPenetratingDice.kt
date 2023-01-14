package com.treasurehacktory

import java.text.NumberFormat
import kotlin.math.absoluteValue
import kotlin.random.Random

data class PenDiceRoll(val numberOfDice: Int = 1, val numberOfSides: Int, val dieModifier: Int = 0,
                       val highThreshold: Int = 1, val lowThreshold: Int = 0,
                       val honorModifier: Int = 0, val willAutoPenetrate: Boolean,
                       val standardRolls : List<Int>, val extraRolls : List<Int>) {

    fun getRollTotal() = standardRolls.sum() + dieModifier +
            extraRolls.fold(0) {total, roll -> total + roll - 1 } +
            ((standardRolls.size + extraRolls.size) * honorModifier)

    fun getDiceDescription() = NumberFormat.getNumberInstance().format(numberOfDice) + "d" +
            NumberFormat.getNumberInstance().format(numberOfSides) + when {
                dieModifier > 0 -> "+" + NumberFormat.getNumberInstance().format(dieModifier)
                dieModifier < 0 -> "-" + NumberFormat.getNumberInstance().format(dieModifier.absoluteValue)
                else    -> ""
            } +
            (if (highThreshold + lowThreshold > 0) " p${if (willAutoPenetrate) "a" else ""}" else "") +
            (if (highThreshold > 0) "(+${NumberFormat.getNumberInstance().format(highThreshold)})" else ("")) +
            (if (lowThreshold > 0) "{-${NumberFormat.getNumberInstance().format(lowThreshold)}}" else ("")) +
            (if (honorModifier != 0) " [HON ${if(honorModifier > 0)"+" else "-"}${honorModifier.absoluteValue}]" else "")
}

/**
 * Rolls dice using the penetrating dice rules of HackMaster 4e.
 *
 * @param highThreshold How many values, starting at and including, the maximum rollable value will
 * trigger an upwards penetration if rolled.
 * @param lowThreshold How many values, starting at and including, the minimum rollable value will
 * trigger an downwards penetration if rolled.
 * @param honorModifier Penalty/bonus to apply to every die roll due to an affected entity's honor.
 * @param willAutoPenetrate Whether or not the first die should automatically be counted as a penetration.
 */
fun rollPenetratingDice(numberOfDice: Int = 1, numberOfSides: Int, dieModifier: Int = 0,
                        highThreshold: Int = 1, lowThreshold: Int = 0,
                        honorModifier: Int = 0, willAutoPenetrate: Boolean = false) : PenDiceRoll {

    var remainingRolls              = numberOfDice
    var remainingPositiveRolls      = 0
    var remainingNegativeRolls      = 0
    var currentRoll                 = 0
    val standardRolls               = ArrayList<Int>()
    val extraRolls                  = ArrayList<Int>()

    /// *** Perform base rolls ***

    do {
        currentRoll = Random.nextInt(1,numberOfSides + 1)   // Get actual roll

        standardRolls.add(currentRoll)
        remainingRolls --

        if (numberOfSides >= 4) {

            when {

                (currentRoll > numberOfSides - highThreshold) || (willAutoPenetrate) ->
                    remainingPositiveRolls++

                (currentRoll <= lowThreshold) ->
                    remainingNegativeRolls++
            }
        }

    } while(remainingRolls > 0)

    // *** Roll penetration dice, if applicable ***

    while (remainingPositiveRolls > 0 && extraRolls.size <= numberOfDice * 16) {

        currentRoll = Random.nextInt(1,numberOfSides + 1)

        extraRolls.add(currentRoll)

        remainingPositiveRolls --

        if (currentRoll > numberOfSides - highThreshold) { remainingPositiveRolls ++ }
    }

    while (remainingNegativeRolls > 0 && extraRolls.size <= numberOfDice * 16) {

        currentRoll = Random.nextInt(1,numberOfSides + 1)

        extraRolls.add(-1 * currentRoll)

        remainingNegativeRolls --

        if (currentRoll > numberOfSides - lowThreshold) { remainingNegativeRolls ++ }
    }

    return PenDiceRoll(numberOfDice, numberOfSides, dieModifier, highThreshold, lowThreshold,
        honorModifier, willAutoPenetrate, standardRolls, extraRolls)
}