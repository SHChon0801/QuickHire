package my.edu.tarc.quickhire.ui.Profile


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference
import my.edu.tarc.quickhire.databinding.FragmentEditProfileBinding
import my.edu.tarc.quickhire.R



class EditProfileFragment : Fragment() {
//    private var _binding: FragmentEditProfileBinding? = null
//    private val binding get() = _binding!!
    private lateinit var binding: FragmentEditProfileBinding

    // This property is only valid between onCreateView and
    // onDestroyView.
    //private lateinit var binding: FragmentEditProfileBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var imageUri: Uri? = null
    private lateinit var uid: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //_binding= FragmentEditProfileBinding.inflate(inflater,container,false)
        binding= FragmentEditProfileBinding.inflate(inflater,container,false)

        val backButton=binding.back
        val changePhoto=binding.chgProf
        val confirmButton:Button=binding.doneProfile
        val cancelButton:Button=binding.cancelProfile

        //firebase auth
        auth = FirebaseAuth.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        loadUserInfo()
        loadPic()


        backButton.setOnClickListener{
            findNavController().navigateUp()
        }

        changePhoto.setOnClickListener{
            pickImageGallery()

        }

        confirmButton.setOnClickListener{
            val firstname = binding.inputFirstName.text.toString()
            val lastname = binding.inputLastName.text.toString()
            val email = binding.inputMail.text.toString()
            val telNo = binding.inputPhone.text.toString()
            val state=binding.statedEdit.text.toString()
            val currentJob=binding.currentJobEdit.text.toString()
            val timePrefer=binding.preferTimeFromEdit.text.toString()+" to "+binding.preferTimeEndEdit.text.toString()
            val education=binding.educationEdit.text.toString()
            val skill=binding.skillEdit.text.toString()
            val about=binding.inputDes.text.toString()
            val uri = imageUri.toString()


            validateName()
            validateTel()
            validateEmail()


            val user = FirebaseAuth.getInstance().currentUser
            val userEmail = user?.email

            val encodedEmail = userEmail?.replace(".","-")
            val dataRef = FirebaseDatabase.getInstance().reference.child("Employees").child(encodedEmail?:"")


            database =
                FirebaseDatabase.getInstance("https://quickhire-409e0-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("Employees")

            if (currentUser != null) {
                dataRef.child("firstName").setValue(firstname)
                dataRef.child("lastName").setValue(lastname)
                dataRef.child("about").setValue(about)
                dataRef.child("state").setValue(state)
                dataRef.child("currentJob").setValue(currentJob)
                dataRef.child("email").setValue(email)
                dataRef.child("phone").setValue(telNo)
                dataRef.child("timePrefer").setValue(timePrefer)
                dataRef.child("education").setValue(education)
                dataRef.child("skill").setValue(skill)
                dataRef.child("profilePic").setValue(uri)

            }
            Toast.makeText(requireContext(), "Profile Updated Successfully!!", Toast.LENGTH_SHORT)
                .show()
            findNavController().navigate(R.id.action_nav_editProfile_to_nav_profileFragment)
        }

        cancelButton.setOnClickListener{
            findNavController().navigate(R.id.action_nav_editProfile_to_nav_profileFragment)
        }

        return binding.root
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            //get uri of the image
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                imageUri = data!!.data
                //binding.profileImage.setImageURI(imageUri)
                Glide.with(requireContext())
                    .load(imageUri)
                    .into(binding.profileImage)

                val TAG: String = "EditProfileFragment"
                Log.d(TAG, imageUri.toString())
            } else {
                Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_SHORT).show()
            }

        }
    )

    private fun loadUserInfo(){
        val datab = FirebaseDatabase.getInstance("https://quickhire-409e0-default-rtdb.asia-southeast1.firebasedatabase.app/")


        val user = FirebaseAuth.getInstance().currentUser
        val userEmail = user?.email

        val encodedEmail = userEmail?.replace(".","-")
        val dataRef = FirebaseDatabase.getInstance().reference.child("Employees").child(encodedEmail?:"")


        //val dataRef = datab.getReference("Employees").child(auth.currentUser!!.uid)
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value!=null) {
                    val firstname = snapshot.child("firstName").value as String
                    val lastname = snapshot.child("lastName").value as String
                    val about = snapshot.child("about").value as String
                    val state = snapshot.child("state").value as String
                    val currentJob = snapshot.child("currentJob").value as String
                    val email = snapshot.child("email").value as String
                    var phone = snapshot.child("phone").value as String
                    val timePrefer = snapshot.child("timePrefer").value as String
                    val education=snapshot.child("education").value as String
                    val skill=snapshot.child("skill").value as String

                    //Split Time
                    val (fromTime, toTime) = timePrefer.split(" to ")

                    dataRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {

                            binding.inputFirstName.setText(firstname)
                            binding.inputLastName.setText(lastname)
                            binding.inputDes.setText(about)
                            binding.statedEdit.setText(state)
                            binding.currentJobEdit.setText(currentJob)
                            binding.inputMail.setText(email)
                            binding.preferTimeFromEdit.setText(fromTime)
                            binding.preferTimeEndEdit.setText(toTime)
                            binding.inputPhone.setText(phone)
                            binding.educationEdit.setText(education)
                            binding.skillEdit.setText(skill)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle errors here
                        }
                    })


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

    private fun loadPic() {
//        val datab =
//            FirebaseDatabase.getInstance("https://quickhire-409e0-default-rtdb.asia-southeast1.firebasedatabase.app/")


        val user = FirebaseAuth.getInstance().currentUser
        val userEmail = user?.email

        val encodedEmail = userEmail?.replace(".","-")
        val dataRef = FirebaseDatabase.getInstance().reference.child("Employees").child(encodedEmail?:"")


       // val dataRef = datab.getReference("Employees").child(auth.currentUser!!.uid)
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value != null) {
                    val profilePic = snapshot.child("profilePic").value as String

                    //set image
                    try {
                        Glide.with(requireContext())
                            .load(profilePic)
                            .placeholder(R.drawable.profileunknown)
                            .into(binding.profileImage)
                    } catch (e: Exception) {

                    }
                } else {
                    Toast.makeText(requireContext(), "Retrieved failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        dataRef.addListenerForSingleValueEvent(eventListener)

    }


    private fun validateName() {
        val name = binding.inputFirstName.text.toString()
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(requireContext(), "Enter your name", Toast.LENGTH_SHORT).show()
        }
    }


    private fun validateTel() {
        val telNo = binding.inputPhone.text.toString()
        if (TextUtils.isEmpty(telNo)) {
            Toast.makeText(requireContext(), "Enter your telephone number", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun validateEmail() {
        val email = binding.inputMail.text.toString()
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(requireContext(), "Enter your email", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //_binding = null
    }
}