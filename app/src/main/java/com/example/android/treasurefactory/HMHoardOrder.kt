package com.example.android.treasurefactory

data class HMHoardOrder(var creationDescription: String = "",
                        var copperPieces: Int = 0 ,
                        var silverPieces: Int = 0 ,
                        var electrumPieces: Int = 0 ,
                        var goldPieces: Int = 0 ,
                        var hardSilverPieces: Int = 0 ,
                        var platinumPieces: Int = 0 ,
                        var gems: Int = 0 ,
                        var artObjects: Int = 0 ,
                        var potions: Int = 0,
                        var scrolls: Int = 0,
                        var armorOrWeapons: Int = 0,
                        var anyButWeapons: Int = 0,
                        var anyMagicItems: Int = 0
                        /*TODO: make data class for handling magic item creation guidelines*/)
