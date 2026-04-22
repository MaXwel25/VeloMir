package com.example.list_temp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.list_temp.data.BikeModel
import com.example.list_temp.databinding.FragmentBikeModelInputBinding
import com.example.list_temp.repository.VeloRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class BikeModelInputFragment : Fragment() {
    private lateinit var binding: FragmentBikeModelInputBinding
    private var bikeModel: BikeModel? = null
    private var manufacturerId: String = ""
    private var isNewModel: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            manufacturerId = args.getString(ARG_MANUFACTURER_ID) ?: ""
            isNewModel = args.getBoolean(ARG_IS_NEW, true)
            if (!isNewModel) {
                val modelJson = args.getString(ARG_MODEL_JSON)
                if (!modelJson.isNullOrEmpty()) {
                    val type = object : TypeToken<BikeModel>() {}.type
                    bikeModel = Gson().fromJson(modelJson, type)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentBikeModelInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etName.setText(bikeModel?.name ?: "")
        binding.etPhone.setText(bikeModel?.phone ?: "")

        binding.btnCancel.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSave.setOnClickListener {
            val newName = binding.etName.text.toString().trim()
            val newPhone = binding.etPhone.text.toString().trim()
            android.util.Log.d("BikeModelInput", "Сохранение: name=$newName, phone=$newPhone, isNew=$isNewModel, manId=$manufacturerId")

            if (newName.isEmpty()) {
                Toast.makeText(requireContext(), "Введите название модели", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val repository = VeloRepository(requireActivity().application)
                    if (isNewModel) {
                        val model = BikeModel(name = newName, phone = newPhone, manufacturerId = manufacturerId)
                        repository.insertModel(model)
                        android.util.Log.d("BikeModelInput", "Модель вставлена: $model")
                        Toast.makeText(requireContext(), "Модель добавлена", Toast.LENGTH_SHORT).show()
                    } else {
                        bikeModel?.apply {
                            name = newName
                            phone = newPhone
                        }
                        bikeModel?.let { updatedModel ->
                            repository.updateModel(updatedModel)
                            android.util.Log.d("BikeModelInput", "Модель обновлена: $updatedModel")
                            Toast.makeText(requireContext(), "Модель обновлена", Toast.LENGTH_SHORT).show()
                        }
                    }
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                } catch (e: Exception) {
                    android.util.Log.e("BikeModelInput", "Ошибка сохранения", e)
                    Toast.makeText(requireContext(), "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    companion object {
        private const val ARG_MODEL_JSON = "model_json"
        private const val ARG_MANUFACTURER_ID = "manufacturer_id"
        private const val ARG_IS_NEW = "is_new"

        @JvmStatic
        fun newInstance(model: BikeModel?, manufacturerId: String): BikeModelInputFragment {
            return BikeModelInputFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_MANUFACTURER_ID, manufacturerId)
                    if (model == null) {
                        putBoolean(ARG_IS_NEW, true)
                    } else {
                        putBoolean(ARG_IS_NEW, false)
                        putString(ARG_MODEL_JSON, Gson().toJson(model))
                    }
                }
            }
        }
    }
}