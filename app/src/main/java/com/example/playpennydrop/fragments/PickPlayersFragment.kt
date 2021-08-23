package com.example.playpennydrop.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playpennydrop.R
import com.example.playpennydrop.databinding.FragmentPickPlayersBinding
import com.example.playpennydrop.viewmodels.GameViewModel
import com.example.playpennydrop.viewmodels.PickPlayersViewModel
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PickPlayersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PickPlayersFragment : Fragment() {
    private val pickPlayersViewModel by activityViewModels<PickPlayersViewModel>()
    private val gameViewModel by activityViewModels<GameViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPickPlayersBinding
            .inflate(inflater, container, false)
            .apply {
                this.vm = pickPlayersViewModel
                this.buttonPlayGame.setOnClickListener {
                    viewLifecycleOwner.lifecycleScope.launch {
                        gameViewModel.startGame(
                        pickPlayersViewModel.players.value
                            ?.filter { newPlayer ->
                                newPlayer.isIncluded.get()
                            }
                            ?.map { newPlayer ->
                                newPlayer.toPlayer()
                            } ?: emptyList()
                        )

                        findNavController().navigate(R.id.gameFragment)
                    }
                }
            }

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PickPlayersFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PickPlayersFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}