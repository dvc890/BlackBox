package top.niunaijun.bcore.fake.service;

import black.android.net.wifi.BRIWifiManagerStub;
import black.android.os.BRServiceManager;
import top.niunaijun.bcore.fake.hook.BinderInvocationStub;

/**
 * @author Findger
 * @function
 * @date :2022/4/3 13:05
 **/
public class IWifiScannerProxy extends BinderInvocationStub {
    public IWifiScannerProxy() {
        super(BRServiceManager.get().getService("wifiscanner"));
    }

    @Override
    protected Object getWho() {
        return BRIWifiManagerStub.get().asInterface(BRServiceManager.get().getService("wifiscanner"));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("wifiscanner");
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }
}
