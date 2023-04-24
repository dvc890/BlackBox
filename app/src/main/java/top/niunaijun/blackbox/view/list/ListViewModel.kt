package top.niunaijun.blackbox.view.list

import androidx.lifecycle.MutableLiveData
import top.niunaijun.blackbox.bean.InstalledAppBean
import top.niunaijun.blackbox.data.AppsRepository
import top.niunaijun.blackbox.view.base.BaseViewModel

class ListViewModel(private val repo: AppsRepository) : BaseViewModel() {
    val appsLiveData = MutableLiveData<List<InstalledAppBean>>()
    val loadingLiveData = MutableLiveData<Boolean>()

    fun previewInstalledList() {
        launchOnUI{
            repo.previewInstallList()
        }
    }

    fun getInstallAppList(userID: Int) {
        launchOnUI {
            repo.getInstalledAppList(userID, loadingLiveData, appsLiveData)
        }
    }

    fun getInstalledModules() {
        launchOnUI {
            repo.getInstalledModuleList(loadingLiveData, appsLiveData)
        }
    }
}
