package my.edu.tarc.quickhire.ui.notifications.organization

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.ui.notifications.MyViewHolder
import my.edu.tarc.quickhire.ui.notifications.NotificationData

class NotificationAdapterOrganization(private val context: Context, private var dataList: List<NotificationData>) : RecyclerView.Adapter<MyViewHolder>()
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
        holder.recUID.text = dataList[position].notificationUID
        holder.recCard.setOnClickListener {

            holder.recCard.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("Image", dataList[holder.adapterPosition].notificationImage)
                bundle.putString("Description", dataList[holder.adapterPosition].notificationDec)
                bundle.putString("Title", dataList[holder.adapterPosition].notificationTitle)
                bundle.putString("Time", dataList[holder.adapterPosition].notificationTime)
                bundle.putString("UID", dataList[holder.adapterPosition].notificationUID)
                bundle.putString("Type", dataList[holder.adapterPosition].notificationType)

                val navController = holder.itemView.findNavController()
                //first_type
                if(holder.recType.text=="first_type") {
                    navController.navigate(
                        R.id.action_nav_notificationOrganization_to_nav_notificationDetailOrganization,
                        bundle
                    )
                }

                //second_type
                if(holder.recType.text=="second_type") {
                    navController.navigate(R.id.action_nav_notificationOrganization_to_nav_notificationDetail2, bundle)
                }
                //navController.navigate(R.id.action_nav_notificationOrganization_to_nav_notificationDetail2, bundle)
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
    var recUID: TextView
    var recCard: CardView
    init {
        recImage = itemView.findViewById(R.id.recImage)
        recTitle = itemView.findViewById(R.id.recTitle)
        recDesc = itemView.findViewById(R.id.recDesc)
        recTime = itemView.findViewById(R.id.recPriority)
        recCard = itemView.findViewById(R.id.recCard)
        recUID = itemView.findViewById(R.id.recUID)
        recType = itemView.findViewById(R.id.recType)
    }
}