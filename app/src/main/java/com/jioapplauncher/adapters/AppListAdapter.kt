package com.jioapplauncher.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.appinfomodule.AppInfo
import com.bumptech.glide.Glide
import com.jioapplauncher.R

class AppListAdapter(var context: Context, var appList: ArrayList<AppInfo>) :RecyclerView.Adapter<AppListAdapter.ItemHolder>() {
    val TAG="AppListAdapter"

    class ItemHolder(v: View): RecyclerView.ViewHolder(v){
        private val appIcon: ImageView = v.findViewById(R.id.app_icon)
        private val appName:TextView=v.findViewById(R.id.app_name)
        private val packageName:TextView=v.findViewById(R.id.package_name)
        private val activityName:TextView=v.findViewById(R.id.activity_name)
        private val versionNameCode:TextView=v.findViewById(R.id.version_name_code)
        private val main:ConstraintLayout=v.findViewById(R.id.main)
        fun bind(data: AppInfo, context: Context){
            Glide.with(context)
                .load(data.launchIcon).placeholder(R.drawable.ic_launcher_background).error(R.drawable.ic_launcher_background).into(
                    appIcon
                )
            appName.text=data.appName
            packageName.text=data.packageName
            packageName.isSelected=true
            packageName.requestFocus()
            activityName.text=data.launchActivityName
            activityName.isSelected=true
            activityName.requestFocus()
            versionNameCode.text=data.versionName+"( "+data.versionCode+" )"
            main.tag=data
            main.setOnClickListener{
                try {
                    val tagData=it.tag as AppInfo
                    val intent = context.packageManager.getLaunchIntentForPackage(tagData.packageName)
                    if(intent!=null){
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent)
                    }
                }catch (ex: Exception){
                    Log.e("click", ex.toString())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.appinfo_list_item, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        try {
            return holder.bind(appList[position], context)
        }catch (ex: Exception){
            Log.e(TAG, ex.message!!)
        }
    }

    override fun getItemCount(): Int {
        return appList.size
    }
    fun updateList(searchList:ArrayList<AppInfo>){
        appList=searchList
        notifyDataSetChanged()
    }
}