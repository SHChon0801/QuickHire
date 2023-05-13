package my.edu.tarc.quickhire.ui.notifications.organization

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.databinding.FragmentNotificationDetail2Binding
import my.edu.tarc.quickhire.databinding.FragmentUserDetailBinding

class UserDetailFragment : Fragment() {

    private var _binding: FragmentUserDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUserDetailBinding.inflate(inflater, container, false)

        return binding.root
    }


}