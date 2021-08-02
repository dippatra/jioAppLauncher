package com.jioapplauncher

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appinfomodule.AppInfo
import com.appinfomodule.ApplicationInfoProvider
import com.google.android.material.textfield.TextInputLayout
import com.jioapplauncher.adapters.AppListAdapter
import fr.castorflex.android.circularprogressbar.CircularProgressBar

class MainActivity : AppCompatActivity() {
    private val TAG="MainActivity"
    private lateinit var appLauncherViewModel: AppLauncherViewModel
    private lateinit var recycler: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var appInfoAdapter: AppListAdapter
    private lateinit var progressBar: CircularProgressBar
    private lateinit var editTextInputLayout: TextInputLayout
    private lateinit var search:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeSupportBar()
        appLauncherViewModel= ViewModelProvider(this).get(AppLauncherViewModel::class.java)
        appLauncherViewModel.getAllAppInfo()
        initializeActivityControl()

    }

    private fun initializeActivityControl() {
        try{
            progressBar=findViewById(R.id.loader)
            layoutManager = LinearLayoutManager(applicationContext)
            recycler=findViewById(R.id.recycler)
            recycler.layoutManager = layoutManager
            recycler.itemAnimator = DefaultItemAnimator()
            recycler.addItemDecoration(DividerItemDecoration(recycler.context, DividerItemDecoration.VERTICAL))
            appLauncherViewModel.appInfoList.observe(this, {
                try {
                    hideLoader()
                    if (it != null) {
                        initializeMovieList(it)
                    }
                } catch (ex: Exception) {
                    Log.e(TAG, ex.message!!)
                }
            })
            showLoader()
            editTextInputLayout = findViewById(R.id.edit_text_input_layout)
            editTextInputLayout.setEndIconOnClickListener {
                search.text.clear()
                showRecyclerView()
                appLauncherViewModel.appInfoList.value?.let { it1 -> appInfoAdapter.updateList(it1) }
            }

        }catch (ex: Exception){
            Log.e(TAG, ex.toString());
        }
    }
    private fun initializeMovieList(appInfo: ArrayList<AppInfo>){
        if(!::appInfoAdapter.isInitialized){
            appInfoAdapter= AppListAdapter(this, appInfo)
            recycler.adapter = appInfoAdapter
        }

    }
    private fun showLoader() {
        try {
            if (progressBar.visibility != View.VISIBLE) {
                progressBar.visibility = View.VISIBLE
            }
        } catch (ex: java.lang.Exception) {
            Log.e(TAG,ex.message!!)
        }
    }

    private fun hideLoader() {
        try {
            if (progressBar.visibility == View.VISIBLE) {
                progressBar.visibility = View.GONE
            }
        } catch (ex: java.lang.Exception) {
            Log.e(TAG,ex.message!!)
        }
    }
    private fun initializeSupportBar(){
        val back: ImageView

        try {
            supportActionBar!!.displayOptions= ActionBar.DISPLAY_SHOW_CUSTOM
            supportActionBar!!.setDisplayShowCustomEnabled(true)
            supportActionBar!!.setCustomView(R.layout.custom_action_bar)
            val parent = supportActionBar?.customView?.parent as Toolbar
            parent.setPadding(0, 0, 0, 0)//for tab otherwise give space in tab
            parent.setContentInsetsAbsolute(0, 0)
            val view = supportActionBar!!.customView
            back=view.findViewById(R.id.back)
            back.setOnClickListener {
                finish()
            }
            search=view.findViewById(R.id.edit_text);
            search.addTextChangedListener(object:TextWatcher{
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    filterSearch(s.toString())

                }

            })

        }catch (ex:Exception){
            Log.e(TAG,ex.toString())
        }
    }
    private fun filterSearch(searchString:String){
        val list=ArrayList<AppInfo>()
        val originalList=appLauncherViewModel.appInfoList.value
        try {
            if(originalList!=null){
                for(item in originalList)
                    if(item.appName.contains(searchString)){
                    list.add(item)
                }
            }
            if(list.isNotEmpty()){
                hideSearchError()
                showRecyclerView()
                appInfoAdapter.updateList(list)
            }else{
                hideRecyclerView()
                showSearchError()
            }
        }catch (ex:Exception){
            Log.e(TAG,ex.toString())
        }
    }
    private fun showSearchError() {
        val error: TextView = findViewById(R.id.search_error)
        if (!error.isVisible) {
            error.visibility = View.VISIBLE
        }
    }

    private fun hideSearchError() {
        val error: TextView = findViewById(R.id.search_error)
        if (error.isVisible) {
            error.visibility = View.GONE
        }
    }
    private fun showRecyclerView() {
        if (this::recycler.isInitialized && !recycler.isVisible) {
            recycler.visibility = View.VISIBLE
        }
    }

    private fun hideRecyclerView() {
        if (this::recycler.isInitialized && recycler.isVisible) {
            recycler.visibility = View.GONE
        }
    }

}