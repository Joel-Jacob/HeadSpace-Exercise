package com.example.hsexercise.common

import com.example.hsexercise.feature.database.FeatureModel
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface PicturesService {

    @GET("/v2/list")
    fun getPicturesList():Observable<List<FeatureModel>>

    @GET("/v2/list")
    fun getPicturesPage(
    @Query("page")page:Int):Observable<List<FeatureModel>>
}