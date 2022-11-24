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
                //HookHelper.dumpDex(packageName)

                if(packageName.equals("xyz.aethersx2.android")) {
                    HookHelper.hookClassAllMethods(context!!.classLoader,
                        object : XC_MethodHook() {
                            override fun beforeHookedMethod(param: MethodHookParam?) {
                                super.beforeHookedMethod(param)
                                var argsstr = "("
                                for (item in param!!.args) {
                                    argsstr += item.toString()
                                    argsstr += ","
                                }
                                if(param.args.size > 0) {
                                    argsstr = argsstr.subSequence(0,argsstr.length-1) as String
                                }
                                argsstr += ")"
                                Log.d(
                                    "Pipedvc",
                                    "beforeHookedMethod:" +
                                            param.thisObject.javaClass.name + "->" + param.method.name + "" + argsstr
                                )
                            }
                        }, "android.app.ContextImpl\$ApplicationContentResolver", "android.content.ContentResolver", "android.content.ContentInterface", "android.app.Application");
//                    HookHelper.hookClassAllMethods(context!!.classLoader,
//                        object : XC_MethodHook() {
//                            override fun beforeHookedMethod(param: MethodHookParam?) {
//                                super.beforeHookedMethod(param)
//                                var argsstr = "("
//                                for (item in param!!.args) {
//                                    argsstr += item.toString()
//                                    argsstr += ","
//                                }
//                                if(param.args.size > 0) {
//                                    argsstr = argsstr.subSequence(0,argsstr.length-1) as String
//                                }
//                                argsstr += ")"
//                                Log.d(
//                                    "Pipedvc",
//                                    "beforeHookedMethod:" +
//                                            "NativeLibrary" + "->" + param.method.name + "" + argsstr
//                                )
//                            }
//                            override fun afterHookedMethod(param: MethodHookParam?) {
//                                super.afterHookedMethod(param)
//                                var argsstr = "("
//                                for (item in param!!.args) {
//                                    argsstr += item.toString()
//                                    argsstr += ","
//                                }
//                                if(param.args.size > 0) {
//                                    argsstr = argsstr.subSequence(0,argsstr.length-1) as String
//                                }
//                                argsstr += ")"
//                                var result = ""
//                                if (param.result is String) {
//                                    result = param.result as String
//                                } else if (param.result is Array<*>) {
//                                    val r = param.result as Array<*>
//                                    result += "["
//                                    for (t in r) {
//                                        if (t is String) {
//                                            result += t
//                                        } else {
//                                            result += t.toString()
//                                        }
//                                        result += "|"
//                                    }
//                                    if(r.size > 0) {
//                                        result = result.subSequence(0,result.length-1) as String
//                                    }
//                                    result += "]"
//                                } else if(param.result == null) {
//                                    result = "void"
//                                } else {
//                                    result = param.result.toString()
//                                }
//                                Log.d(
//                                    "Pipedvc",
//                                    "callafterHooked:" +
//                                    "NativeLibrary" + "->" + param.method.name + "" + argsstr + "=" + result
//                                )
//
//                            }
//                        }, "xyz.aethersx2.android.NativeLibrary")
                }
            }


            override fun beforeApplicationOnCreate(
                packageName: String?,
                processName: String?,
                application: Application?,
                userId: Int
            ) {
//                if(packageName.equals("xyz.aethersx2.android")) {
//                    HookHelper.hookPackageSign(application, "3082058830820370a00302010202146746c609cd52b92de49eb65a6e41524e5a33e41c300d06092a864886f70d01010b05003074310b3009060355040613025553311330110603550408130a43616c69666f726e6961311630140603550407130d4d6f756e7461696e205669657731143012060355040a130b476f6f676c6520496e632e3110300e060355040b1307416e64726f69643110300e06035504031307416e64726f69643020170d3231313130373039353431325a180f32303531313130373039353431325a3074310b3009060355040613025553311330110603550408130a43616c69666f726e6961311630140603550407130d4d6f756e7461696e205669657731143012060355040a130b476f6f676c6520496e632e3110300e060355040b1307416e64726f69643110300e06035504031307416e64726f696430820222300d06092a864886f70d01010105000382020f003082020a0282020100cd6d83dca90298abd53480aecfc72e693ae300ecaca46057de1b5b8172bb777b20d0eaa6854fe3a9f8765a50fccfb1ee113894b103991aa59c978577e4a79e2bf5ef7eaeccf2a63ec42c61bdfbb4ba903a7d8cb7cb496e0de20267a8b6eaab17d62e74afbc4975b5eeb2f8b93f8ca01aee1307a40300952a4e744423041259be00b1e055c5e8055ee805084298d31b0851dcf5e82be5c5972fa0a8848a14ae6c0a4ab854a7970c6b39b02a1283fe24b5b521786e56dddd34499455d0b726be6115e0f680b0949373ac7b8356443894847a9805a72be4ba268041715602ad4f51a733d1d25736ee2cec9a45025da4375f76dca7bae133a83f35e890005ccf7ecc07fdcfb42b1c76145c8379b8c9d65bf5b2da5439b3c9fa855817e7de4109367462c797132db9cfa2018de9d7e9da76769cc3df2a149651c425161a8b5aeeeda10fb167b7f34d80eed43406522dbf18880e0fe67d3bd70f6dacb009dea0f4fbf15f2a37da4765f80a4c8bded02cd05699b23ec9c52c6d193261b61b3d5af604b2fee5f09d933ddecd399587f6f8e6a2292012e5cb1ffd9d7842846efb3636bd175acac159956b36c9ca04fd8745f6865cd0de1189e4edffefb11c3463debd18ca7d473663d855f3497f7fa55982415b76de87c2b1314d8461785f206f5d2e718084c2ab7529b7922f64319db52f5ec3770fbffae4a03ffcd25ba0194f69eb40a10203010001a310300e300c0603551d13040530030101ff300d06092a864886f70d01010b050003820201001396bba44d9535eebe4d1a3a0ccbcdd5f9d2015161bca29d9ccf419baf59a0f6d90935ef8ba4349aa1e29304e79142394b13d547a0bef63444badab35f60d89d0073d27c9afd470b953f696743570a042057ea350297dd06958882fa71791bab455ff57163aafc94fe73a3a5477f90e32c54bb7e296721d91a69f34297b40a85bb040f895c26be5a0f5f175a3790120f3c75e2d451d1ddf3644ed74696d450161daddfc4c6aaa7e691bdf78b32cecbfc3b461bddeb1e521fbf525b24f03835a6d01c6c3d536821d918193491afa6c6b622b7871b5e04bbbeafeff5aec3d0430cc9827ce7d76d0fd8e4a395f39f93db6a9c20710271fa67ea4feb30d899195838a833c82365c477db37c47aaa1f6d427fa30bb302c5dcc13afdbb267190490609dc6ba17b3e505fe3682875c1789eb30928c214fcc23ac71eedc9730e763d120ea3301fa4b366f10bf340b3f3843c03001b191e7bfee04a25dfd69c9cf92c563dd96241c9d77ca62c271594369e9b67864d2bdd578cd68ed879dce6fc818c44d61daa6da711341fc93a94199dc5db2e599e4216f8f793a0a537ff7b6168fc6ffd185d5a7894975754275c05b09dbc59184d619e20e56f01d8acf8f9a4fb34814782254205811bca490e280bc9495b1c62342af3ab6d53d702da7186bfa7bd26335e57710a5eb8e9937df5033c9754b8bcacd1afaf29e2be8f54dfa01d35fc3f53")
//                }
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