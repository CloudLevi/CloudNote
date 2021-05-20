package com.cloudlevi.cloudnote.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "folders_table")
@Parcelize
data class Folder(
    val title: String,
    val date_modified: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
):Parcelable {
}