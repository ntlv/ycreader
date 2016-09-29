package se.ntlv.newsbringer.newsthreads

import android.view.View
import android.widget.TextView
import org.jetbrains.anko.find
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onLongClick
import se.ntlv.newsbringer.R
import se.ntlv.newsbringer.adapter.BindingViewHolder
import se.ntlv.newsbringer.adapter.starify
import se.ntlv.newsbringer.customviews.DateView
import se.ntlv.newsbringer.network.RowItem

class FrontpageHolder(root: View,
                 private val onClick: (FrontpageHolder) -> Unit,
                 private val onLongClick: (FrontpageHolder) -> Boolean) : BindingViewHolder<RowItem.NewsThreadUiData>(root) {

    //model data
    var id: Long? = null
    var isStarred: Int? = null

    //view bindings
    val view = root
    val title = root.find<TextView>(R.id.news_item_title)
    val by = root.find<TextView>(R.id.by)
    val time = root.find<DateView>(R.id.news_item_submission_time)
    val score = root.find<TextView>(R.id.score)
    var link: String? = null
    val commentCount = root.find<TextView>(R.id.comment_count)
    val ordinal = root.find<TextView>(R.id.ordinal)

    override fun bind(item: RowItem.NewsThreadUiData) {
        //model data
        id = item.id
        isStarred = item.isStarred

        //bind view data
        title.text = item.title.starify(item.isStarred)
        by.text = item.by
        time.text = item.time.toString()
        score.text = item.score.toString()
        link = item.url
        commentCount.text = item.descendants.toString()
        ordinal.text = item.ordinal.toString()

        view.onClick { onClick(this) }
        view.onLongClick { onLongClick(this) }
    }
}