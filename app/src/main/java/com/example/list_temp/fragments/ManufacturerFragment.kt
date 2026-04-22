package com.example.list_temp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.list_temp.MainActivity
import com.example.list_temp.R
import com.example.list_temp.data.Manufacturer
import com.example.list_temp.databinding.FragmentManufacturerBinding
import com.example.list_temp.interfaces.MainActivityCallbacks
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ManufacturerFragment : Fragment(), MainActivity.Edit {

    companion object {
        private const val ARG_BIKE_TYPE_ID = "bike_type_id"
        fun newInstance(bikeTypeId: String): ManufacturerFragment {
            return ManufacturerFragment().apply {
                arguments = Bundle().apply { putString(ARG_BIKE_TYPE_ID, bikeTypeId) }
            }
        }
    }

    private lateinit var viewModel: ManufacturerViewModel
    private lateinit var binding: FragmentManufacturerBinding
    private val bikeTypeId: String by lazy { arguments?.getString(ARG_BIKE_TYPE_ID) ?: "" }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentManufacturerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        viewModel = ViewModelProvider(this, factory).get(ManufacturerViewModel::class.java)
        viewModel.setCurrentBikeTypeId(bikeTypeId)

        viewModel.manufacturers.observe(viewLifecycleOwner) { manufacturers ->
            setupViewPager(manufacturers)
        }

        (requireActivity() as MainActivityCallbacks).newTitle("Производители")
    }

    private fun setupViewPager(manufacturers: List<Manufacturer>) {
        // 1. Очищаем слушатель и вкладки
        binding.tlManufacturer.clearOnTabSelectedListeners()
        binding.tlManufacturer.removeAllTabs()

        if (manufacturers.isEmpty()) return

        // 2. Добавляем вкладки
        manufacturers.forEach { man ->
            binding.tlManufacturer.addTab(binding.tlManufacturer.newTab().setText(man.name))
        }

        // 3. Настраиваем адаптер и связываем TabLayout с ViewPager
        val adapter = ManufacturerPageAdapter(requireActivity(), manufacturers)
        binding.vpManufacturer.adapter = adapter

        TabLayoutMediator(binding.tlManufacturer, binding.vpManufacturer) { tab, pos ->
            tab.text = manufacturers[pos].name
        }.attach()

        // 4. Выбираем нужную вкладку (без слушателя)
        val selectedIndex = viewModel.currentManufacturer.value?.let { current ->
            manufacturers.indexOfFirst { it.id == current.id }.takeIf { it >= 0 }
        } ?: 0

        // Выбираем вкладку, не вызывая onTabSelected
        binding.tlManufacturer.selectTab(binding.tlManufacturer.getTabAt(selectedIndex), true)

        // 5. Добавляем слушатель только после выбора
        binding.tlManufacturer.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val position = tab.position
                if (position < manufacturers.size) {
                    viewModel.setCurrentManufacturer(manufacturers[position])
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private inner class ManufacturerPageAdapter(
        fa: FragmentActivity,
        private val manufacturers: List<Manufacturer>
    ) : FragmentStateAdapter(fa) {

        override fun getItemCount(): Int = manufacturers.size

        override fun createFragment(position: Int): Fragment {
            return BikeModelFragment.newInstance(manufacturers[position])
        }
    }

    override fun append() {
        editManufacturer()
    }

    override fun update() {
        viewModel.currentManufacturer.value?.let {
            editManufacturer(it.name)
        }
    }

    override fun delete() {
        viewModel.currentManufacturer.value?.let { man ->
            AlertDialog.Builder(requireContext())
                .setTitle("Удаление")
                .setMessage("Удалить производителя \"${man.name}\"?")
                .setPositiveButton("Да") { _, _ -> viewModel.deleteManufacturer(man) }
                .setNegativeButton("Нет", null)
                .show()
        }
    }

    private fun editManufacturer(initialName: String = "") {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_string, null)
        val tvInfo = dialogView.findViewById<TextView>(R.id.tvInfo)
        val etString = dialogView.findViewById<EditText>(R.id.etString)
        tvInfo.text = "Название производителя"
        etString.setText(initialName)

        AlertDialog.Builder(requireContext())
            .setTitle(if (initialName.isBlank()) "Добавить" else "Изменить")
            .setView(dialogView)
            .setPositiveButton("ОК") { _, _ ->
                val newName = etString.text.toString().trim()
                if (newName.isNotEmpty()) {
                    if (initialName.isBlank()) {
                        viewModel.addManufacturer(newName)
                    } else {
                        viewModel.currentManufacturer.value?.let { man ->
                            viewModel.updateManufacturer(man, newName)
                        }
                    }
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}