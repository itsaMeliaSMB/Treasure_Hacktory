package com.example.android.treasurefactory.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.android.treasurefactory.database.ArtObjectEntity
import com.example.android.treasurefactory.database.GemEntity
import com.example.android.treasurefactory.database.MagicItemEntity
import com.example.android.treasurefactory.database.SpellCollectionEntity
import com.example.android.treasurefactory.model.Hoard

data class HoardWithUniqueItems(
    @Embedded val hoard: Hoard,
    @Relation(
        parentColumn = "hoardID",
        entityColumn = "hoardID"
    ) val gems: List<GemEntity>,
    @Relation(
        parentColumn = "hoardID",
        entityColumn = "hoardID"
    ) val artObjects: List<ArtObjectEntity>,
    @Relation(
        parentColumn = "hoardID",
        entityColumn = "hoardID"
    ) val magicItems: List<MagicItemEntity>,
    @Relation(
        parentColumn = "hoardID",
        entityColumn = "hoardID"
    ) val spellCollections: List<SpellCollectionEntity>,

    )