package com.kgg.android.seenear.network.data

import android.os.Parcel
import android.os.Parcelable

data class reportList(
    var reportList : List<Report>
)

data class Report(
    var date : String? = null,
    var statusList : List<Status>,
    var nerList : List<Ner>,
    var sentimentList : Map<String, Int>,

) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.createTypedArrayList(Status.CREATOR)!!,
        parcel.createTypedArrayList(Ner.CREATOR)!!,
        mutableMapOf<String, Int>().apply {
            parcel.readHashMap(Int::class.java.classLoader)?.let {
                putAll(it as Map<out String, Int>)
            }
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(date)
        parcel.writeTypedList(statusList)
        parcel.writeTypedList(nerList)
        parcel.writeMap(sentimentList as Map<*, *>?)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Report> {
        override fun createFromParcel(parcel: Parcel): Report {
            return Report(parcel)
        }

        override fun newArray(size: Int): Array<Report?> {
            return arrayOfNulls(size)
        }
    }
}


data class Ner(

    var id : Int = 0,
    var type : String? = null,
    var target : String? = null,
    var createdAt : String? = null,
    var content : String? = null,

): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(type)
        parcel.writeString(target)
        parcel.writeString(createdAt)
        parcel.writeString(content)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Ner> {
        override fun createFromParcel(parcel: Parcel): Ner {
            return Ner(parcel)
        }

        override fun newArray(size: Int): Array<Ner?> {
            return arrayOfNulls(size)
        }
    }
}

data class Status(
    var id : Int = 0,
    var type : String? = null,
    var done : Boolean = false,
    var chatId : Int = 0,
    var createdAt : String? = null,
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(type)
        parcel.writeByte(if (done) 1 else 0)
        parcel.writeInt(chatId)
        parcel.writeString(createdAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Status> {
        override fun createFromParcel(parcel: Parcel): Status {
            return Status(parcel)
        }

        override fun newArray(size: Int): Array<Status?> {
            return arrayOfNulls(size)
        }
    }
}
