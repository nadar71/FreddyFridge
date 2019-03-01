package eu.indiewalkabout.fridgemanager;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * --------------------------------------------------------------------------------
 * Global executor for all the app request; all tasks are grouped here.
 * Used to run threads outside main thread.
 * --------------------------------------------------------------------------------
 */
public class AppExecutors {
    // singleton instantiation
    private static final Object LOCK = new Object();
    private static AppExecutors singleInstance;
    private final Executor diskIO;
    private final Executor mainThread;
    private final Executor networkIO;

    // singleton constructor
    private AppExecutors(Executor diskIO,Executor mainThread,Executor networkIO) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
    }

    public static AppExecutors getInstance() {
        if(singleInstance == null){
            synchronized (LOCK){
                singleInstance = new AppExecutors(
                        Executors.newSingleThreadExecutor(),
                        Executors.newFixedThreadPool(3),
                        new MainThreadExecutor()
                );
            }
        }
        return singleInstance;
    }


    public Executor diskIO(){
        return diskIO;
    }

    public Executor mainThread(){
        return mainThread;
    }

    public Executor networkIO(){
        return networkIO;
    }


    private static  class MainThreadExecutor implements Executor{
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }


}
