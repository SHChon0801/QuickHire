package my.edu.tarc.quickhire.ui.Profile

import android.os.Bundle
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        val editButton=binding.editDetail
        val logOutButton=binding.buttonLogOut

        //ArrayList
//        userList = ArrayList<UserSkills>()
//        educateList = ArrayList<UserEdu>()


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
        val datab = FirebaseDatabase.getInstance("https://quickhire-409e0-default-rtdb.asia-southeast1.firebasedatabase.app/")

        val dataRef = datab.getReference("Organizations").child(auth.currentUser!!.uid)
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value!=null) {
                    val name = snapshot.child("name").value as String
                    val about = snapshot.child("about").value as String
                    val state = snapshot.child("state").value as String
                    val currentJob = snapshot.child("currentJob").value as String
                    val email = snapshot.child("email").value as String
                    var phone = snapshot.child("phone").value as String
                    val timePrefer = snapshot.child("timePrefer").value as String
                    val education=snapshot.child("education").value as String
                    val skill=snapshot.child("skill").value as String
                    val profilePic = snapshot.child("profilePic").value as String



                    //set data
                    binding.name.text = name
                    binding.about.text = about
                    binding.currentJob.text = currentJob
                    binding.stated.text = state
                    binding.TimePrefer.text = timePrefer
                    binding.Email.text = email
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

//    fun onDataChange(dataSnapshot: DataSnapshot) {
//        for (keyId in dataSnapshot.children) {
//            if (keyId.child("email").value == email) {
//                fname = keyId.child("fullName").getValue<String>(String::class.java)
//                profession = keyId.child("profession").getValue<String>(String::class.java)
//                workplace = keyId.child("workplace").getValue<String>(String::class.java)
//                phone = keyId.child("phone").getValue<String>(String::class.java)
//                facebook = keyId.child("facebook").getValue<String>(String::class.java)
//                twitter = keyId.child("twitter").getValue<String>(String::class.java)
//                break
//            }
//        }
//        nameTxtView.setText(fname)
//        emailTxtView.setText(email)
//        occupationTxtView.setText(profession)
//        workTxtView.setText(workplace)
//        phoneTxtView.setText(phone)
//        videoTxtView.setText(phone)
//        facebookTxtView.setText(facebook)
//        twitterTxtView.setText(twitter)
//    }

//    private fun loadSkill(){
//        val currentUser = FirebaseAuth.getInstance().currentUser
//        val datab = FirebaseDatabase.getInstance("https://job-alley-3f825-default-rtdb.firebaseio.com/")
//        val dataRef = datab.getReference("Users").child(currentUser!!.uid).child("skill")
//        val skillListener = object :ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//
//                if(snapshot.exists()){
//                    for(skillSnapshot in snapshot.children){
//                        val usersSkill = skillSnapshot.getValue(UserSkills::class.java)
//
//                        if (usersSkill != null) {
//                            userList.add(usersSkill)
//                        }
//                        userAdapter.notifyDataSetChanged()
//                    }
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(requireContext(), "Retrieved failed", Toast.LENGTH_SHORT).show()
//            }
//
//        }
//        dataRef.addListenerForSingleValueEvent(skillListener)
//    }
//    private fun loadEdu(){
//        val currentUser = FirebaseAuth.getInstance().currentUser
//        val datab = FirebaseDatabase.getInstance("https://job-alley-3f825-default-rtdb.firebaseio.com/")
//        val dataRef = datab.getReference("Users").child(currentUser!!.uid).child("education")
//        val eduListener = object :ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val TAG: String = "EducationFragment"
//                Log.d(TAG, snapshot.exists().toString())
//                if(snapshot.exists()){
//                    for(eduSnapshot in snapshot.children){
////                       val sch = snapshot.child("uschool").value as String
////                        val educate = eduSnapshot.child("uSchool").getValue()
//                        Log.d(TAG, currentUser.uid)
//                        val educate = eduSnapshot.getValue(UserEdu::class.java)
//                        Log.d(TAG, eduSnapshot.toString())
//                        Log.d(TAG, educate.toString())
//                        if (educate != null) {
//                            educateList.add(educate)
//                        }
//                        eduAdapter.notifyDataSetChanged()
//                    }
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(requireContext(), "Retrieved failed", Toast.LENGTH_SHORT).show()
//            }
//
//        }
//        dataRef.addListenerForSingleValueEvent(eduListener)
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}