package me.droreo002.oreocore.database.object.interfaces;

@Deprecated
public interface SqlCallback<T> {

    void onSuccess(T done);
    void onError(Throwable throwable);

}
