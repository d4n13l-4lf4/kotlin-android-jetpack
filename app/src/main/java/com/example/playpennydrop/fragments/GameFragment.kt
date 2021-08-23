package com.example.playpennydrop.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.playpennydrop.R
import com.example.playpennydrop.databinding.FragmentGameBinding
import com.example.playpennydrop.viewmodels.GameViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GameFragment : Fragment() {

    private val gameViewModel by activityViewModels<GameViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentGameBinding.inflate(inflater, container, false).apply {
            vm = gameViewModel
            textCurrentTurnInfo.movementMethod = ScrollingMovementMethod()
            lifecycleOwner = viewLifecycleOwner
        }

        val dialog = AlertDialog.Builder(activity)
            .setTitle("New Game?")
            .setIcon(R.drawable.mdi_dice_6_24dp)
            .setMessage("Same players or new players?")
            .setPositiveButton("Same Players") { _, _ ->
                //goToPickPlayers()
            }
            .setNeutralButton("Cancel") { _, _ ->

            }.create()

        dialog.show()

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GameFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GameFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}