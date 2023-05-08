package my.edu.tarc.quickhire.ui.SearchandPost

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import my.edu.tarc.quickhire.R
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
        private val jobPayRate: TextView = itemView.findViewById(R.id.employerJobPayRate)
        fun bind(job: EmployerJob) {
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef =
                job.jobImage?.let { storageRef.child(it) } // Assuming job.jobImage contains the path to the image in Firebase Storage

            // Fetch the download URL of the image
            imageRef!!.downloadUrl.addOnSuccessListener { uri ->
                // Load the image using Glide
                Glide.with(jobImage.context)
                    .load(uri)
                    .into(jobImage)
            }.addOnFailureListener { exception ->
                // Handle any errors
                Log.e(TAG, "Failed to retrieve image download URL: ${exception.message}")
            }
            jobNameTextView.text = job.jobName
            jobDescriptionTextView.text = job.jobDescription
            jobPayRate.text = "Pay Rate Per Hours : ${job.jobPayRate} "
        }
    }
}

