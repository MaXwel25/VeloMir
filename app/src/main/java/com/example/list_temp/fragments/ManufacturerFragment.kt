package com.example.list_temp.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.list_temp.MainActivity
import com.example.list_temp.R
import com.example.list_temp.databinding.FragmentManufacturerBinding
import com.example.list_temp.data.Manufacturer
import com.example.list_temp.interfaces.MainActivityCallbacks
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ManufacturerFragment : Fragment(), MainActivity.Edit {

    companion object {
        private var INSTANCE : ManufacturerFragment? = null
        fun getInstance(): ManufacturerFragment{
            if (INSTANCE == null) INSTANCE = ManufacturerFragment()
            return INSTANCE ?: throw Exception("ManufacturerFragment не создан")
        }
        fun newInstance() : ManufacturerFragment{
            INSTANCE = ManufacturerFragment()
            return INSTANCE!!
        }
    }

    private lateinit var viewModel: ManufacturerViewModel
    private var tabPosition : Int=0
    private lateinit  var _binding: FragmentManufacturerBinding
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentManufacturerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(ManufacturerViewModel::class.java)
        val ma = (requireActivity() as MainActivityCallbacks)
        ma.newTitle("ТИП ВЕЛОСИПЕДА \"${viewModel.bikeType?.name}\"")

        viewModel.manufacturerList.observe(viewLifecycleOwner) {
            createUI(it)
        }
    }

    private inner class ManufacturerPageAdapter(fa: FragmentActivity,
                                                private val manufacturers: List<Manufacturer>?): FragmentStateAdapter(fa) {
        override fun getItemCount(): Int {
            return (manufacturers?.size ?: 0)
        }

        override fun createFragment(position: Int): Fragment {
            return BikeModelFragment.newInstance(manufacturers!![position])
        }
    }
    private fun createUI(manufacturerList : List<Manufacturer>){
        binding.tlManufacturer.clearOnTabSelectedListeners()
        binding.tlManufacturer.removeAllTabs()

        for (i in 0 until (manufacturerList.size)){
            binding.tlManufacturer.addTab(binding.tlManufacturer.newTab().apply{
                text= manufacturerList.get(i).name
            })
        }
        val adapter= ManufacturerPageAdapter(requireActivity(), viewModel.manufacturerList.value)
        binding.vpManufacturer.adapter=adapter
        TabLayoutMediator(binding.tlManufacturer,binding.vpManufacturer,true,true){
                tab,pos ->
            tab.text= manufacturerList.get(pos).name
        }.attach()

        tabPosition=0
        if (viewModel.manufacturer != null)
            tabPosition= if(viewModel.getManufacturerListPosition>=0)
                viewModel.getManufacturerListPosition
            else
                0
        viewModel.setCurrentManufacturer(tabPosition)
        binding.tlManufacturer.selectTab(binding.tlManufacturer.getTabAt(tabPosition),true)

        binding.tlManufacturer.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tabPosition= tab?.position!!
                viewModel.setCurrentManufacturer(manufacturerList[tabPosition])
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun append() {
        editManufacturer()
    }

    override fun update() {
        editManufacturer(viewModel.manufacturer?.name ?: "" )
    }

    override fun delete() {
        deleteDialog()
    }

    private fun deleteDialog(){
        if (viewModel.manufacturer==null) return
        AlertDialog.Builder(requireContext())
            .setTitle("Удаление!") // заголовок
            .setMessage("Вы действительно хотите удалить производителя ${viewModel.manufacturer?.name ?: ""}?") // сообщение
            .setPositiveButton("ДА") { _, _ ->
                viewModel.deleteManufacturer()
            }
            .setNegativeButton("НЕТ", null)
            .setCancelable(true)
            .create()
            .show()
    }

    private fun editManufacturer(manufacturerName : String=""){
        val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_string, null)
        val messageText= mDialogView.findViewById<TextView>(R.id.tvInfo)
        val inputString= mDialogView.findViewById<EditText>(R.id.etString)
        inputString.setText(manufacturerName)
        messageText.text="Укажите название производителя"

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("ИЗМЕНЕНИЕ ДАННЫХ") // заголовок
            .setView(mDialogView)
            .setPositiveButton("подтверждаю") { _, _ ->
                if (inputString.text.isNotBlank()) {
                    if (manufacturerName.isBlank())
                        viewModel.appendManufacturer(inputString.text.toString())
                    else
                        viewModel.updateManufacturer(inputString.text.toString())
                }
            }
            .setNegativeButton("отмена",null)
            .setCancelable(true)
            .create()
            .show()
    }
}