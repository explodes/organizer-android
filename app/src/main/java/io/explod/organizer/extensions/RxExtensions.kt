package io.explod.organizer.extensions

import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers


fun <T> Flowable<T>.observeOnMain(): Flowable<T> {
    return this.observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.observeOnMain(): Observable<T> {
    return this.observeOn(AndroidSchedulers.mainThread())
}

fun <T> Single<T>.observeOnMain(): Single<T> {
    return this.observeOn(AndroidSchedulers.mainThread())
}

fun <T> Maybe<T>.observeOnMain(): Maybe<T> {
    return this.observeOn(AndroidSchedulers.mainThread())
}

fun Completable.observeOnMain(): Completable {
    return this.observeOn(AndroidSchedulers.mainThread())
}
