package chom.arikui.waffle.digitalclockapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class AdapterListAlarm(private val context: Context, private val resource: Int, private val dataList: List<RingtoneData>) : BaseAdapter() {

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

        val item = getItem(p0) as RingtoneData
        var convertView = p1

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, null)
        }

        val textAlarmTitle = convertView!!.findViewById(R.id.text_alarm_title) as TextView
        textAlarmTitle.text = item.title

        return convertView
    }

    override fun getItem(p0: Int): Any {
        return dataList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return dataList.size
    }

}