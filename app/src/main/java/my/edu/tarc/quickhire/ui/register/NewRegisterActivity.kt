package my.edu.tarc.quickhire.ui.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import my.edu.tarc.quickhire.databinding.ActivityLoginBinding
import my.edu.tarc.quickhire.databinding.ActivityNewRegisterBinding

class NewRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)






    }
}