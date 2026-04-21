package com.example.list_temp.fragments

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.list_temp.MainActivity
import com.example.list_temp.NamesOfFragment
import com.example.list_temp.R
import com.example.list_temp.databinding.FragmentBikeTypeBinding
import com.example.list_temp.data.BikeType
import com.example.list_temp.interfaces.MainActivityCallbacks

class BikeTypeFragment : Fragment(), MainActivity.Edit {

    companion object {
        private var INSTANCE: BikeTypeFragment? = null
        fun getInstance(): BikeTypeFragment {
            if (INSTANCE == null) INSTANCE = BikeTypeFragment()
            return INSTANCE ?: throw Exception("BikeTypeFragment не создан")
        }
    }

    private lateinit var viewModel: BikeTypeViewModel
    private lateinit var _binding: FragmentBikeTypeBinding
    val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val ma = (requireActivity() as MainActivityCallbacks)
        ma.newTitle("ТИПЫ ВЕЛОСИПЕДОВ")
        _binding = FragmentBikeTypeBinding.inflate(inflater, container, false)
        binding.rvBikeType.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(BikeTypeViewModel::class.java)
        viewModel.bikeTypeList.observe(viewLifecycleOwner) {
            if (it != null)
                binding.rvBikeType.adapter = BikeTypeAdapter(it.items)
        }
    }

    override fun append() {
        editBikeType()
    }

    override fun update() {
        editBikeType(viewModel.bikeType?.name ?: "")
    }

    override fun delete() {
        deleteDialog()
    }

    private fun deleteDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Удаление!")
            .setMessage("Вы действительно хотите удалить тип велосипеда " +
                    "${viewModel.bikeType?.name ?: ""}?")
            .setPositiveButton("ДА") { _, _ ->
                viewModel.deleteBikeType()
            }
            .setNegativeButton("НЕТ", null)
            .setCancelable(true)
            .create()
            .show()
    }

    private fun editBikeType(typeName: String = "") {
        val mDialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_string, null)
        val messageText = mDialogView.findViewById<TextView>(R.id.tvInfo)
        val inputString = mDialogView.findViewById<EditText>(R.id.etString)
        inputString.setText(typeName)
        messageText.text = "Укажите название типа велосипеда"

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("ИЗМЕНЕНИЕ ДАННЫХ")
            .setView(mDialogView)
            .setPositiveButton("подтверждаю") { _, _ ->
                if (inputString.text.isNotBlank()) {
                    if (typeName.isBlank())
                        viewModel.appendBikeType(inputString.text.toString())
                    else
                        viewModel.updateBikeType(inputString.text.toString())
                }
            }
            .setNegativeButton("отмена", null)
            .setCancelable(true)
            .create()
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
            holder.bind(viewModel.bikeTypeList.value!!.items[position])
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
                if (bikeType == viewModel.bikeType)
                    updateCurrentView(itemView)
                val tv = itemView.findViewById<TextView>(R.id.tvBikeType)
                tv.text = bikeType.name
                tv.setOnClickListener {
                    viewModel.setCurrentBikeType(bikeType)
                    updateCurrentView(itemView)
                }
                tv.setOnLongClickListener {
                    tv.callOnClick()
                    mainActivity?.showFragment(NamesOfFragment.MANUFACTURER)
                    true
                }
            }
        }
    }

    var mainActivity: MainActivityCallbacks? = null
    override fun onAttach(context: Context) {
        mainActivity = (context as MainActivityCallbacks)
        mainActivity?.newTitle("Типы велосипедов")
        super.onAttach(context)
    }
}