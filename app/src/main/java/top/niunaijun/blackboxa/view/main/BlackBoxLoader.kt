package top.niunaijun.blackboxa.view.main

import android.app.Application
import android.content.Context
import android.util.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackbox.app.BActivityThread
import top.niunaijun.blackbox.app.configuration.AppLifecycleCallback
import top.niunaijun.blackbox.app.configuration.ClientConfiguration
import top.niunaijun.blackboxa.app.App
import top.niunaijun.blackboxa.biz.cache.AppSharedPreferenceDelegate
import top.niunaijun.blackboxa.util.HookHelper
import java.io.File

/**
 *
 * @Description:
 * @Author: wukaicheng
 * @CreateDate: 2021/5/6 23:38
 */
class BlackBoxLoader {


    private var mHideRoot by AppSharedPreferenceDelegate(App.getContext(), false)
    private var mHideXposed by AppSharedPreferenceDelegate(App.getContext(), false)
    private var mDaemonEnable by AppSharedPreferenceDelegate(App.getContext(), false)
    private var mShowShortcutPermissionDialog by AppSharedPreferenceDelegate(App.getContext(), true)


    fun hideRoot(): Boolean {
        return mHideRoot
    }

    fun invalidHideRoot(hideRoot: Boolean) {
        this.mHideRoot = hideRoot
    }

    fun hideXposed(): Boolean {
        return mHideXposed
    }

    fun invalidHideXposed(hideXposed: Boolean) {
        this.mHideXposed = hideXposed
    }

    fun daemonEnable(): Boolean {
        return mDaemonEnable
    }

    fun invalidDaemonEnable(enable: Boolean) {
        this.mDaemonEnable = enable
    }

    fun showShortcutPermissionDialog(): Boolean {
        return mShowShortcutPermissionDialog
    }

    fun invalidShortcutPermissionDialog(show: Boolean) {
        this.mShowShortcutPermissionDialog = show
    }

    fun getBlackBoxCore(): BlackBoxCore {
        return BlackBoxCore.get()
    }

    fun addLifecycleCallback() {
        BlackBoxCore.get().addAppLifecycleCallback(object : AppLifecycleCallback() {
            override fun beforeCreateApplication(
                packageName: String?,
                processName: String?,
                context: Context?,
                userId: Int
            ) {
                Log.d(
                    TAG,
                    "beforeCreateApplication: pkg $packageName, processName $processName,userID:${BActivityThread.getUserId()}"
                )
                HookHelper.dumpDex(packageName)
                if(packageName.equals("xyz.aethersx2.android")) {
                    HookHelper.hookClassAllMethods(context!!.classLoader,
                        object : XC_MethodHook() {
                            override fun afterHookedMethod(param: MethodHookParam?) {
                                super.afterHookedMethod(param)
                                var argsstr = "("
                                for (item in param!!.args) {
                                    argsstr += item.toString()
                                    argsstr += ","
                                }
                                if(param.args.size > 0) {
                                    argsstr = argsstr.subSequence(0,argsstr.length-1) as String
                                }
                                argsstr += ")"
                                var result = ""
                                if (param.result is String) {
                                    result = param.result as String
                                } else if (param.result is Array<*>) {
                                    val r = param.result as Array<*>
                                    result += "["
                                    for (t in r) {
                                        if (t is String) {
                                            result += t
                                        } else {
                                            result += t.toString()
                                        }
                                        result += "|"
                                    }
                                    if(r.size > 0) {
                                        result = result.subSequence(0,result.length-1) as String
                                    }
                                    result += "]"
                                } else if(param.result == null) {
                                    result = "void"
                                } else {
                                    result = param.result.toString()
                                }
                                Log.d(
                                    "Pipedvc",
                                    "callafterHooked:" +
                                    "NativeLibrary" + "->" + param.method.name + "" + argsstr + "=" + result
                                )

                            }
                        }, "xyz.aethersx2.android.NativeLibrary")
                }
            }


            override fun beforeApplicationOnCreate(
                packageName: String?,
                processName: String?,
                application: Application?,
                userId: Int
            ) {
                Log.d(TAG, "beforeApplicationOnCreate: pkg $packageName, processName $processName")
            }

            override fun afterApplicationOnCreate(
                packageName: String?,
                processName: String?,
                application: Application?,
                userId: Int
            ) {
                Log.d(TAG, "afterApplicationOnCreate: pkg $packageName, processName $processName")
//                RockerManager.init(application,userId)
            }
        })
    }

    fun attachBaseContext(context: Context) {
        BlackBoxCore.get().doAttachBaseContext(context, object : ClientConfiguration() {
            override fun getHostPackageName(): String {
                return context.packageName
            }

            override fun isHideRoot(): Boolean {
                return mHideRoot
            }

            override fun isHideXposed(): Boolean {
                return mHideXposed
            }

            override fun isEnableDaemonService(): Boolean {
                return mDaemonEnable
            }

            override fun requestInstallPackage(file: File?, userId: Int): Boolean {
                val packageInfo =
                    context.packageManager.getPackageArchiveInfo(file!!.absolutePath, 0)
                return false
            }
        })
    }

    fun doOnCreate(context: Context) {
        BlackBoxCore.get().doCreate()

    }


    companion object {

        val TAG: String = BlackBoxLoader::class.java.simpleName

    }

}