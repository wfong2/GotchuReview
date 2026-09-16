package com.gotchureviews.app.data.repository;

import android.content.Context;
import com.gotchureviews.app.data.local.InvoiceImageStore;
import com.gotchureviews.app.data.remote.ApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class InvoiceRepository_Factory implements Factory<InvoiceRepository> {
  private final Provider<ApiService> apiServiceProvider;

  private final Provider<InvoiceImageStore> imageStoreProvider;

  private final Provider<Context> contextProvider;

  public InvoiceRepository_Factory(Provider<ApiService> apiServiceProvider,
      Provider<InvoiceImageStore> imageStoreProvider, Provider<Context> contextProvider) {
    this.apiServiceProvider = apiServiceProvider;
    this.imageStoreProvider = imageStoreProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public InvoiceRepository get() {
    return newInstance(apiServiceProvider.get(), imageStoreProvider.get(), contextProvider.get());
  }

  public static InvoiceRepository_Factory create(Provider<ApiService> apiServiceProvider,
      Provider<InvoiceImageStore> imageStoreProvider, Provider<Context> contextProvider) {
    return new InvoiceRepository_Factory(apiServiceProvider, imageStoreProvider, contextProvider);
  }

  public static InvoiceRepository newInstance(ApiService apiService, InvoiceImageStore imageStore,
      Context context) {
    return new InvoiceRepository(apiService, imageStore, context);
  }
}
