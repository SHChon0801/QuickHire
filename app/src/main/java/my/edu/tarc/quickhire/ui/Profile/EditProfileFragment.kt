package my.edu.tarc.quickhire.ui.Profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quickhiretest.R
import com.example.quickhiretest.databinding.FragmentEditProfileBinding
import com.example.quickhiretest.databinding.FragmentProfileBinding
import com.example.quickhiretest.ui.Profile.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference
import my.edu.tarc.quickhire.databinding.FragmentEditProfileBinding
import my.edu.tarc.quickhire.databinding.FragmentProfileBinding
import java.lang.Exception
import java.util.ArrayList

class EditProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private lateinit var binding: FragmentEditProfileBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var imageUri: Uri? = null
    private lateinit var uid: String



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentProfileBinding.inflate(inflater,container,false)

        val backButton=binding.back
        val changePhoto=binding.chgProf
        val confirmButton=binding.doneProfile
        val cancelButton=binding.cancelProfile

        //firebase auth
        auth = FirebaseAuth.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        loadPic()

        backButton.setOnClickListener{
            findNavController().navigateUp()
        }

        changePhoto.setOnClickListener{
            pickImageGallery()
        }

        confirmButton.setOnClickListener{
            val name = binding.inputName.text.toString()
            val email = binding.inputMail.text.toString()
            val telNo = binding.inputPhone.text.toString()
            val state=binding.statedEdit.text.toString()
            val currentJob=binding.currentJobEdit.text.toString()
            val timePrefer=binding.preferTimeEdit.text.toString()
            val education=binding.educationEdit.text.toString()
            val skill=binding.skillEdit.text.toString()
            val about=binding.inputDes.text.toString()
            val uri = imageUri.toString()


            validateName()
            validateTel()
            validateEmail()

            database =
                FirebaseDatabase.getInstance("https://quickhiretest-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("Users")

            if (currentUser != null) {
                database.child(currentUser.uid).child("name").setValue(name)
                database.child(currentUser.uid).child("about").setValue(about)
                database.child(currentUser.uid).child("state").setValue(state)
                database.child(currentUser.uid).child("job").setValue(currentJob)
                database.child(currentUser.uid).child("email").setValue(email)
                database.child(currentUser.uid).child("phone").setValue(telNo)
                database.child(currentUser.uid).child("time").setValue(timePrefer)
                database.child(currentUser.uid).child("education").setValue(education)
                database.child(currentUser.uid).child("skill").setValue(skill)
                database.child(currentUser.uid).child("profilePic").setValue(uri)

            }
            Toast.makeText(requireContext(), "Profile Updated Successfully!!", Toast.LENGTH_SHORT)
                .show()
            findNavController().navigate(R.id.action_nav)
        }

        cancelButton.setOnClickListener{
            findNavController().navigate(R.id.action_nav_editProfile_to_nav_profile)
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
                binding.profileImage.setImageURI(imageUri)
                val TAG: String = "EditProfileFragment"
                Log.d(TAG, imageUri.toString())
            } else {
                Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_SHORT).show()
            }

        }
    )

    private fun loadPic() {
        val datab =
            FirebaseDatabase.getInstance("https://quickhiretest-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val dataRef = datab.getReference("Users").child(auth.currentUser!!.uid)
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
        val name = binding.inputName.text.toString()
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
        _binding = null
    }
}