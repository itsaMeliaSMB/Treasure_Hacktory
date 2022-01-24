package com.example.android.treasurefactory.model

import androidx.room.Ignore

/**
 * Generated magic item following HackMaster 4e rules.
 *
 * @param templateID Primary key of the magic item template in the database reference table
 * @param classUsability Map of class type (fighter/thief/cleric/magic-user/druid) to its ability to use this item (true/false). Use lowercase for strings.
 * @param notes List of generated special notes for the object. First list should be a list of names for all the other lists.
 */
data class MagicItem(
    val mItemID: Int,
    val templateID: Int,
    val hoardID: Int,
    val iconID: String,
    val typeOfItem: String, //TODO add function for converting table to descriptive label
    val name: String,
    val sourceText: String,
    val sourcePage: Int,
    val xpWorth: Int,
    val gpWorth: Double,
    val classUsability: Map<String,Boolean>,
    val isCursed: Boolean,
    val alignment: String,
    val notes: List<List<String>> = emptyList(),
    val userNotes: List<String> = emptyList()) : Evaluable {

    @Ignore
    override fun getGpValue(): Double = gpWorth

    @Ignore
    override fun getXpValue(): Int = xpWorth
    }