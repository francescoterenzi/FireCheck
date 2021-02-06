/*
 * Created by Francesco Terenzi on 21/11/20 20.51
 * Copyright (C) 2020 FireCheck
 *  Last modified 21/11/20 19.36
 */

package com.fireless.firecheck.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.http.*
import com.fireless.firecheck.models.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val BASE_URL = "https://firecheck-api.herokuapp.com" //BASE URL

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()


/**
 * USER MANAGEMENT
 */
interface SetUserApiService {
    @POST("users/")
    @FormUrlEncoded
    fun setUser(@Field("id") uuid: String):
            Deferred<UserProperty>
}

interface GetUserControlsApiService {
    @GET("users/{id}/controls")
    fun getUserControls(@Path("id") uuid: String):
            Deferred<List<Maintenance>>
}

/**
 * EXTINGUISHER MANAGEMENT
 */
interface SetExtinguisherApiService {
    @POST("extinguishers/")
    @FormUrlEncoded
    fun setExtinguisher(
        @Field("id") id: String,
        @Field("company_id") companyId: Int):
            Deferred<Extinguisher>
}

/**
 * MAINTENANCE MANAGEMENT
 */
interface SetMaintenanceApiService {
    @POST("controls/")
    @FormUrlEncoded
    fun setMaintenance(
        @Field("extinguisher_id") extinguisherId: String,
        @Field("user_id") userId: String):
            Deferred<Extinguisher>
}

object UserApi {

    val retrofitServiceSetUser: SetUserApiService by lazy {
        retrofit.create(SetUserApiService::class.java)
    }

    val retrofitServiceGetUserControls: GetUserControlsApiService by lazy {
        retrofit.create(GetUserControlsApiService::class.java)
    }
}

object ExtinguisherApi {

    val retrofitServiceSetExtinguisher: SetExtinguisherApiService by lazy {
        retrofit.create(SetExtinguisherApiService::class.java)
    }

}

object MaintenanceApi {

    val retrofitServiceSetMaintenance: SetMaintenanceApiService by lazy {
        retrofit.create(SetMaintenanceApiService::class.java)
    }

}


