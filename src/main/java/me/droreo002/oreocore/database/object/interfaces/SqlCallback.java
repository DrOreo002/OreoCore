package me.droreo002.oreocore.database.object.interfaces;

import java.util.List;
import java.util.Map;

public interface SqlCallback<T> {

    void onSuccess(T done);
    void onError(Throwable throwable);

}
