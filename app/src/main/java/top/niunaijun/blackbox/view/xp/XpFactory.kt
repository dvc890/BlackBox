package top.niunaijun.blackbox.view.xp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import top.niunaijun.blackbox.data.XpRepository

@Suppress("UNCHECKED_CAST")
class XpFactory(private val repo: XpRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return XpViewModel(repo) as T
    }
}
