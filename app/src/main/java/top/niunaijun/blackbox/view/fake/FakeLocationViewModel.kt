package top.niunaijun.blackbox.view.fake

import androidx.lifecycle.MutableLiveData
import top.niunaijun.bcore.entity.location.BLocation
import top.niunaijun.blackbox.bean.FakeLocationBean
import top.niunaijun.blackbox.data.FakeLocationRepository
import top.niunaijun.blackbox.view.base.BaseViewModel

class FakeLocationViewModel(private val mRepo: FakeLocationRepository) : BaseViewModel() {
    val appsLiveData = MutableLiveData<List<FakeLocationBean>>()

    fun getInstallAppList(userID: Int) {
        launchOnUI {
            mRepo.getInstalledAppList(userID, appsLiveData)
        }
    }

    fun setPattern(userId: Int, pkg: String, pattern: Int) {
        launchOnUI {
            mRepo.setPattern(userId, pkg, pattern)
        }
    }

    fun setLocation(userId: Int, pkg: String, location: BLocation) {
        launchOnUI {
            mRepo.setLocation(userId, pkg, location)
        }
    }
}
