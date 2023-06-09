package my.edu.tarc.quickhire.ui.home

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.databinding.RecyclerOrganizationJobBinding

class OrganizationHomeAdapter(private val dataList: List<Job>): RecyclerView.Adapter<OrganizationHomeAdapter.HomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = RecyclerOrganizationJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val currentItem = dataList[position]
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = currentItem.jobImage.let { storageRef.child(it.toString()) }
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            // Load the image using Glide
            Glide.with(holder.binding.root.context)
                .load(uri)
                .into(holder.binding.organizationJobImage)
        }.addOnFailureListener { exception ->
            // Handle any errors
            Log.e(ContentValues.TAG, "Failed to retrieve image download URL: ${exception.message}")
        }
        holder.binding.organizationJobImage.contentDescription = currentItem.jobName
        holder.binding.organizationJobName.text = currentItem.jobName
        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                currentItem.jobID?.let { it1 -> putInt("jobId", it1) }
                putString("jobImage", currentItem.jobImage)
                putString("jobName", currentItem.jobName)
                putString("jobDescription", currentItem.jobDescription)
                putString("jobArea", currentItem.jobArea)
                putString("jobSpecialist", currentItem.jobSpecialist)
                currentItem.jobPayRate?.let { it1 -> putDouble("jobPayRate", it1) }
                putString("jobEmail", currentItem.jobEmail)
                currentItem.ignoredViewCount?.let { it1 -> putInt("jobViewCount", it1) }
            }
            holder.itemView.findNavController().navigate(R.id.action_nav_organization_home_to_organizationHomeDetailFragment, bundle)
        }

    }

    class HomeViewHolder(val binding: RecyclerOrganizationJobBinding): RecyclerView.ViewHolder(binding.root)


}