package com.treasurehacktory

import kotlin.random.Random

data class PenDiceRoll(var baseRoll: Int = 0, var baseCount: Int = 0,
                       var penetrationRoll: Int = 0, var penetrationCount: Int = 0) {

    fun clearAll(){

        baseRoll = 0
        baseCount = 0
        penetrationRoll = 0
        penetrationCount = 0
    }

    fun addToThis(addend: PenDiceRoll){

        baseRoll           += addend.baseRoll
        baseCount           += addend.baseCount
        penetrationRoll    += addend.penetrationRoll
        penetrationCount    += addend.penetrationCount
    }

    fun getRollTotal() = baseRoll + penetrationRoll

    fun getDiceCount() = baseCount + penetrationCount
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

    val roll                        = PenDiceRoll()
    var remainingRolls              = numberOfDice
    var remainingPenetrationRolls   = 0
    var isPositivePenetration       = false
    var currentRoll                 = 0

    /// *** Perform base rolls ***

    do {
        currentRoll = Random.nextInt(1,numberOfSides + 1)   // Get actual roll

        roll.baseRoll += currentRoll + honorModifier               // Add to total value w/ HON mod
        roll.baseCount ++                                          // Increment dice counter
        remainingRolls --                                          // Decrement remaining base rolls

        // *** Determine penetration direction and applicability ***

        if (numberOfSides >= 4) {

            when {

                (currentRoll <= lowThreshold) -> {

                    isPositivePenetration = false
                    remainingPenetrationRolls++
                }

                (currentRoll > numberOfSides - highThreshold) || (willAutoPenetrate) -> {

                    isPositivePenetration = true
                    remainingPenetrationRolls++
                }
            }
        }

    } while(remainingRolls > 0)

    roll.baseRoll += dieModifier                       // Add one-time modifier to total

    // *** Roll penetration dice, if applicable ***

    while (remainingPenetrationRolls > 0) {

        currentRoll = Random.nextInt(1,numberOfSides + 1)

        if (isPositivePenetration)  {
            roll.penetrationRoll += currentRoll - 1 + honorModifier
        } else {
            roll.penetrationRoll -= currentRoll - 1 + honorModifier
        }

        roll.penetrationCount ++
        remainingPenetrationRolls --

        // *** Determine penetration direction and applicability***

        if (numberOfSides >= 4) {

            when {

                (currentRoll <= lowThreshold) -> {

                    isPositivePenetration = false
                    remainingPenetrationRolls ++
                }

                (currentRoll > numberOfSides - highThreshold) -> {

                    isPositivePenetration = true
                    remainingPenetrationRolls ++
                }
            }
        }
    }

    return roll
}