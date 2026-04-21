package com.example.list_temp.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.list_temp.NamesOfFragment
import com.example.list_temp.R
import com.example.list_temp.data.BikeModel
import com.example.list_temp.data.Manufacturer
import com.example.list_temp.databinding.FragmentBikeModelBinding
import com.example.list_temp.interfaces.MainActivityCallbacks
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BikeModelFragment : Fragment() {

    companion object {
        private const val ARG_MANUFACTURER = "manufacturer"
        fun newInstance(manufacturer: Manufacturer): BikeModelFragment {
            return BikeModelFragment().apply {
                arguments = Bundle().apply {
                    putString("man_id", manufacturer.id)
                    putString("man_name", manufacturer.name)
                }
            }
        }
    }

    private lateinit var viewModel: BikeModelViewModel
    private lateinit var binding: FragmentBikeModelBinding
    private lateinit var manufacturer: Manufacturer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = arguments?.getString("man_id") ?: ""
        val name = arguments?.getString("man_name") ?: ""
        manufacturer = Manufacturer(id = id, name = name, bikeTypeId = "")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentBikeModelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        viewModel = ViewModelProvider(this, factory).get(BikeModelViewModel::class.java)
        viewModel.setCurrentManufacturer(manufacturer)

        binding.rvBikeModel.layoutManager = LinearLayoutManager(context)

        viewModel.models.observe(viewLifecycleOwner) { models ->
            binding.rvBikeModel.adapter = BikeModelAdapter(models)
        }

        binding.fabAppendBikeModel.setOnClickListener {
            editBikeModel(null)
        }

        (requireActivity() as MainActivityCallbacks).newTitle("Модели ${manufacturer.name}")
    }

    private fun deleteDialog(model: BikeModel) {
        AlertDialog.Builder(requireContext())
            .setTitle("Удаление")
            .setMessage("Удалить модель \"${model.name}\"?")
            .setPositiveButton("Да") { _, _ -> viewModel.deleteBikeModel(model) }
            .setNegativeButton("Нет", null)
            .show()
    }

    private fun editBikeModel(model: BikeModel?) {
        // используем BikeModelInputFragment для ввода
        val fragment = BikeModelInputFragment.newInstance(model ?: BikeModel())
        (requireActivity() as MainActivityCallbacks).showFragment(NamesOfFragment.BIKE_MODEL_INPUT, model)
    }

    private inner class BikeModelAdapter(private val items: List<BikeModel>) :
        RecyclerView.Adapter<BikeModelAdapter.ItemHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            val view = layoutInflater.inflate(R.layout.element_bike_model_list, parent, false)
            return ItemHolder(view)
        }

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            holder.bind(items[position])
        }

        private var lastView: View? = null

        private fun updateCurrentView(view: View) {
            lastView?.findViewById<ConstraintLayout>(R.id.clBikeModel)?.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.white)
            )
            view.findViewById<ConstraintLayout>(R.id.clBikeModel).setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.pink)
            )
            lastView = view
        }

        private inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
            private lateinit var bikeModel: BikeModel

            fun bind(model: BikeModel) {
                this.bikeModel = model
                val tv = itemView.findViewById<TextView>(R.id.tvBikeModelName)
                tv.text = model.name

                if (viewModel.currentBikeModel.value?.id == model.id) {
                    updateCurrentView(itemView)
                } else {
                    itemView.findViewById<ConstraintLayout>(R.id.clBikeModel)
                        .setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                }

                itemView.setOnClickListener {
                    viewModel.setCurrentBikeModel(model)
                    updateCurrentView(itemView)
                }

                itemView.findViewById<ImageButton>(R.id.ibEditBikeModel).setOnClickListener {
                    editBikeModel(model)
                }
                itemView.findViewById<ImageButton>(R.id.ibDeleteBikeModel).setOnClickListener {
                    deleteDialog(model)
                }

                val llButtons = itemView.findViewById<LinearLayout>(R.id.llBikeModelButtons)
                val ibCall = itemView.findViewById<ImageButton>(R.id.ibCall)
                llButtons.visibility = View.INVISIBLE
                ibCall.visibility = View.INVISIBLE

                itemView.setOnLongClickListener {
                    it.callOnClick()
                    llButtons.visibility = View.VISIBLE
                    if (model.phone.isNotBlank()) ibCall.visibility = View.VISIBLE
                    // анимация
                    MainScope().launch {
                        val lp = llButtons.layoutParams
                        lp.width = 1
                        var w = 1
                        while (w < 350) {
                            w += 35
                            lp.width = w
                            llButtons.layoutParams = lp
                            delay(50)
                        }
                    }
                    true
                }

                ibCall.setOnClickListener {
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE)
                        == PackageManager.PERMISSION_GRANTED) {
                        startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:${model.phone}")))
                    } else {
                        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), 2)
                    }
                }
            }
        }
    }
}