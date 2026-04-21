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
        private lateinit var manufacturer: Manufacturer
        fun newInstance(manufacturer: Manufacturer): BikeModelFragment {
            this.manufacturer = manufacturer
            return BikeModelFragment()
        }
    }

    private lateinit var viewModel: BikeModelViewModel
    private lateinit var _binding: FragmentBikeModelBinding
    val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBikeModelBinding.inflate(inflater, container, false)
        binding.rvBikeModel.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(BikeModelViewModel::class.java)
        viewModel.initManufacturer(manufacturer)
        viewModel.bikeModelList.observe(viewLifecycleOwner) {
            binding.rvBikeModel.adapter = BikeModelAdapter(it)
        }
        binding.fabAppendBikeModel.setOnClickListener {
            editBikeModel(BikeModel().apply { manufacturerID = viewModel.manufacturer.id })
        }
    }

    private fun deleteDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Удаление!")
            .setMessage("Вы действительно хотите удалить модель ${viewModel.bikeModel?.name ?: ""}?")
            .setPositiveButton("ДА") { _, _ ->
                viewModel.deleteBikeModel()
            }
            .setNegativeButton("НЕТ", null)
            .setCancelable(true)
            .create()
            .show()
    }

    private fun editBikeModel(model: BikeModel) {
        (requireActivity() as MainActivityCallbacks).showFragment(NamesOfFragment.BIKE_MODEL_INPUT, model)
        (requireActivity() as MainActivityCallbacks).newTitle("Производитель ${viewModel.manufacturer.name}")
    }

    private inner class BikeModelAdapter(private val items: List<BikeModel>) :
        RecyclerView.Adapter<BikeModelAdapter.ItemHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            val view = layoutInflater.inflate(R.layout.element_bike_model_list, parent, false)
            return ItemHolder(view)
        }

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            holder.bind(viewModel.bikeModelList.value!![position])
        }

        private var lastView: View? = null
        private fun updateCurrentView(view: View) {
            val ll = lastView?.findViewById<LinearLayout>(R.id.llBikeModelButtons)
            ll?.visibility = View.INVISIBLE
            ll?.layoutParams = ll?.layoutParams.apply { this?.width = 1 }
            val ib = lastView?.findViewById<ImageButton>(R.id.ibCall)
            ib?.visibility = View.INVISIBLE
            ib?.layoutParams = ib?.layoutParams.apply { this?.width = 1 }

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

            fun bind(bikeModel: BikeModel) {
                this.bikeModel = bikeModel
                if (bikeModel == viewModel.bikeModel)
                    updateCurrentView(itemView)
                val tv = itemView.findViewById<TextView>(R.id.tvBikeModelName)
                tv.text = bikeModel.name
                val cl = itemView.findViewById<ConstraintLayout>(R.id.clBikeModel)
                cl.setOnClickListener {
                    viewModel.setCurrentBikeModel(bikeModel)
                    updateCurrentView(itemView)
                }
                itemView.findViewById<ImageButton>(R.id.ibEditBikeModel).setOnClickListener {
                    editBikeModel(bikeModel)
                }
                itemView.findViewById<ImageButton>(R.id.ibDeleteBikeModel).setOnClickListener {
                    deleteDialog()
                }

                val llb = itemView.findViewById<LinearLayout>(R.id.llBikeModelButtons)
                llb.visibility = View.INVISIBLE
                llb?.layoutParams = llb?.layoutParams.apply { this?.width = 1 }
                val ib = itemView.findViewById<ImageButton>(R.id.ibCall)
                ib.visibility = View.INVISIBLE
                cl.setOnLongClickListener {
                    cl.callOnClick()
                    llb.visibility = View.VISIBLE
                    if (bikeModel.phone.isNotBlank())
                        ib.visibility = View.VISIBLE
                    MainScope().launch {
                        val lp = llb?.layoutParams
                        lp?.width = 1
                        val ip = ib.layoutParams
                        ip.width = 1
                        while (lp?.width!! < 350) {
                            lp?.width = lp?.width!! + 35
                            llb?.layoutParams = lp
                            ip.width = ip.width + 10
                            if (ib.visibility == View.VISIBLE)
                                ib.layoutParams = ip
                            delay(50)
                        }
                    }
                    true
                }
                itemView.findViewById<ImageButton>(R.id.ibCall).setOnClickListener {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.CALL_PHONE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:${bikeModel.phone}"))
                        startActivity(intent)
                    } else {
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.CALL_PHONE), 2
                        )
                    }
                }
            }
        }
    }
}