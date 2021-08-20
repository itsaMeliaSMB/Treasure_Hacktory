package com.example.android.treasurefactory

data class TypeStats(val chance: Int =0, val lowerBound: Int = 0, val upperBound: Int = lowerBound)

val letterTypeTable = mapOf<String,TypeStats>(

    //TODO: convert enum class to a class with a companion object table and processing functions
    //It's too damn hot tonight. Sorry, future Amelia. I hope I at least went to bed on time.
)

enum class HMLetterType(val treasureTable: List<TypeStats>) {

    // Table 13S : Lair Treasures

    A(listOf(TypeStats(25,1000,3000),TypeStats(30,200,2000),TypeStats(35,500,3000),TypeStats(40,1000,6000),
        TypeStats(35,300,1800),TypeStats(35,300,1800),TypeStats(60,10,40),TypeStats(50,2,12),TypeStats(),
        TypeStats(),TypeStats(),TypeStats(),TypeStats(30,3,3))),
    B(listOf(TypeStats(50,1000,6000),TypeStats(25,1000,3000),TypeStats(25,300,1800),TypeStats(25,200,2000),
        TypeStats(25,150,1500),TypeStats(25,100,1000),TypeStats(30,1,8),TypeStats(20,1,4),TypeStats(),
        TypeStats(),TypeStats(10,1,1),TypeStats(),TypeStats())),
    C(listOf(TypeStats(20,1000,10000),TypeStats(30,1000,6000),TypeStats(40,1000,3000),TypeStats(),TypeStats(),
        TypeStats(10,100,600),TypeStats(25,1,6),TypeStats(20,1,3),TypeStats(),TypeStats(),TypeStats(),TypeStats(),
        TypeStats(10,2,2))),
    D(listOf(TypeStats(10,1000,6000),TypeStats(15,1000,10000),TypeStats(25,1000,12000),TypeStats(50,1000,3000),TypeStats(),
        TypeStats(15,100,600),TypeStats(30,1,10),TypeStats(25,1,6),TypeStats(15,1,1),TypeStats(),TypeStats(),
        TypeStats(),TypeStats(15,2,2))),
    E(listOf(TypeStats(5,1000,6000),TypeStats(25,1000,10000),TypeStats(45,1000,12000),TypeStats(25,1000,4000),
        TypeStats(15,100,1200),TypeStats(25,300,1800),TypeStats(15,1,12),TypeStats(10,1,6),TypeStats(),
        TypeStats(25,1,1),TypeStats(),TypeStats(),TypeStats(25,3,3))),
    F(listOf(TypeStats(),TypeStats(10,3000,18000),TypeStats(25,2000,12000),TypeStats(40,1000,6000),TypeStats(30,500,5000),
        TypeStats(15,1000,4000),TypeStats(20,2,20),TypeStats(10,1,8),TypeStats(),TypeStats(),TypeStats(),
        TypeStats(30,5,5),TypeStats())),
    G(listOf(TypeStats(),TypeStats(),TypeStats(15,3000,24000),TypeStats(50,2000,20000),TypeStats(50,1500,15000),
        TypeStats(50,1000,10000),TypeStats(30,3,18),TypeStats(25,1,6),TypeStats(),TypeStats(),TypeStats(),
        TypeStats(),TypeStats(35,5,5))),
    H(listOf(TypeStats(25,3000,18000),TypeStats(35,2000,20000),TypeStats(45,2000,20000),TypeStats(55,2000,20000),
        TypeStats(45,2000,20000),TypeStats(35,1000,8000),TypeStats(50,3,30),TypeStats(50,2,20),TypeStats(),
        TypeStats(),TypeStats(),TypeStats(),TypeStats(15,6,6))),
    I(listOf(TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(15,100,400),TypeStats(30,100,600),
        TypeStats(55,2,12),TypeStats(50,2,8),TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(15,1,1))),

    // Table 13T: Individual and Small Lair Treasures

