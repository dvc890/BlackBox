package top.niunaijun.bcore.utils.compat;

import black.android.os.BRStrictMode;

public class StrictModeCompat {
    public static int DETECT_VM_FILE_URI_EXPOSURE = BRStrictMode.get().DETECT_VM_FILE_URI_EXPOSURE() == null ?
            (0x20 << 8) : BRStrictMode.get().DETECT_VM_FILE_URI_EXPOSURE();

    public static int PENALTY_DEATH_ON_FILE_URI_EXPOSURE = BRStrictMode.get().PENALTY_DEATH_ON_FILE_URI_EXPOSURE() == null ?
            (0x04 << 24) : BRStrictMode.get().PENALTY_DEATH_ON_FILE_URI_EXPOSURE();

    public static void disableDeathOnFileUriExposure() {
        try {
            BRStrictMode.get().disableDeathOnFileUriExposure();
        } catch (Throwable e) {
            try {
                int sVmPolicyMask = BRStrictMode.get().sVmPolicyMask();
                sVmPolicyMask &= ~(DETECT_VM_FILE_URI_EXPOSURE | PENALTY_DEATH_ON_FILE_URI_EXPOSURE);
                BRStrictMode.get()._set_sVmPolicyMask(sVmPolicyMask);
            } catch (Throwable e2) {
                e2.printStackTrace();
            }
        }
    }
}
