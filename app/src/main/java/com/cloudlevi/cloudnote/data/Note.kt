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
    val title: String,
    val description: String = "",
    val date_modified: Long = System.currentTimeMillis(),
    val folder: Int = -1,
    val background_color: String = "",
    val password: String = "",
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
): Parcelable{

}