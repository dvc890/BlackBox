package top.niunaijun.blackbox.view.xp

import androidx.lifecycle.MutableLiveData
import top.niunaijun.blackbox.bean.XpModuleInfo
import top.niunaijun.blackbox.data.XpRepository
import top.niunaijun.blackbox.view.base.BaseViewModel

class XpViewModel(private val repo: XpRepository) : BaseViewModel() {
    val appsLiveData = MutableLiveData<List<XpModuleInfo>>()
    val resultLiveData = MutableLiveData<String>()

    fun getInstalledModule() {
        launchOnUI {
            repo.getInstallModules(appsLiveData)
        }
    }

    fun installModule(source:String) {
        launchOnUI {
            repo.installModule(source, resultLiveData)
        }
    }

    fun unInstallModule(packageName: String) {
        launchOnUI {
            repo.unInstallModule(packageName, resultLiveData)
        }
    }
}
