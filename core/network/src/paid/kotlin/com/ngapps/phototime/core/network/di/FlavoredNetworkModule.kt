package com.ngapps.phototime.core.network.di

import com.ngapps.phototime.core.network.SyncPtNetworkDataSource
import com.ngapps.phototime.core.network.UploadPtNetworkDataSource
import com.ngapps.phototime.core.network.retrofit.SyncRetrofitPtNetwork
import com.ngapps.phototime.core.network.retrofit.UploadRetrofitPtNetwork
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface FlavoredNetworkModule {

    @Binds
    fun bindsSyncRetrofitPtNetwork(impl: SyncRetrofitPtNetwork): SyncPtNetworkDataSource

    @Binds
    fun bindsUploadRetrofitPtNetwork(impl: UploadRetrofitPtNetwork): UploadPtNetworkDataSource
}
