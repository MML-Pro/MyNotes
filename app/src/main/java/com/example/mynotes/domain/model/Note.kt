package com.example.mynotes.domain.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(tableName = "notes_table")
@Parcelize
data class Note(

    @PrimaryKey(autoGenerate = true)
    var id:Int =0,
    @ColumnInfo("title")
    val title:String,
    @ColumnInfo("note_content")
    val noteContent:String,
    @ColumnInfo("date_time")
    val dateTime:String,
    @ColumnInfo("image_path")
    val imagePath:String?=null,
    @ColumnInfo("color")
    val color:String?=null,
    @ColumnInfo("web_link")
    val webLink:String?=null
) : Parcelable{

    override fun toString(): String {
        return "$title : $dateTime"
    }
}
