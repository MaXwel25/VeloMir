package com.example.list_temp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.list_temp.R
import com.example.list_temp.data.BikeModel
import com.example.list_temp.databinding.FragmentBikeModelInputBinding
import com.example.list_temp.repository.AppRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private const val ARG_PARAM1 = "bike_model_param"

class BikeModelInputFragment : Fragment() {
    private lateinit var bikeModel: BikeModel
    private lateinit var  _binding : FragmentBikeModelInputBinding

    val binding
        get()=_binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val param1 = it.getString(ARG_PARAM1)
            if (param1==null)
                bikeModel= BikeModel()
            else {
                val paramType = object : TypeToken<BikeModel>() {}.type
                bikeModel = Gson().fromJson(param1, paramType)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentBikeModelInputBinding.inflate(inflater,container,false)

        binding.etName.setText(bikeModel.name)
        binding.etPhone.setText(bikeModel.phone)

        binding.btnCancel.setOnClickListener{
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.btnSave.setOnClickListener {
            bikeModel.name = binding.etName.text.toString()
            bikeModel.phone = binding.etPhone.text.toString()
            AppRepository.getInstance().updateBikeModel(bikeModel)
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        return binding.root
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