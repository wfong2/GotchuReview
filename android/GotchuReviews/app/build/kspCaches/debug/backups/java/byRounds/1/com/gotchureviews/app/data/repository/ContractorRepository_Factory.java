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
public final class ContractorRepository_Factory implements Factory<ContractorRepository> {
  private final Provider<ApiService> apiServiceProvider;

  public ContractorRepository_Factory(Provider<ApiService> apiServiceProvider) {
    this.apiServiceProvider = apiServiceProvider;
  }

  @Override
  public ContractorRepository get() {
    return newInstance(apiServiceProvider.get());
  }

  public static ContractorRepository_Factory create(Provider<ApiService> apiServiceProvider) {
    return new ContractorRepository_Factory(apiServiceProvider);
  }

  public static ContractorRepository newInstance(ApiService apiService) {
    return new ContractorRepository(apiService);
  }
}
