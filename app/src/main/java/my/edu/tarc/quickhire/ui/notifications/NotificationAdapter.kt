package my.edu.tarc.quickhire.ui.notifications

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import my.edu.tarc.quickhire.R


class NotificationAdapter (private val context: Context, private var dataList: List<NotificationData>) : RecyclerView.Adapter<MyViewHolder>()
//class MyAdapter (private val parentFragment: Fragment: Context, private var dataList: List<DataClass>) : RecyclerView.Adapter<MyViewHolder>()

{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.recycler_notification, parent, false)
        return MyViewHolder(view)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(dataList[position].notificationImage)
            .into(holder.recImage)
        holder.recTitle.text = dataList[position].notificationTitle
        holder.recDesc.text = dataList[position].notificationDec
        holder.recTime.text = dataList[position].notificationTime
        holder.recType.text = dataList[position].notificationType

        holder.recCard.setOnClickListener {

            holder.recCard.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("Image", dataList[holder.adapterPosition].notificationImage)
                bundle.putString("Description", dataList[holder.adapterPosition].notificationDec)
                bundle.putString("Title", dataList[holder.adapterPosition].notificationTitle)
                bundle.putString("Time", dataList[holder.adapterPosition].notificationTime)
                bundle.putString("Type", dataList[holder.adapterPosition].notificationType)

                val navController = holder.itemView.findNavController()
                //first_type
                navController.navigate(R.id.action_nav_notification_to_nav_notificationDetail, bundle)

                //second_type
                navController.navigate(R.id.action_nav_notificationOrganization_to_nav_notificationDetailOrganization, bundle)
            }

        }
    }




    override fun getItemCount(): Int {
        return dataList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun searchDataList(searchList: List<NotificationData>) {
        dataList = searchList
        notifyDataSetChanged()
    }
}
class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var recImage: ImageView
    var recTitle: TextView
    var recDesc: TextView
    var recTime: TextView
    var recType: TextView
    var recCard: CardView
    init {
        recImage = itemView.findViewById(R.id.recImage)
        recTitle = itemView.findViewById(R.id.recTitle)
        recDesc = itemView.findViewById(R.id.recDesc)
        recTime = itemView.findViewById(R.id.recPriority)
        recCard = itemView.findViewById(R.id.recCard)
        recType = itemView.findViewById(R.id.recType)
    }
}