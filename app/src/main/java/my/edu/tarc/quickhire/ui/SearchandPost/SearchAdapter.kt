package my.edu.tarc.quickhire.ui.SearchandPost

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.ui.home.EmployerHomeAdapter
import my.edu.tarc.quickhire.ui.home.EmployerJob

class SearchAdapter(private val jobs: List<EmployerJob>) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_employer_job, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val job = jobs[position]
        holder.bind(job)
    }

    override fun getItemCount(): Int {
        return jobs.size
    }

    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val jobImage: ImageView = itemView.findViewById(R.id.employerJobImage)
        private val jobNameTextView: TextView = itemView.findViewById(R.id.employerJobName)
        private val jobDescriptionTextView: TextView = itemView.findViewById(R.id.employerJobDescription)

        fun bind(job: EmployerJob) {
            Glide.with(jobImage.context).load(job.jobImage).into(jobImage)
            jobNameTextView.text = job.jobName
            jobDescriptionTextView.text = job.jobDescription
        }
    }
}

