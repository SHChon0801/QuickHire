package my.edu.tarc.quickhire.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.quickhire.R

class OrganizationHomeAdapter(private val dataList: ArrayList<OrganizationJob>): RecyclerView.Adapter<OrganizationHomeAdapter.HomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_organization_job, parent, false)
        return HomeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.rvJobImage.setImageResource(currentItem.jobImage)
        holder.rvJobName.text = currentItem.jobName
    }

    class HomeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val rvJobImage: ImageView = itemView.findViewById(R.id.organizationJobImage)
        val rvJobName: TextView = itemView.findViewById(R.id.organizationJobName)
    }


}