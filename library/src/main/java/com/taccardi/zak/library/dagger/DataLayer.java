package com.taccardi.zak.library.dagger;

/**
 * Created by Zhuinden on 2017.07.08..
 */
public class DataLayer {
    /**
     * The main/UI thread.
     */
    public static final String MAIN = "main";
    /**
     * A computation threadpool. Should be used for object creation/calculation.
     */
    public static final String COMP = "comp";
    /**
     * A thread for accessing the database.
     */
    public static final String DISK = "disk";

    public static final String NETWORK = "network";

    public static final String TRAMPOLINE = "trampoline";
}
