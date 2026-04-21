package com.example.list_temp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.list_temp.data.BikeModel
import com.example.list_temp.databinding.FragmentBikeModelInputBinding
import com.example.list_temp.repository.VeloRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "bike_model_param"

class BikeModelInputFragment : Fragment() {
    private lateinit var bikeModel: BikeModel
    private lateinit var binding: FragmentBikeModelInputBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val param1 = it.getString(ARG_PARAM1)
            bikeModel = if (param1 == null) BikeModel()
            else {
                val paramType = object : TypeToken<BikeModel>() {}.type
                Gson().fromJson(param1, paramType)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentBikeModelInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etName.setText(bikeModel.name)
        binding.etPhone.setText(bikeModel.phone)

        binding.btnCancel.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSave.setOnClickListener {
            bikeModel.name = binding.etName.text.toString().trim()
            bikeModel.phone = binding.etPhone.text.toString().trim()

            lifecycleScope.launch {
                val repository = VeloRepository(requireActivity().application)
                if (bikeModel.id.isEmpty()) {
                    repository.insertModel(bikeModel)
                } else {
                    repository.updateModel(bikeModel)
                }
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(model: BikeModel) =
            BikeModelInputFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, Gson().toJson(model))
                }
            }
    }
}