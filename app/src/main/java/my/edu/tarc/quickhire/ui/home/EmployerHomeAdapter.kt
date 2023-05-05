package my.edu.tarc.quickhire.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.quickhire.R

class EmployerHomeAdapter(private val dataList: ArrayList<Job>): RecyclerView.Adapter<EmployerHomeAdapter.HomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.job, parent, false)
        return HomeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.rvJobImage.setImageResource(currentItem.jobImage)
        holder.rvJobName.text = currentItem.jobName
        holder.rvJobDescription.text = currentItem.jobDescription
        holder.rvJobSpecialist.text = currentItem.jobSpecialist
        holder.rvJobPayRate.text = currentItem.jobPayRate.toString()
    }

    class HomeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val rvJobImage: ImageView = itemView.findViewById(R.id.jobImage)
        val rvJobName: TextView = itemView.findViewById(R.id.jobName)
        val rvJobDescription: TextView = itemView.findViewById(R.id.jobDescription)
        val rvJobSpecialist: TextView = itemView.findViewById(R.id.jobSpecialist)
        val rvJobPayRate: TextView = itemView.findViewById(R.id.jobPayRate)
    }
}