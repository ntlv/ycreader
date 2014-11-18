package se.ntlv.newsbringer

import android.widget.TextView
import android.content.Context
import android.util.AttributeSet
import kotlin.template.LocaleFormatter
import android.util.Log

class DateView(context: Context, attributeSet: AttributeSet) : TextView(context, attributeSet) {

    override fun setText(text: CharSequence?, type: TextView.BufferType?) {
        val reformattedText = text?.reformat()
        super<TextView>.setText(reformattedText ?: text, type)
    }
}

fun CharSequence.reformat(): CharSequence {
    try {
        val asLong = this.toString().toLong()
        val diff = System.currentTimeMillis().div(1000).minus(asLong)
        return when {
            diff < 60 -> "$diff seconds"
            diff < 3600 -> "${diff.div(60)} min"
            diff < 86400 -> "${diff.div(3600)} hours"
            else -> "${diff.div(86400)} days"
        }
    } catch (formatException: NumberFormatException) {
        return this;
    }
}
