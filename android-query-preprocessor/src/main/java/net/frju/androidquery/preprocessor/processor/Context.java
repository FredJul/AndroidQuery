package net.frju.androidquery.preprocessor.processor;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class Context {
    private final Types mTypes;
    private final Elements mElements;
    private final Filer mFiler;
    private final Messager mMessager;

    private static Context sInstance;

    public Types getTypeUtils() {
        return mTypes;
    }

    public Elements getElementUtils() {
        return mElements;
    }

    Filer getFiler() {
        return mFiler;
    }

    Messager getMessager() {
        return mMessager;
    }

    static void createInstance(ProcessingEnvironment env) {
        sInstance = new Context(env);
    }

    public static Context getInstance() {
        if (sInstance == null)
            throw new IllegalStateException("createInstance must be called before getInstance()");

        return sInstance;
    }

    private Context(ProcessingEnvironment env) {
        mTypes = env.getTypeUtils();
        mElements = env.getElementUtils();
        mFiler = env.getFiler();
        mMessager = env.getMessager();
    }
}