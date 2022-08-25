package top.niunaijun.blackbox.util

import top.niunaijun.blackbox.data.AppsRepository
import top.niunaijun.blackbox.data.FakeLocationRepository
import top.niunaijun.blackbox.data.GmsRepository
import top.niunaijun.blackbox.data.XpRepository
import top.niunaijun.blackbox.view.apps.AppsFactory
import top.niunaijun.blackbox.view.fake.FakeLocationFactory
import top.niunaijun.blackbox.view.gms.GmsFactory
import top.niunaijun.blackbox.view.list.ListFactory
import top.niunaijun.blackbox.view.xp.XpFactory

/**
 *
 * @Description:
 * @Author: wukaicheng
 * @CreateDate: 2021/4/29 22:38
 */
object InjectionUtil {
    private val appsRepository = AppsRepository()
    private val xpRepository = XpRepository()
    private val gmsRepository = GmsRepository()
    private val fakeLocationRepository = FakeLocationRepository()

    fun getAppsFactory() : AppsFactory {
        return AppsFactory(appsRepository)
    }

    fun getListFactory(): ListFactory {
        return ListFactory(appsRepository)
    }

    fun getXpFactory():XpFactory{
        return XpFactory(xpRepository)
    }

    fun getGmsFactory():GmsFactory{
        return GmsFactory(gmsRepository)
    }

    fun getFakeLocationFactory():FakeLocationFactory{
        return FakeLocationFactory(fakeLocationRepository)
    }
}
