package com.matrimonymandaps.vendor.data.di


import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.google.firebase.iid.FirebaseInstanceId
import com.matrimonymandaps.vendor.BuildConfig
import com.matrimonymandaps.vendor.contract.IRemoteDataSource
import com.matrimonymandaps.vendor.contract.IRepository
import com.matrimonymandaps.vendor.contract.IWebService
import com.matrimonymandaps.vendor.data.model.request.AutoLoginRequest
import com.matrimonymandaps.vendor.domain.SendAutoLoginRequest
import com.matrimonymandaps.vendor.domain.SendLogoutRequest
import com.matrimonymandaps.vendor.domain.SendOtpApiRequest
import com.matrimonymandaps.vendor.domain.SendVerifyOtpRequest
import com.matrimonymandaps.vendor.network.FHPRepository
import com.matrimonymandaps.vendor.network.FiveHundredPixelsAPI
import com.matrimonymandaps.vendor.network.NetworkDataSource
import com.matrimonymandaps.vendor.network.RetrofitWebService
import com.matrimonymandaps.vendor.ui.base.BaseActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


/**
 * Hilt Module class that builds our dependency graph
 * @author Prasan
 * @since 1.0
 */
@InstallIn(ActivityComponent::class)
@Module
object HiltDependenciesModule {
    var credentials = BuildConfig.ApiUserName + ":" + BuildConfig.ApiPassword
    val basic = "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)



    @Provides
    fun provideBaseActivity(activity: Activity): BaseActivity {
        check(activity is BaseActivity) { "Every Activity is expected to extend BaseActivity" }
        return activity as BaseActivity
    }

    @Provides
    fun provideFirebase()=FirebaseInstanceId.getInstance()


    @Provides
    fun provideSharedPreference(@ApplicationContext context: Context): SharedPreferences? {
        return context.getSharedPreferences("mandap_pref", Context.MODE_PRIVATE)
    }


    /**
     * Returns the [HttpLoggingInterceptor] instance with logging level set to body
     * @since 1.0.0
     */
    @Provides
    fun provideLoggingInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }


    /**
     * Provides an [OkHttpClient]
     * @param loggingInterceptor [HttpLoggingInterceptor] instance
     * @since 1.0.0
     */
    @Provides
    fun provideOKHttpClient(loggingInterceptor: HttpLoggingInterceptor) = OkHttpClient().apply {
        OkHttpClient.Builder().run {

            addInterceptor(loggingInterceptor)
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .addInterceptor { chain ->
                        val newRequest = chain.request().newBuilder()
                                .addHeader("Authorization", basic)
                                .build()
                        chain.proceed(newRequest)
                    }
                     build()
        }
    }

    /**
     * Returns a [MoshiConverterFactory] instance
     * @since 1.0.0
     */
    @Provides
    fun provideMoshiConverterFactory(): MoshiConverterFactory = MoshiConverterFactory.create()

    /**
     * Returns an instance of the [FiveHundredPixelsAPI] interface for the retrofit class
     * @return [FiveHundredPixelsAPI] impl
     * @since 1.0.0
     */
    @Provides
    fun provideRetrofitInstance(
            client: OkHttpClient,
            moshiConverterFactory: MoshiConverterFactory
    ): FiveHundredPixelsAPI =
            Retrofit.Builder().run {
                baseUrl(BuildConfig.ServerURL)
                addConverterFactory(moshiConverterFactory)
                client(client)
                build()
            }.run {
                create(FiveHundredPixelsAPI::class.java)
            }


    /**
     * Returns a [IWebService] impl
     * @param retrofitClient [FiveHundredPixelsAPI] retrofit interface
     * @since 1.0.0
     */
    @Provides
    fun providesRetrofitService(retrofitClient: FiveHundredPixelsAPI): IWebService =
            RetrofitWebService(retrofitClient)

    /**
     * Returns a [IRemoteDataSource] impl
     * @param webService [IWebService] instance
     * @since 1.0.0
     */
    @Provides
    fun providesNetworkDataSource(webService: IWebService): IRemoteDataSource =
            NetworkDataSource(webService)

    /**
     * Returns a singleton [IRepository] implementation
     * @param remoteDataSource [IRemoteDataSource] implementation
     * @since 1.0.0
     */
    @Provides
    fun provideRepository(remoteDataSource: IRemoteDataSource): IRepository =
            FHPRepository(remoteDataSource)

    /**
     * Returns a [GetPopularPhotosUseCase] instance
     * @param repository [IRepository] impl
     * @since 1.0.0
     */
    @Provides
    fun provideUseCase(repository: IRepository): SendOtpApiRequest =
            SendOtpApiRequest(
            repository
        )

    @Provides
    fun provideVerifyOtpUsecase(repository: IRepository): SendVerifyOtpRequest =
            SendVerifyOtpRequest(
                    repository
            )

    @Provides
    fun provideAutoLoginUseCase(repository: IRepository):SendAutoLoginRequest=SendAutoLoginRequest(repository)

    @Provides
    fun provideLogoutUseCase(repository: IRepository):SendLogoutRequest= SendLogoutRequest(repository)


}