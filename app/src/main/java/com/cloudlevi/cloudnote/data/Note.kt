package com.cloudlevi.cloudnote.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.FieldPosition

@Entity(tableName = "notes_table")
@Parcelize
data class Note (
    var title: String,
    var description: String = "",
    var date_modified: Long = System.currentTimeMillis(),
    var folder: Int = -1,
    var pinned: Boolean = false,
    var background_color: String = "#FFFFFF",
    var password: String = "",
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
): Parcelable{
}