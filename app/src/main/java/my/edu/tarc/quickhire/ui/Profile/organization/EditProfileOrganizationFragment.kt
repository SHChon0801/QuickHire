package my.edu.tarc.quickhire.ui.Profile.organization

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
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.databinding.FragmentEditProfileBinding
import my.edu.tarc.quickhire.databinding.FragmentEditProfileOrganizationBinding


class EditProfileOrganizationFragment : Fragment() {
    private lateinit var binding: FragmentEditProfileOrganizationBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var imageUri: Uri? = null
    private lateinit var uid: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentEditProfileOrganizationBinding.inflate(inflater,container,false)

        val backButton=binding.back
        val changePhoto=binding.chgProf
        val confirmButton: Button =binding.doneProfile
        val cancelButton: Button =binding.cancelProfile

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
            val name = binding.inputName.text.toString()
            val email = binding.inputMail.text.toString()
            val telNo = binding.inputPhone.text.toString()
            val job=binding.inputJobProvided.text.toString()
            val address=binding.inputAddress.text.toString()
            val about=binding.inputDes.text.toString()
            val uri = imageUri.toString()

            val user = FirebaseAuth.getInstance().currentUser
            val userEmail = user?.email

            val encodedEmail = userEmail?.replace(".","-")
            val dataRef = FirebaseDatabase.getInstance().reference.child("Organizations").child(encodedEmail?:"")

//            database =
//                FirebaseDatabase.getInstance("https://quickhire-409e0-default-rtdb.asia-southeast1.firebasedatabase.app/")
//                    .getReference("Organizations")

            if (currentUser != null) {
                dataRef.child("name").setValue(name)
                dataRef.child("about").setValue(about)
                dataRef.child("job").setValue(job)
                dataRef.child("address").setValue(address)
                dataRef.child("email").setValue(email)
                dataRef.child("phone").setValue(telNo)
                dataRef.child("profilePic").setValue(uri)

            }
            Toast.makeText(requireContext(), "Profile Updated Successfully!!", Toast.LENGTH_SHORT)
                .show()
            findNavController().navigate(R.id.action_nav_editProfileOrganization_to_nav_profileOrganization)
        }

        cancelButton.setOnClickListener{
            findNavController().navigate(R.id.action_nav_editProfileOrganization_to_nav_profileOrganization)
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

    private fun loadUserInfo(){
        val datab = FirebaseDatabase.getInstance("https://quickhire-409e0-default-rtdb.asia-southeast1.firebasedatabase.app/")

        val user = FirebaseAuth.getInstance().currentUser
        val userEmail = user?.email

        val encodedEmail = userEmail?.replace(".","-")
        val dataRef = FirebaseDatabase.getInstance().reference.child("Organizations").child(encodedEmail?:"")



       // val dataRef = datab.getReference("Organizations").child(auth.currentUser!!.uid)
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value!=null) {
                    val name = snapshot.child("orgName").value as String
                    val about = snapshot.child("about").value as String
                    val job = snapshot.child("job").value as String
                    val address = snapshot.child("address").value as String
                    val email = snapshot.child("email").value as String
                    var phone = snapshot.child("phone").value as String

                    dataRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {

                            binding.inputName.setText(name)
                            binding.inputDes.setText(about)
                            binding.inputJobProvided.setText(job)
                            binding.inputAddress.setText(address)
                            binding.inputMail.setText(email)
                            binding.inputPhone.setText(phone)
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

        val user = FirebaseAuth.getInstance().currentUser
        val userEmail = user?.email

        val encodedEmail = userEmail?.replace(".","-")
        val dataRef = FirebaseDatabase.getInstance().reference.child("Organizations").child(encodedEmail?:"")


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



    override fun onDestroyView() {
        super.onDestroyView()
        //_binding = null
    }
}