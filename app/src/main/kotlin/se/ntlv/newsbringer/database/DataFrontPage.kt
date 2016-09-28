package se.ntlv.newsbringer.database

import android.os.Parcel
import android.os.Parcelable
import android.support.v7.util.DiffUtil
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import se.ntlv.newsbringer.network.RowItem
import se.ntlv.newsbringer.customviews.DataDiffCallback

class DataFrontPage(internal val base: List<RowItem.NewsThreadUiData>,
                    override val diff: DiffUtil.DiffResult) : AdapterModelCollection<RowItem.NewsThreadUiData> {

    override fun get(position: Int) = base[position]

    override val size = base.size

    companion object : AnkoLogger {

        @Suppress("unused")
        @JvmField
        val CREATOR = object : Parcelable.Creator<DataFrontPage> {
            override fun newArray(size: Int): Array<DataFrontPage?> = arrayOfNulls(size)

            override fun createFromParcel(source: Parcel): DataFrontPage {
                info("Creating from parcel")
                val base: MutableList<RowItem.NewsThreadUiData> = mutableListOf()
                source.readTypedList(base, RowItem.NewsThreadUiData.CREATOR)
                val diff = DiffUtil.calculateDiff(DataDiffCallback(null, base))
                return DataFrontPage(base, diff)
            }
        }
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeTypedList(base)
    }

    override fun describeContents(): Int = 0
}
