package android.os;

import android.os.Bundle;

oneway interface IRemoteCallback {
    void sendResult(in Bundle data);
}
