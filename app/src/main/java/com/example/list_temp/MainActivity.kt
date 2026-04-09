package com.example.list_temp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.addCallback
import androidx.core.view.GravityCompat
import com.example.list_temp.data.Student
import com.example.list_temp.fragments.FacultyFragment
import com.example.list_temp.fragments.GroupFragment
import com.example.list_temp.fragments.StudentInputFragment
import com.example.list_temp.fragments.StudentsFragment
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
                                                                                                        /*
                    val myFragment = supportFragmentManager.findFragmentByTag(SETTINGS_TAG)
                    if (myFragment != null && myFragment.isVisible) {
                        binding.activityMainToolbar.title=""
                        checkPreference()
                    }
*/
                supportFragmentManager.popBackStack()
                when (activeFragment){
                    NamesOfFragment.FACULTY ->{
                        finish()
                    }
                    NamesOfFragment.GROUP ->{
                        activeFragment=NamesOfFragment.FACULTY
                    }
                    NamesOfFragment.STUDENT ->{
                        activeFragment=NamesOfFragment.GROUP
                    }
                    else -> {}
                }
                updateMenu(activeFragment)
            } else {
                   finish()
                }
        }
        showFragment(activeFragment,null)
    }

    var activeFragment : NamesOfFragment=NamesOfFragment.FACULTY

    private  var  _miAppendFaculty: MenuItem? =null
    private  var  _miUpdateFaculty: MenuItem? =null
    private  var  _miDeleteFaculty: MenuItem? =null
    private  var  _miAppendGroup: MenuItem? =null
    private  var  _miUpdateGroup: MenuItem? =null
    private  var  _miDeleteGroup: MenuItem? =null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        _miAppendFaculty = menu?.findItem(R.id.miAppendFacultet)
        _miUpdateFaculty = menu?.findItem(R.id.miUpdateFacultet)
        _miDeleteFaculty = menu?.findItem(R.id.miDeleteFacultet)
        _miAppendGroup = menu?.findItem(R.id.miAppendGroup)
        _miUpdateGroup = menu?.findItem(R.id.miUpdateGroup)
        _miDeleteGroup = menu?.findItem(R.id.miDeleteGroup)
        updateMenu(activeFragment)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.miAppendFacultet -> {
                val fedit: Edit = FacultyFragment.getInstance()
                fedit.append()
                true
            }
            R.id.miUpdateFacultet -> {
                val fedit: Edit = FacultyFragment.getInstance()
                fedit.update()
                true
            }
            R.id.miDeleteFacultet -> {
                val fedit: Edit = FacultyFragment.getInstance()
                fedit.delete()
                true
            }
            R.id.miAppendGroup -> {
                val fedit: Edit = GroupFragment.getInstance()
                fedit.append()
                true
            }
            R.id.miUpdateGroup -> {
                val fedit: Edit = GroupFragment.getInstance()
                fedit.update()
                true
            }
            R.id.miDeleteGroup -> {
                val fedit: Edit = GroupFragment.getInstance()
                fedit.delete()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

                                                                                                        /*
    override fun onResume() {
        super.onResume()
        showFaculty()
    }
*/

    fun showFaculty() {
        showFragment(NamesOfFragment.FACULTY)
    }

    override fun newTitle(_title: String) {
        title=_title
    }

    private fun updateMenu(fragmentType: NamesOfFragment){
        _miAppendFaculty?.isVisible = fragmentType==NamesOfFragment.FACULTY
        _miUpdateFaculty?.isVisible = fragmentType==NamesOfFragment.FACULTY
        _miDeleteFaculty?.isVisible = fragmentType==NamesOfFragment.FACULTY
        _miAppendGroup?.isVisible = fragmentType==NamesOfFragment.GROUP
        _miUpdateGroup?.isVisible = fragmentType==NamesOfFragment.GROUP
        _miDeleteGroup?.isVisible = fragmentType==NamesOfFragment.GROUP
    }

    override fun showFragment(fragmentType: NamesOfFragment, student: Student?) {
        when (fragmentType){
            NamesOfFragment.FACULTY ->{
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fcMain, FacultyFragment.getInstance())
                    .addToBackStack("faculty")
                    .commit()
            }
            NamesOfFragment.GROUP ->{
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fcMain, GroupFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
            }
            NamesOfFragment.STUDENT -> {
                if (student != null)
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fcMain, StudentInputFragment.newInstance(student))
                        .addToBackStack(null)
                        .commit()
                }
        }
        activeFragment=fragmentType
        updateMenu(fragmentType)
    }

    override fun onDestroy() {
        AppRepository.getInstance().saveData()
        super.onDestroy()
    }
}