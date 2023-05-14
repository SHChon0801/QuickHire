package my.edu.tarc.quickhire.ui.Profile.organization

import android.content.ContentValues
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
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.databinding.FragmentProfileBinding
import my.edu.tarc.quickhire.databinding.FragmentProfileOrganizationBinding
import my.edu.tarc.quickhire.ui.home.Job
import my.edu.tarc.quickhire.ui.home.OrganizationHomeAdapter
import java.lang.Exception

class ProfileOrganizationFragment : Fragment() {
    private var _binding: FragmentProfileOrganizationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //Recycle view Stuff
    private lateinit var eduRv: RecyclerView
    //private lateinit var educateList: ArrayList<UserEdu>
    //private lateinit var eduAdapter:EduAdapter
    private lateinit var recv: RecyclerView
    //private lateinit var userList: ArrayList<UserSkills>
    //private lateinit var userAdapter: UserAdapter

    private lateinit var database: DatabaseReference
    private lateinit var nameInput: TextView
    private lateinit var aboutInput: TextView
    private lateinit var currentJobInput: TextView
    private lateinit var statedInput: TextView
    private lateinit var timePreferInput: TextView
    private lateinit var emailInput: TextView
    private lateinit var phoneInput: TextView
    private lateinit var educationInput: TextView
    private lateinit var skillInput: TextView


    private lateinit var navController: NavController
    private lateinit var uid: String

    //firebase auth
    private lateinit var auth: FirebaseAuth

    private val databaseNew = Firebase.database.reference
    private lateinit var dataList: ArrayList<Job>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileOrganizationBinding.inflate(inflater, container, false)

        val editButton=binding.editDetail
        val logOutButton=binding.buttonLogOut

        //ArrayList
//        userList = ArrayList<UserSkills>()
//        educateList = ArrayList<UserEdu>()


        //Authenticator
        auth = FirebaseAuth.getInstance()
        loadUserInfo()

        editButton.setOnClickListener{
            findNavController().navigate(R.id.nav_editProfileOrganization)
        }

        logOutButton.setOnClickListener {
            auth.signOut()
            findNavController().navigate(R.id.nav_loginActivity)
            Toast.makeText(requireContext(),"Log Out Successfully", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private fun loadUserInfo(){
        //val datab = FirebaseDatabase.getInstance("https://quickhire-409e0-default-rtdb.asia-southeast1.firebasedatabase.app/")

        val user = FirebaseAuth.getInstance().currentUser
        val userEmail = user?.email

        val encodedEmail = userEmail?.replace(".","-")
        val dataRef = FirebaseDatabase.getInstance().reference.child("Organizations").child(encodedEmail?:"")

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value!=null) {
                    val name = snapshot.child("orgName").value as String
                    val about = snapshot.child("about").value as String
                    val job = snapshot.child("job").value as String
                    val address = snapshot.child("address").value as String
                    val email = snapshot.child("email").value as String
                    var phone = snapshot.child("phone").value as String
                    val profilePic = snapshot.child("profilePic").value as String

                    //set data
                    binding.name.text = name
                    binding.about.text = about
                    //binding.jobProvided.text = job
                    binding.address.text = address
                    binding.Email.text = email
                    binding.ContactNumber.text = phone

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

                //Calculate Number Of Jobs


                databaseNew.child("Jobs").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        val user = FirebaseAuth.getInstance().currentUser
                        var numberJob=0
                        for (jobSnapshot in snapshot.children) {
                            val job = jobSnapshot.getValue(Job::class.java)
                            job?.let{
                                if (user?.email == job.jobEmail) { // filter out jobs with current user's email

                                    numberJob+=1
                                }
                            }
                        }

                        binding.jobProvided.text=numberJob.toString()

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })



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