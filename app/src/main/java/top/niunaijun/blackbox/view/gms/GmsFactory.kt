package top.niunaijun.blackbox.view.gms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import top.niunaijun.blackbox.data.GmsRepository

/**
 *
 * @Description:
 * @Author: BlackBox
 * @CreateDate: 2022/3/2 21:15
 */
class GmsFactory(private val repo:GmsRepository): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GmsViewModel(repo) as T
    }
}
