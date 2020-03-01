package com.example.hsexercise.common

import com.example.hsexercise.feature.database.FeatureModel
import io.reactivex.Observable
import retrofit2.http.GET

interface PicturesService {

    @GET("/v2/list")
    fun getPicturesList():Observable<List<FeatureModel>>
}