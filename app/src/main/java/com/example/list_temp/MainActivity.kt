package com.example.list_temp

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.list_temp.data.BikeModel
import com.example.list_temp.fragments.BikeModelInputFragment
import com.example.list_temp.fragments.BikeTypeFragment
import com.example.list_temp.fragments.ManufacturerFragment
import com.example.list_temp.interfaces.MainActivityCallbacks
import com.example.list_temp.sync.SyncResult
import com.example.list_temp.sync.VeloSyncManager
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), MainActivityCallbacks {

    interface Edit {
        fun append()
        fun update()
        fun delete()
    }

    private lateinit var syncManager: VeloSyncManager
    private var currentBikeTypeId: String? = null      // ID выбранного типа велосипеда
    private var currentManufacturerId: String? = null  // ID выбранного производителя

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        syncManager = VeloSyncManager(this)

        onBackPressedDispatcher.addCallback(this) {
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
                when (activeFragment) {
                    NamesOfFragment.BIKE_TYPE -> finish()
                    NamesOfFragment.MANUFACTURER -> activeFragment = NamesOfFragment.BIKE_TYPE
                    NamesOfFragment.BIKE_MODEL_INPUT -> activeFragment = NamesOfFragment.MANUFACTURER
                    else -> {}
                }
                updateMenu(activeFragment)
            } else {
                finish()
            }
        }

        if (savedInstanceState == null) {
            showFragment(NamesOfFragment.BIKE_TYPE, null)
        }
    }

    var activeFragment: NamesOfFragment = NamesOfFragment.BIKE_TYPE

    private var _miAppendBikeType: MenuItem? = null
    private var _miUpdateBikeType: MenuItem? = null
    private var _miDeleteBikeType: MenuItem? = null
    private var _miAppendManufacturer: MenuItem? = null
    private var _miUpdateManufacturer: MenuItem? = null
    private var _miDeleteManufacturer: MenuItem? = null
    private var _miSyncPush: MenuItem? = null
    private var _miSyncPull: MenuItem? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        _miAppendBikeType = menu?.findItem(R.id.miAppendBikeType)
        _miUpdateBikeType = menu?.findItem(R.id.miUpdateBikeType)
        _miDeleteBikeType = menu?.findItem(R.id.miDeleteBikeType)
        _miAppendManufacturer = menu?.findItem(R.id.miAppendManufacturer)
        _miUpdateManufacturer = menu?.findItem(R.id.miUpdateManufacturer)
        _miDeleteManufacturer = menu?.findItem(R.id.miDeleteManufacturer)
        _miSyncPush = menu?.findItem(R.id.miSyncPush)
        _miSyncPull = menu?.findItem(R.id.miSyncPull)
        updateMenu(activeFragment)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.miAppendBikeType -> { getCurrentEditFragment()?.append(); true }
            R.id.miUpdateBikeType -> { getCurrentEditFragment()?.update(); true }
            R.id.miDeleteBikeType -> { getCurrentEditFragment()?.delete(); true }
            R.id.miAppendManufacturer -> { getCurrentEditFragment()?.append(); true }
            R.id.miUpdateManufacturer -> { getCurrentEditFragment()?.update(); true }
            R.id.miDeleteManufacturer -> { getCurrentEditFragment()?.delete(); true }
            R.id.miSyncPush -> {
                lifecycleScope.launch {
                    val result = syncManager.pushAllToServer()
                    showSyncResult(result)
                }
                true
            }
            R.id.miSyncPull -> {
                lifecycleScope.launch {
                    val result = syncManager.pullAllFromServer()
                    showSyncResult(result)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getCurrentEditFragment(): Edit? {
        return supportFragmentManager.findFragmentById(R.id.fcMain) as? Edit
    }

    private fun showSyncResult(result: SyncResult) {
        val message = when (result) {
            is SyncResult.Success -> result.message
            is SyncResult.Error -> result.message
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun newTitle(_title: String) {
        title = _title
    }

    private fun updateMenu(fragmentType: NamesOfFragment) {
        val isBikeType = fragmentType == NamesOfFragment.BIKE_TYPE
        val isManufacturer = fragmentType == NamesOfFragment.MANUFACTURER

        _miAppendBikeType?.isVisible = isBikeType
        _miUpdateBikeType?.isVisible = isBikeType
        _miDeleteBikeType?.isVisible = isBikeType
        _miAppendManufacturer?.isVisible = isManufacturer
        _miUpdateManufacturer?.isVisible = isManufacturer
        _miDeleteManufacturer?.isVisible = isManufacturer
        _miSyncPush?.isVisible = true
        _miSyncPull?.isVisible = true
    }

    override fun showFragment(fragmentType: NamesOfFragment, bikeModel: BikeModel?) {
        val fragment = when (fragmentType) {
            NamesOfFragment.BIKE_TYPE -> BikeTypeFragment.getInstance()
            NamesOfFragment.MANUFACTURER -> {
                ManufacturerFragment.newInstance(currentBikeTypeId ?: "")
            }
            NamesOfFragment.BIKE_MODEL_INPUT -> {
                val manId = bikeModel?.manufacturerId ?: currentManufacturerId ?: ""
                Log.d("MainActivity", "showFragment BIKE_MODEL_INPUT: manId=$manId")
                BikeModelInputFragment.newInstance(bikeModel, manId)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fcMain, fragment)
            .addToBackStack(null)
            .commit()

        activeFragment = fragmentType
        updateMenu(fragmentType)
    }

    fun setCurrentBikeTypeId(id: String) {
        currentBikeTypeId = id
    }

    fun setCurrentManufacturerId(id: String) {
        android.util.Log.d("MainActivity", "setCurrentManufacturerId: $id")
        currentManufacturerId = id
    }
}