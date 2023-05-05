package my.edu.tarc.quickhire.ui.SearchandPost

import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.databinding.ActivityRegisterBinding
import my.edu.tarc.quickhire.databinding.FragmentEditProfileBinding
import my.edu.tarc.quickhire.databinding.FragmentPostingBinding

class PostingFragment : Fragment() {
    private lateinit var binding: FragmentPostingBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //_binding= FragmentEditProfileBinding.inflate(inflater,container,false)
        binding= FragmentPostingBinding.inflate(inflater,container,false)
        //firebase auth
        auth = FirebaseAuth.getInstance()
        return binding.root
    }


}