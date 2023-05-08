package my.edu.tarc.quickhire.ui.Profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.databinding.FragmentProfileBinding
import kotlin.math.log
import java.lang.Exception

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //firebase auth
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        val editButton=binding.editDetail
        val logOutButton=binding.buttonLogOut

        //Authenticator
        auth = FirebaseAuth.getInstance()

        loadUserInfo()

        editButton.setOnClickListener{
            findNavController().navigate(R.id.nav_editProfile)
        }

        logOutButton.setOnClickListener {
            auth.signOut()
            findNavController().navigate(R.id.nav_loginActivity)
            Toast.makeText(requireContext(),"Log Out Successfully",Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private fun loadUserInfo(){
        //val datab = FirebaseDatabase.getInstance("https://quickhire-409e0-default-rtdb.asia-southeast1.firebasedatabase.app/")

        val user = FirebaseAuth.getInstance().currentUser
        val userEmail = user?.email

        val encodedEmail = userEmail?.replace(".","-")
        val dataRef = FirebaseDatabase.getInstance().reference.child("Employees").child(encodedEmail?:"")

        //val dataRef = datab.getReference("Employees").child(emailUid)

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value!=null) {
                    val firstname = snapshot.child("firstName").value as String
                    val lastname = snapshot.child("lastName").value as String
                    val about = snapshot.child("about").value as String
                    val state = snapshot.child("state").value as String
                    val currentJob = snapshot.child("currentJob").value as String
                    val email1 = snapshot.child("email").value as String
                    val phone = snapshot.child("phone").value as String
                    val timePrefer = snapshot.child("timePrefer").value as String
                    val education=snapshot.child("education").value as String
                    val skill=snapshot.child("skill").value as String
                    val profilePic = snapshot.child("profilePic").value as String



                    //set data
                    binding.name.text = firstname+(" ")+lastname
                    binding.about.text = about
                    binding.currentJob.text = currentJob
                    binding.stated.text = state
                    binding.TimePrefer.text = timePrefer
                    binding.Email.text = email1
                    binding.ContactNumber.text = phone
                    binding.educational.text = education
                    binding.skill.text = skill

                    //set image
                    try{
                        Glide.with(requireContext())
                            .load(profilePic)
                            .placeholder(R.drawable.profileunknown)
                            .into(binding.imageProfile)
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}