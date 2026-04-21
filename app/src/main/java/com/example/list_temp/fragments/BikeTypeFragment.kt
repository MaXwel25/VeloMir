package com.example.list_temp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.list_temp.MainActivity
import com.example.list_temp.NamesOfFragment
import com.example.list_temp.R
import com.example.list_temp.data.BikeType
import com.example.list_temp.databinding.FragmentBikeTypeBinding
import com.example.list_temp.interfaces.MainActivityCallbacks

class BikeTypeFragment : Fragment(), MainActivity.Edit {

    companion object {
        fun getInstance(): BikeTypeFragment = BikeTypeFragment()
    }

    private lateinit var viewModel: BikeTypeViewModel
    private lateinit var binding: FragmentBikeTypeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBikeTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // инициализация ViewModel с Application
        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        viewModel = ViewModelProvider(this, factory).get(BikeTypeViewModel::class.java)

        binding.rvBikeType.layoutManager = LinearLayoutManager(context)

        viewModel.allBikeTypes.observe(viewLifecycleOwner) { types ->
            binding.rvBikeType.adapter = BikeTypeAdapter(types)
        }

        (requireActivity() as MainActivityCallbacks).newTitle("ТИПЫ ВЕЛОСИПЕДОВ")
    }

    override fun append() {
        editBikeType()
    }

    override fun update() {
        viewModel.currentBikeType.value?.let { current ->
            editBikeType(current.name)
        }
    }

    override fun delete() {
        viewModel.currentBikeType.value?.let { current ->
            AlertDialog.Builder(requireContext())
                .setTitle("Удаление!")
                .setMessage("Вы действительно хотите удалить тип велосипеда \"${current.name}\"?")
                .setPositiveButton("ДА") { _, _ ->
                    viewModel.deleteBikeType(current)
                }
                .setNegativeButton("НЕТ", null)
                .show()
        }
    }

    private fun editBikeType(initialName: String = "") {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_string, null)
        val tvInfo = dialogView.findViewById<TextView>(R.id.tvInfo)
        val etString = dialogView.findViewById<EditText>(R.id.etString)
        tvInfo.text = "Укажите название типа велосипеда"
        etString.setText(initialName)

        AlertDialog.Builder(requireContext())
            .setTitle(if (initialName.isBlank()) "ДОБАВЛЕНИЕ" else "ИЗМЕНЕНИЕ")
            .setView(dialogView)
            .setPositiveButton("Подтвердить") { _, _ ->
                val newName = etString.text.toString().trim()
                if (newName.isNotBlank()) {
                    if (initialName.isBlank()) {
                        viewModel.addBikeType(newName)
                    } else {
                        viewModel.currentBikeType.value?.let { type ->
                            viewModel.updateBikeType(type, newName)
                        }
                    }
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private inner class BikeTypeAdapter(private val items: List<BikeType>) :
        RecyclerView.Adapter<BikeTypeAdapter.ItemHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            val view = layoutInflater.inflate(R.layout.element_bike_type_list, parent, false)
            return ItemHolder(view)
        }

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            holder.bind(items[position])
        }

        private var lastView: View? = null

        private fun updateCurrentView(view: View) {
            lastView?.findViewById<ConstraintLayout>(R.id.clBikeType)?.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.white)
            )
            view.findViewById<ConstraintLayout>(R.id.clBikeType).setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.pink)
            )
            lastView = view
        }

        private inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
            private lateinit var bikeType: BikeType

            fun bind(bikeType: BikeType) {
                this.bikeType = bikeType
                val tv = itemView.findViewById<TextView>(R.id.tvBikeType)
                tv.text = bikeType.name

                // Выделение текущего элемента
                if (viewModel.currentBikeType.value?.id == bikeType.id) {
                    updateCurrentView(itemView)
                } else {
                    itemView.findViewById<ConstraintLayout>(R.id.clBikeType)
                        .setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                }

                tv.setOnClickListener {
                    viewModel.setCurrentBikeType(bikeType)
                    updateCurrentView(itemView)
                }
                tv.setOnLongClickListener {
                    it.callOnClick()
                    (requireActivity() as MainActivity).setCurrentBikeTypeId(bikeType.id)
                    (requireActivity() as MainActivityCallbacks).showFragment(NamesOfFragment.MANUFACTURER)
                    true
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivityCallbacks).newTitle("Типы велосипедов")
    }


}