    J(listOf(TypeStats(100,3,24),TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),
        TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats())),
    K(listOf(TypeStats(),TypeStats(100,3,18),TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),
        TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats())),
    L(listOf(TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(100,3,18),TypeStats(100,2-12),TypeStats(),
        TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats())),
    M(listOf(TypeStats(),TypeStats(),TypeStats(100,3,12),TypeStats(100,2,8),TypeStats(),TypeStats(),TypeStats(),
        TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats())),
    N(listOf(TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(100,1,6),TypeStats(),TypeStats(),
        TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats())),
    O(listOf(TypeStats(100,10,40),TypeStats(100,10,30),TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),
        TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats())),
    P(listOf(TypeStats(),TypeStats(100,10,60),TypeStats(100,3,30),TypeStats(),TypeStats(),TypeStats(100,1,20),TypeStats(),
        TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats())),
    Q(listOf(TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(100,1,4),TypeStats(),
        TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats())),
    R(listOf(TypeStats(),TypeStats(),TypeStats(),TypeStats(100,2,20),TypeStats(),TypeStats(100,10,60),TypeStats(100,2,8),
        TypeStats(100,1,3),TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats())),
    S(listOf(TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),
        TypeStats(100,1,8),TypeStats(),TypeStats(),TypeStats(),TypeStats())),
    T(listOf(TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),
        TypeStats(100,1,4),TypeStats(),TypeStats(),TypeStats())),
    U(listOf(TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(90,2,16),TypeStats(80,1,6),
        TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(70,1,1))),
    V(listOf(TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),
        TypeStats(),TypeStats(),TypeStats(),TypeStats(100,2,2))),
    W(listOf(TypeStats(),TypeStats(),TypeStats(100,4,24),TypeStats(100,5,30),TypeStats(100,2,16),TypeStats(100,1,8),
        TypeStats(60,2,16),TypeStats(50,1,8),TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(60,2,2))),
    X(listOf(TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats(),
        TypeStats(100,2,2),TypeStats(),TypeStats(),TypeStats(),TypeStats())),
    Y(listOf(TypeStats(),TypeStats(),TypeStats(),TypeStats(100,200,1200),TypeStats(),TypeStats(),TypeStats(),TypeStats(),
        TypeStats(),TypeStats(),TypeStats(),TypeStats(),TypeStats())),
    Z(listOf(TypeStats(100,100,300),TypeStats(100,100,400),TypeStats(100,100,500),TypeStats(100,100,600),
        TypeStats(100,100,500),TypeStats(100,100,400),TypeStats(55,1,6),TypeStats(50,2,12),TypeStats(),TypeStats(),
        TypeStats(),TypeStats(),TypeStats(50,3,3)));

    companion object {

        fun getOddsString(letterType: HMLetterType): String {

            val treasureLabels = listOf<String>(
                "copper piece(s)",
                "silver piece(s)",
                "electrum piece(s)",
                "gold piece(s)",
                "hard silver piece(s)",
                "platinum piece(s)",
                "gem(s)",
                "art object(s)",
                "potion(s)/oil(s)",
                "scroll(s)",
                "magic weapon(s)/armor",
                "non-weapon magic item(s)",
                "magic item(s)")

            val outputList = mutableListOf<String>()

            var outputString = ""

            fun generateOddsLine(input: TypeStats, label: String): String {

                return if (input.chance != 0) {

                    "${input.chance}% chance of " +
                            "${if (input.lowerBound == input.upperBound) input.lowerBound
                            else "${input.lowerBound} - ${input.upperBound}" } " + label + "."
                } else ""
            }

            // Write odds of a particular type of treasure to the list if non-0% chance
            letterType.treasureTable.forEachIndexed{index, entry ->
                if (entry.chance != 0) {
                    outputList.add("${entry.chance}% chance of " +
                            "${if (entry.lowerBound == entry.upperBound) entry.lowerBound
                            else "${entry.lowerBound} - ${entry.upperBound}" } " +
                            treasureLabels[index] + ".")
                }
            }

            // Concatenate list entries into single string
            outputString = outputList.joinToString("\n")

            // Return result
            return outputString
        }
    }
}

/* NOTICE:  LetterTypes have been changed from the pure Kotlin application.
            They have been moved from individual constructor parameters to a list.
            Please refactor as necessary.
 */