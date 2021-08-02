package com.appinfomodule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ApplicationInfoProvider {
    private static ApplicationInfoProvider applicationInfoProvider;
    private static CompositeDisposable compositeDisposable;
    private AppInfoDetailListener appInfoDetailListener;
    private AppStatusInfoListener appStatusInfoListener;
    private ApplicationInfoProvider(){

    }
    public static ApplicationInfoProvider getApplicationInfoProvider(){
        if(applicationInfoProvider==null){
            compositeDisposable=new CompositeDisposable();
            applicationInfoProvider=new ApplicationInfoProvider();
        }
        return applicationInfoProvider;
    }
    public void addAppInfoDetailListener(AppInfoDetailListener appInfoDetailListener){
        this.appInfoDetailListener=appInfoDetailListener;
    }
    public void addAppStatusListener(AppStatusInfoListener appStatusInfoListener){
        this.appStatusInfoListener=appStatusInfoListener;
    }
    private List<AppInfo> getInstalledApps(Context context) {
        List<AppInfo> apps = new ArrayList<AppInfo>();
        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if ((!isSystemPackage(p))) {
                String appName = p.applicationInfo.loadLabel(context.getPackageManager()).toString();
                Drawable icon = p.applicationInfo.loadIcon(context.getPackageManager());
                String packages = p.applicationInfo.packageName;
                if(!getLauncherActivityName(context,packages).isEmpty()){
                    apps.add(new AppInfo(appName, packages,p.versionName,p.versionCode,getLauncherActivityName(context,packages),icon));
                }

            }
        }
        Collections.sort(apps, (o1, o2) -> o1.getAppName().compareTo(o2.getAppName()));
        return apps;
    }
    private String getLauncherActivityName(Context context,String packageName){
        try{
            String activityName = "";
            final PackageManager pm = context.getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage(packageName);
            List<ResolveInfo> activityList = pm.queryIntentActivities(intent,0);
            if(activityList != null){
                activityName = activityList.get(0).activityInfo.name;
            }
            return activityName;
        }catch (Exception ex){
            Log.e("getLauncherActivityName",ex.toString());
        }
        return "";

    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return (pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }
    public void observeAppInfo(Context context) {
        Observable<List<AppInfo>> appInfoObservable = Observable.fromCallable(() -> getInstalledApps(context));
         appInfoObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> compositeDisposable.add(disposable))
                .subscribe(appInfo -> {
                    if(appInfoDetailListener!=null){
                        appInfoDetailListener.appDetails(appInfo);
                    }
                });

    }
    public void clearDisposable(){
        compositeDisposable.clear();
    }
    private LocalBroadcastManager localBroadcastManager;
    public void initializeAppDetectReceiver(Context context){
        localBroadcastManager = LocalBroadcastManager.getInstance(context.getApplicationContext());
        IntentFilter mFilter = new IntentFilter("android.intent.action.PACKAGE_INSTALL");
        mFilter.addAction("android.intent.action.PACKAGE_ADDED");
        mFilter.addAction("android.intent.action.PACKAGE_REMOVED ");
        mFilter.addAction("android.intent.action.PACKAGE_FULLY_REMOVED");
        mFilter.addDataScheme("package");
        mFilter.setPriority(999);
        localBroadcastManager.registerReceiver(receiver,mFilter);
    }
    private BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(appStatusInfoListener!=null){
                appStatusInfoListener.appDetectInfo("installed","");
            }

        }
    };
    public void unregisterAppDetectReceiver(Context context){
        if(receiver!=null){
            LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
        }
    }
}
