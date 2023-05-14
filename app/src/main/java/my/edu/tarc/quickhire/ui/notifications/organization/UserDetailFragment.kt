package my.edu.tarc.quickhire.ui.notifications.organization

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.databinding.FragmentNotificationDetail2Binding
import my.edu.tarc.quickhire.databinding.FragmentUserDetailBinding
import my.edu.tarc.quickhire.ui.notifications.NotificationData
import java.lang.Exception
import java.util.ArrayList

class UserDetailFragment : Fragment() {

    private var _binding: FragmentUserDetailBinding? = null
    private val binding get() = _binding!!

    //Recycler View
    private lateinit var notificationRecv: RecyclerView
    private lateinit var notificationList: ArrayList<NotificationData>
    private lateinit var notificationAdapter: NotificationAdapterOrganization


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUserDetailBinding.inflate(inflater, container, false)


        loadInform()

        binding.back.setOnClickListener{
            findNavController().navigate(R.id.action_nav_userDetail_to_nav_notificationDetail2)
        }

        return binding.root
    }

    private fun loadInform() {

        val UID = arguments?.getString("UID")
        val encodedEmail = UID?.replace(".","-")

        val dataRef = FirebaseDatabase.getInstance().reference.child("Employees").child(encodedEmail?:"")

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value!=null) {
                    val firstname = snapshot.child("firstName").value
                    if (firstname != null) {
                        val firstNameString = firstname as String
                        binding.name.text = firstNameString
                        // Use firstNameString as needed
                    } else {
                        // Handle null case
                    }

                    val lastname = snapshot.child("lastName").value as String?
                    val state = snapshot.child("state").value
                    if (state != null) {
                        val stateString = state as String
                        binding.stated.text = stateString
                        // Use firstNameString as needed
                    } else {
                        // Handle null case
                    }

                    val email1 = snapshot.child("email").value
                    if (email1 != null) {
                        val emailString = email1 as String
                        binding.Email.text = emailString
                        // Use firstNameString as needed
                    } else {
                        // Handle null case
                    }
                    val phone = snapshot.child("phone").value as String?
                    val timePrefer = snapshot.child("timePrefer").value as String?
                    val education=snapshot.child("education").value as String?
                    val skill=snapshot.child("skill").value as String?
                    val profilePic = snapshot.child("profilePic").value as String?

                    //set data
                    //binding.name.text = firstname+" "+lastname
                    //binding.stated.text = state
                    binding.TimePrefer.text = timePrefer
                    //binding.Email.text = email1
                    binding.ContactNumber.text = phone
                    binding.educational.text = education
                    binding.skill.text = skill

                    //set image
                    try{
                        Glide.with(requireContext())
                            .load(profilePic)
                            .placeholder(R.drawable.profileunknown)
                            .into(binding.profileImage)
                    }catch (e: Exception){

                    }
                }else{
                    Toast.makeText(requireContext(), "Retrieved failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        dataRef.addListenerForSingleValueEvent(eventListener)
    }


}