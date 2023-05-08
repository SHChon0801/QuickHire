package my.edu.tarc.quickhire.ui.register

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import my.edu.tarc.quickhire.Employee
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.databinding.FragmentRegisterPage1Binding
import my.edu.tarc.quickhire.databinding.FragmentRegisterPage2Binding
import my.edu.tarc.quickhire.ui.login.LoginActivity


class RegisterPage2Fragment : Fragment() {
    private lateinit var binding: FragmentRegisterPage2Binding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReferenceEmp: DatabaseReference
    private lateinit var database: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterPage2Binding.inflate(inflater,container,false)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReferenceEmp = database.getReference("Employees")

        val currentUser = FirebaseAuth.getInstance().currentUser


        binding.btnEmpReg.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPass.text.toString()
            val confirmPassword = binding.editTextConfirmPass.text.toString()
            val firstName = binding.editTextFirstName.text.toString()
            val lastName = binding.editTextLastName.text.toString()
            val phone = binding.editTextPhone.text.toString()
            val role = "Employee"

            if(email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() &&
                    firstName.isNotEmpty() && lastName.isNotEmpty()){
                if(password == confirmPassword){
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                        if(it.isSuccessful){

                            val newEmp = Employee(email,password,firstName,lastName,phone,role)
                            if(currentUser!=null){

                                databaseReferenceEmp.child(currentUser.uid).setValue(newEmp)


                                val employeeRef = databaseReferenceEmp.push()

                                employeeRef.setValue(newEmp).addOnCompleteListener { task ->
                                    if (task.isSuccessful){
                                        val employeeId = employeeRef.key



                                        val newemployeeRef = databaseReferenceEmp.child(employeeId?:"")
                                        newemployeeRef.addListenerForSingleValueEvent(object :
                                            ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                if (snapshot.exists()) {
                                                    val employee = snapshot.getValue(Employee::class.java)
                                                    val role = employee?.role // Extract the role value from the employee object
                                                    val intent = Intent(activity, LoginActivity::class.java)
                                                    intent.putExtra("empRole",role)
                                                    startActivity(intent)

                                                    binding.textView16.setText(role)

                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                // Handle the error
                                            }
                                        })




                                    }


                                }


                            }



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

