package com.gotchureviews.app.di;

import com.gotchureviews.app.data.remote.ApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.serialization.json.Json;
import okhttp3.OkHttpClient;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class AppModule_ProvideApiServiceFactory implements Factory<ApiService> {
  private final Provider<OkHttpClient> okHttpClientProvider;

  private final Provider<Json> jsonProvider;

  public AppModule_ProvideApiServiceFactory(Provider<OkHttpClient> okHttpClientProvider,
      Provider<Json> jsonProvider) {
    this.okHttpClientProvider = okHttpClientProvider;
    this.jsonProvider = jsonProvider;
  }

  @Override
  public ApiService get() {
    return provideApiService(okHttpClientProvider.get(), jsonProvider.get());
  }

  public static AppModule_ProvideApiServiceFactory create(
      Provider<OkHttpClient> okHttpClientProvider, Provider<Json> jsonProvider) {
    return new AppModule_ProvideApiServiceFactory(okHttpClientProvider, jsonProvider);
  }

  public static ApiService provideApiService(OkHttpClient okHttpClient, Json json) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideApiService(okHttpClient, json));
  }
}
