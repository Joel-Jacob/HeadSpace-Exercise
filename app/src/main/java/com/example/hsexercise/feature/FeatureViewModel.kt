package com.example.hsexercise.feature

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hsexercise.common.DatabaseProvider
import com.example.hsexercise.common.NetworkProvider
import com.example.hsexercise.feature.database.FeatureModel
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class FeatureViewModel(application: Application) : AndroidViewModel(application) {

//    lateinit var application: Application
    var database = DatabaseProvider.provideRoomDatabase(application)

    fun getPictures(): Observable<List<FeatureModel>> {
        return NetworkProvider
            .getPictures()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    fun getPicturesPage(page: Int): Observable<List<FeatureModel>> {
        return NetworkProvider
            .getPicturesPage(page)
            .subscribeOn(Schedulers.io())
            .map {
                database.featureTableDao().insertAll(it)
                it
            }
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getRoomItems(): Maybe<List<FeatureModel>> {
        return database.featureTableDao().getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    class Factory(val app: Application) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) = FeatureViewModel(app) as T
    }
}
