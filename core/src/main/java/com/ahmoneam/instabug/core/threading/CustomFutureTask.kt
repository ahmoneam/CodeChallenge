package com.ahmoneam.instabug.core.threading

import com.ahmoneam.instabug.core.di.SL
import com.ahmoneam.instabug.core.utils.test.IAndroidTestIdlingResource
import java.util.concurrent.Callable
import java.util.concurrent.CancellationException
import java.util.concurrent.FutureTask

class CustomFutureTask<T : Any?>(callable: Callable<T>) : FutureTask<T>(callable) {
    init {
        SL[IAndroidTestIdlingResource::class.java].increment()
    }

    private var onFinishCallback: ((T?, Throwable?) -> Unit)? = null

    fun onFinish(call: (T?, Throwable?) -> Unit): CustomFutureTask<T> {
        onFinishCallback = call
        return this
    }

    override fun done() {
        super.done()
        var data: T? = null
        var error: Throwable? = null
        try {
            if (isCancelled) error = CancellationException()
            else data = get()
        } catch (t: Throwable) {
            error = t
        }
        onFinishCallback?.invoke(data, error)
        SL[IAndroidTestIdlingResource::class.java].decrement()
    }
}