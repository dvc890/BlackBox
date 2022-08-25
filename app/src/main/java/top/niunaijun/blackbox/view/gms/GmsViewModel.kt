package top.niunaijun.blackbox.view.gms

import androidx.lifecycle.MutableLiveData
import top.niunaijun.blackbox.bean.GmsBean
import top.niunaijun.blackbox.bean.GmsInstallBean
import top.niunaijun.blackbox.data.GmsRepository
import top.niunaijun.blackbox.view.base.BaseViewModel

/**
 *
 * @Description: gms viewModel
 * @Author: BlackBox
 * @CreateDate: 2022/3/2 21:11
 */
class GmsViewModel(private val mRepo: GmsRepository) : BaseViewModel() {
    val mInstalledLiveData = MutableLiveData<List<GmsBean>>()
    val mUpdateInstalledLiveData = MutableLiveData<GmsInstallBean>()

    fun getInstalledUser() {
        launchOnUI {
            mRepo.getGmsInstalledList(mInstalledLiveData)
        }
    }

    fun installGms(userID: Int) {
        launchOnUI {
            mRepo.installGms(userID,mUpdateInstalledLiveData)
        }
    }

    fun uninstallGms(userID: Int) {
        launchOnUI {
            mRepo.uninstallGms(userID,mUpdateInstalledLiveData)
        }
    }
}
