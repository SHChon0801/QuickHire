package my.edu.tarc.quickhire.ui.notifications

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import my.edu.tarc.quickhire.R

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import my.edu.tarc.quickhire.databinding.FragmentUploadBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.text.DateFormat
import java.util.Calendar

class UploadFragment : Fragment() {
    private var _binding: FragmentUploadBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    var imageURL:String?=null
    var uri: Uri?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentUploadBinding.inflate(inflater, container, false)

//        val activityResultLauncher=registerForActivityResult<Intent,ActivityResult>(
//            ActivityResultContracts.StartActivityForResult()){result->
//            if (result.resultCode==RESULT_OK){
//                val data=result.data
//                uri=data!!.data
//                binding.uploadImage.setImageURI(uri)
//
//            }else{
//                Toast.makeText(requireContext(),"No Image Selected",Toast.LENGTH_SHORT).show()
//
//            }
//        }
//
//        binding.uploadImage.setOnClickListener{
//            val photoPicker=Intent(Intent.ACTION_PICK)
//            photoPicker.type="image/*"
//            activityResultLauncher.launch(photoPicker)
//        }
//        binding.saveButton.setOnClickListener {
//            saveData()
//        }


        return binding.root
    }

//    private fun saveData() {
//        val storageReference=FirebaseStorage.getInstance().reference.child("Task Images")
//            .child(uri!!.lastPathSegment!!)
//
////        val builder=AlertDialog.Builder(requireActivity())
////        builder.setMessage("R.string.delete_record")
////        builder.setPositiveButton(
////            getString(R.string.delete),
////            {
////                    _,_ ->
////                val name = binding.editTextName.text.toString()
////                val phone = binding.editTextPhone.text.toString()
////                val newContact = Contact(name, phone)
////                myContactViewModel.deleteContact(newContact)
////                findNavController().navigateUp()
////            })
////        builder.setNegativeButton(
////            getString(android.R.string.cancel),
////            {
////                    _,_ ->
////                //DO NOTIHNG
////
////            }
////        ).create().show()
//
//        val builder=AlertDialog.Builder(requireContext())
//        builder.setCancelable(false)
//        builder.setView(R.layout.progress_layout)
//        val dialog=builder.create()
//        dialog.show()
//
//        storageReference.putFile(uri!!).addOnSuccessListener { taskSnapshot->
//            val uriTask=taskSnapshot.storage.downloadUrl
//            while(!uriTask.isComplete);
//            val uriImage=uriTask.result
//            imageURL=uriImage.toString()
//            uploadData()
//            dialog.dismiss()
//        }.addOnFailureListener{
//            dialog.dismiss()
//        }
//    }
//
//    private fun uploadData(){
//        val title = binding.uploadTitle.text.toString()
//        val desc = binding.uploadDesc.text.toString()
//        val priority = binding.uploadPriority.text.toString()
//        val dataClass = NotificationData(title, desc, priority, imageURL)
//        val currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
//        FirebaseDatabase.getInstance().getReference("Notification").child(currentDate)
//            .setValue(dataClass).addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show()
//                    //requireActivity().finish()
//                }
//            }.addOnFailureListener { e ->
//                Toast.makeText(
//                    requireContext(), e.message.toString(), Toast.LENGTH_SHORT).show()
//                    //requireContext(), "Unsaved", Toast.LENGTH_SHORT).show()
//            }
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}