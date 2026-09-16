package com.gotchureviews.app.data.repository;

import com.gotchureviews.app.data.remote.ApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class ReviewRepository_Factory implements Factory<ReviewRepository> {
  private final Provider<ApiService> apiServiceProvider;

  public ReviewRepository_Factory(Provider<ApiService> apiServiceProvider) {
    this.apiServiceProvider = apiServiceProvider;
  }

  @Override
  public ReviewRepository get() {
    return newInstance(apiServiceProvider.get());
  }

  public static ReviewRepository_Factory create(Provider<ApiService> apiServiceProvider) {
    return new ReviewRepository_Factory(apiServiceProvider);
  }

  public static ReviewRepository newInstance(ApiService apiService) {
    return new ReviewRepository(apiService);
  }
}
