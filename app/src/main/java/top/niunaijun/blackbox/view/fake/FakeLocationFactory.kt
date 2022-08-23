package top.niunaijun.blackbox.view.fake

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import top.niunaijun.blackbox.data.FakeLocationRepository

/**
 *
 * @Author: BlackBoxing
 * @CreateDate: 2022/3/14
 */
class FakeLocationFactory(private val repo: FakeLocationRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FakeLocationViewModel(repo) as T
    }
}
