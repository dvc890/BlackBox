package top.niunaijun.bcore.fake.service;

import java.lang.reflect.Method;

import black.android.os.BRIDeviceIdentifiersPolicyServiceStub;
import black.android.os.BRServiceManager;
import top.niunaijun.bcore.BlackBoxCore;
import top.niunaijun.bcore.fake.hook.BinderInvocationStub;
import top.niunaijun.bcore.fake.hook.MethodHook;
import top.niunaijun.bcore.fake.hook.ProxyMethod;
import top.niunaijun.bcore.utils.Md5Utils;

/**
 * Created by Milk on 4/3/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class IDeviceIdentifiersPolicyProxy extends BinderInvocationStub {
    public IDeviceIdentifiersPolicyProxy() {
        super(BRServiceManager.get().getService("device_identifiers"));
    }

    @Override
    protected Object getWho() {
        return BRIDeviceIdentifiersPolicyServiceStub.get().asInterface(BRServiceManager.get().getService("device_identifiers"));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("device_identifiers");
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @ProxyMethod("getSerialForPackage")
    public static class x extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            /*args[0] = BlackBoxCore.getHostPkg();
            return method.invoke(who, args);*/
            return Md5Utils.md5(BlackBoxCore.getHostPkg());
        }
    }
}
