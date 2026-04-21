package com.example.list_temp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.addCallback
import com.example.list_temp.data.BikeModel
import com.example.list_temp.fragments.BikeTypeFragment
import com.example.list_temp.fragments.ManufacturerFragment
import com.example.list_temp.fragments.BikeModelInputFragment
import com.example.list_temp.interfaces.MainActivityCallbacks
import com.example.list_temp.repository.AppRepository

class MainActivity : AppCompatActivity(), MainActivityCallbacks {
    interface Edit {
        fun append()
        fun update()
        fun delete()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        onBackPressedDispatcher.addCallback(this /* lifecycle owner */) {
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
                when (activeFragment) {
                    NamesOfFragment.BIKE_TYPE -> {
                        finish()
                    }
                    NamesOfFragment.MANUFACTURER -> {
                        activeFragment = NamesOfFragment.BIKE_TYPE
                    }
                    NamesOfFragment.BIKE_MODEL_INPUT -> {
                        activeFragment = NamesOfFragment.MANUFACTURER
                    }
                    else -> {}
                }
                updateMenu(activeFragment)
            } else {
                finish()
            }
        }
        showFragment(activeFragment, null)
    }

    var activeFragment: NamesOfFragment = NamesOfFragment.BIKE_TYPE

    private var _miAppendBikeType: MenuItem? = null
    private var _miUpdateBikeType: MenuItem? = null
    private var _miDeleteBikeType: MenuItem? = null
    private var _miAppendManufacturer: MenuItem? = null
    private var _miUpdateManufacturer: MenuItem? = null
    private var _miDeleteManufacturer: MenuItem? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        _miAppendBikeType = menu?.findItem(R.id.miAppendBikeType)
        _miUpdateBikeType = menu?.findItem(R.id.miUpdateBikeType)
        _miDeleteBikeType = menu?.findItem(R.id.miDeleteBikeType)
        _miAppendManufacturer = menu?.findItem(R.id.miAppendManufacturer)
        _miUpdateManufacturer = menu?.findItem(R.id.miUpdateManufacturer)
        _miDeleteManufacturer = menu?.findItem(R.id.miDeleteManufacturer)
        updateMenu(activeFragment)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.miAppendBikeType -> {
                val fedit: Edit = BikeTypeFragment.getInstance()
                fedit.append()
                true
            }
            R.id.miUpdateBikeType -> {
                val fedit: Edit = BikeTypeFragment.getInstance()
                fedit.update()
                true
            }
            R.id.miDeleteBikeType -> {
                val fedit: Edit = BikeTypeFragment.getInstance()
                fedit.delete()
                true
            }
            R.id.miAppendManufacturer -> {
                val fedit: Edit = ManufacturerFragment.getInstance()
                fedit.append()
                true
            }
            R.id.miUpdateManufacturer -> {
                val fedit: Edit = ManufacturerFragment.getInstance()
                fedit.update()
                true
            }
            R.id.miDeleteManufacturer -> {
                val fedit: Edit = ManufacturerFragment.getInstance()
                fedit.delete()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun newTitle(_title: String) {
        title = _title
    }

    private fun updateMenu(fragmentType: NamesOfFragment) {
        _miAppendBikeType?.isVisible = fragmentType == NamesOfFragment.BIKE_TYPE
        _miUpdateBikeType?.isVisible = fragmentType == NamesOfFragment.BIKE_TYPE
        _miDeleteBikeType?.isVisible = fragmentType == NamesOfFragment.BIKE_TYPE
        _miAppendManufacturer?.isVisible = fragmentType == NamesOfFragment.MANUFACTURER
        _miUpdateManufacturer?.isVisible = fragmentType == NamesOfFragment.MANUFACTURER
        _miDeleteManufacturer?.isVisible = fragmentType == NamesOfFragment.MANUFACTURER
    }

    override fun showFragment(fragmentType: NamesOfFragment, bikeModel: BikeModel?) {
        when (fragmentType) {
            NamesOfFragment.BIKE_TYPE -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fcMain, BikeTypeFragment.getInstance())
                    .addToBackStack("bikeType")
                    .commit()
            }
            NamesOfFragment.MANUFACTURER -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fcMain, ManufacturerFragment.getInstance())
                    .addToBackStack(null)
                    .commit()
            }
            NamesOfFragment.BIKE_MODEL_INPUT -> {
                if (bikeModel != null)
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fcMain, BikeModelInputFragment.newInstance(bikeModel))
                        .addToBackStack(null)
                        .commit()
            }
        }
        activeFragment = fragmentType
        updateMenu(fragmentType)
    }

    override fun onDestroy() {
        AppRepository.getInstance().saveData()
        super.onDestroy()
    }
}