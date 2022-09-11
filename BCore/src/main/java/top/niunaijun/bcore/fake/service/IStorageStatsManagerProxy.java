package top.niunaijun.bcore.fake.service;

import android.content.Context;

import java.lang.reflect.Method;

import black.android.app.usage.BRIStorageStatsManagerStub;
import black.android.os.BRServiceManager;
import top.niunaijun.bcore.fake.hook.BinderInvocationStub;
import top.niunaijun.bcore.utils.MethodParameterUtils;

/**
 * Created by BlackBox on 2022/3/3.
 */
public class IStorageStatsManagerProxy extends BinderInvocationStub {
    public IStorageStatsManagerProxy() {
        super(BRServiceManager.get().getService(Context.STORAGE_STATS_SERVICE));
    }

    @Override
    protected Object getWho() {
        return BRIStorageStatsManagerStub.get().asInterface(BRServiceManager.get().getService(Context.STORAGE_STATS_SERVICE));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.STORAGE_STATS_SERVICE);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodParameterUtils.replaceFirstAppPkg(args);
        MethodParameterUtils.replaceLastUid(args);
        return super.invoke(proxy, method, args);
    }

    /*@ProxyMethod("queryStatsForUid")
    public static class QueryStatsForUid extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastUid(args);
            return method.invoke(who, args);
        }
    }*/
}
