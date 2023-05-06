package my.edu.tarc.quickhire.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.databinding.FragmentRegisterPage2Binding
import my.edu.tarc.quickhire.databinding.FragmentRegisterPage3Binding


class RegisterPage3Fragment : Fragment() {
    private lateinit var binding: FragmentRegisterPage3Binding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReferenceOrg: DatabaseReference
    private lateinit var database: FirebaseDatabase




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterPage3Binding.inflate(inflater,container,false)


        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReferenceOrg = database.getReference("Organization")


        binding.btnOrgReg.setOnClickListener {
            val email = binding.editTextOrgEmail.text.toString()
            val password = binding.editTextOrgPass.text.toString()
            val confirmPassword = binding.editTextOrgConfirmPass.text.toString()
            val name = binding.editTextOrgName.text.toString()
            var role = "Organization"

            if(email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() &&
                name.isNotEmpty()){
                if(password == confirmPassword){
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                        if(it.isSuccessful){

                        }  else{
                            showToast(it.exception.toString(),Toast.LENGTH_SHORT)
                        }
                    }
                }else{
                    showToast("Passwords do not match",Toast.LENGTH_SHORT)
                }
            }else{
                showToast("Fields cannot be empty",Toast.LENGTH_SHORT)
            }

        }








        return binding.root
    }


    private fun showToast(message: String, length: Int) {
        val context = context
        if (context != null) {
            Toast.makeText(context, message, length).show()
        }
    }

}