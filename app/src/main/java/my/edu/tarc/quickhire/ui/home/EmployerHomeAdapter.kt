package my.edu.tarc.quickhire.ui.home

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.databinding.RecyclerEmployerJobBinding

class EmployerHomeAdapter(private val dataList: List<Job>): RecyclerView.Adapter<EmployerHomeAdapter.HomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = RecyclerEmployerJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val user = FirebaseAuth.getInstance().currentUser
        val currentItem = dataList[position]
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = currentItem.jobImage.let { storageRef.child(it.toString()) }
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            // Load the image using Glide
            Glide.with(holder.binding.root.context)
                .load(uri)
                .into(holder.binding.employerJobImage)
        }.addOnFailureListener { exception ->
            // Handle any errors
            Log.e(ContentValues.TAG, "Failed to retrieve image download URL: ${exception.message}")
        }
        holder.binding.employerJobImage.contentDescription = currentItem.jobName
        holder.binding.employerJobName.text = currentItem.jobName
        holder.binding.employerJobDescription.text = currentItem.jobDescription
        holder.binding.employerJobPayRate.text = "Pay Rate Per Hour: " + currentItem.jobPayRate.toString()
        if(currentItem.emailIDApplied != null){
            if(currentItem.emailIDApplied!!.contains(user!!.email)){
                holder.binding.applyButton.isEnabled = false
            }
        }
        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putString("jobImage", currentItem.jobImage)
                putString("jobName", currentItem.jobName)
                putString("jobDescription", currentItem.jobDescription)
                putString("jobArea", currentItem.jobArea)
                putString("jobSpecialist", currentItem.jobSpecialist)
                putInt("jobID", currentItem.jobID!!)
                currentItem.jobPayRate?.let { it1 -> putDouble("jobPayRate", it1) }
                putStringArrayList("emailIDApplied", currentItem.emailIDApplied)
            }
            holder.itemView.findNavController().navigate(R.id.action_nav_employer_home_to_employerHomeDetailFragment, bundle)
        }
    }


    class HomeViewHolder(val binding: RecyclerEmployerJobBinding): RecyclerView.ViewHolder(binding.root)
}