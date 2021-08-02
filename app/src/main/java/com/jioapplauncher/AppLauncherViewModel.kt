package com.jioapplauncher

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.appinfomodule.AppInfo
import com.appinfomodule.AppInfoDetailListener
import com.appinfomodule.ApplicationInfoProvider

class AppLauncherViewModel(application: Application) : AndroidViewModel(application),AppInfoDetailListener {
    val appInfoList: MutableLiveData<ArrayList<AppInfo>> = MutableLiveData()
    private val context = getApplication<Application>().applicationContext
    override fun appDetails(details: MutableList<AppInfo>?) {
        if (details!=null){
            appInfoList.value= details as ArrayList<AppInfo>?
        }
    }

    fun getAllAppInfo(){
        ApplicationInfoProvider.getApplicationInfoProvider().observeAppInfo(context)
        ApplicationInfoProvider.getApplicationInfoProvider().addAppInfoDetailListener (this)

    }

}