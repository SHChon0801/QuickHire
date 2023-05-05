package my.edu.tarc.quickhire.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import my.edu.tarc.quickhire.MainActivity
import my.edu.tarc.quickhire.databinding.ActivityLoginBinding
import my.edu.tarc.quickhire.ui.register.NewRegisterActivity
import my.edu.tarc.quickhire.ui.register.RegisterActivity
import androidx.navigation.findNavController
import my.edu.tarc.quickhire.R

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()


        binding.btnLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if(it.isSuccessful){
                        val intent = Intent(this,MainActivity::class.java)
                        startActivity(intent)
                    }else{
                        Toast.makeText(this,it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }

                }
            }else{
                Toast.makeText(this,"Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }

        }



        binding.textViewRegisterPage.setOnClickListener {
            val intent = Intent(this, NewRegisterActivity::class.java)
            startActivity(intent)

        }
    }
}





































