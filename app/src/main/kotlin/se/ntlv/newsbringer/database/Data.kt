package se.ntlv.newsbringer.database

import android.os.Parcelable
import android.support.v7.util.DiffUtil.DiffResult


interface ParcelableIdentifiable : Parcelable {
    val id: Long

    override fun describeContents(): Int = 0
}

interface AdapterModelCollection<out T : ParcelableIdentifiable> : Parcelable {
    operator fun get(position: Int): T

    val diff: DiffResult
    val size: Int

    override fun describeContents(): Int = 0
}
