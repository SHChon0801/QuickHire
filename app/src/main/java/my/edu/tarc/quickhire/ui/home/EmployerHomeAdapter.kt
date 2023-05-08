package my.edu.tarc.quickhire.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import my.edu.tarc.quickhire.R

class EmployerHomeAdapter(private val dataList: ArrayList<EmployerJob>): RecyclerView.Adapter<EmployerHomeAdapter.HomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_employer_job, parent, false)
        return HomeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val currentItem = dataList[position]
        Glide.with(holder.rvJobImage.context).load(currentItem.jobImage).into(holder.rvJobImage)
        holder.rvJobName.text = currentItem.jobName
        holder.rvJobDescription.text = currentItem.jobDescription
        holder.rvJobPayRate.text = currentItem.jobPayRate.toString()
    }

    class HomeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val rvJobImage: ImageView = itemView.findViewById(R.id.employerJobImage)
        val rvJobName: TextView = itemView.findViewById(R.id.employerJobName)
        val rvJobDescription: TextView = itemView.findViewById(R.id.employerJobDescription)
        val rvJobPayRate: TextView = itemView.findViewById(R.id.employerJobPayRate)
    }
}