package com.example.android.treasurefactory

import kotlin.random.Random

data class DieType(val numberOfDice:Int = 1,val numberOfSides:Int,val dieModifier:Int = 0)

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

//TODO Add parameters replacing dieType in this function to get rid of DieType class
fun rollPenetratingDice(dieType: DieType, highThreshold: Int = 1, lowThreshold: Int = 0,
                        honorModifier: Int = 0, willAutoPenetrate: Boolean = false) : PenDiceRoll{

    val roll                        = PenDiceRoll()
    var remainingRolls              = dieType.numberOfDice
    var remainingPenetrationRolls   = 0
    var isPositivePenetration       = false
    var currentRoll                 = 0

    /// *** Perform base rolls ***

    do {
        currentRoll = Random.nextInt(1,dieType.numberOfSides + 1)   // Get actual roll

        roll.baseRoll += currentRoll + honorModifier               // Add to total value w/ HON mod
        roll.baseCount ++                                           // Increment dice counter
        remainingRolls --                                           // Decrement remaining base rolls

        // *** Determine penetration direction and applicability***

        if (dieType.numberOfSides >= 4) {

            when {

                (currentRoll <= lowThreshold) -> {

                    isPositivePenetration = false
                    remainingPenetrationRolls++
                }

                (currentRoll > dieType.numberOfSides - highThreshold) || (willAutoPenetrate) -> {

                    isPositivePenetration = true
                    remainingPenetrationRolls++
                }
            }
        }

    } while(remainingRolls > 0)

    roll.baseRoll += dieType.dieModifier                       // Add one-time modifier to total

    // *** Roll penetration dice, if applicable ***

    while (remainingPenetrationRolls > 0) {

        currentRoll = Random.nextInt(1,dieType.numberOfSides + 1)

        if (isPositivePenetration)  {
            roll.penetrationRoll += currentRoll - 1 + honorModifier
        } else {
            roll.penetrationRoll -= currentRoll - 1 + honorModifier
        }

        roll.penetrationCount ++
        remainingPenetrationRolls --

        // *** Determine penetration direction and applicability***

        if (dieType.numberOfSides >= 4) {

            when {

                (currentRoll <= lowThreshold) -> {

                    isPositivePenetration = false
                    remainingPenetrationRolls ++
                }

                (currentRoll > dieType.numberOfSides - highThreshold) -> {

                    isPositivePenetration = true
                    remainingPenetrationRolls ++
                }
            }
        }
    }

    return roll
}