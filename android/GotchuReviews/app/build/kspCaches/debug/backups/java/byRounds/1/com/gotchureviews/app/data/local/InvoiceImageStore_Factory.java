package com.gotchureviews.app.data.local;

import android.content.Context;
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
public final class InvoiceImageStore_Factory implements Factory<InvoiceImageStore> {
  private final Provider<Context> contextProvider;

  public InvoiceImageStore_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public InvoiceImageStore get() {
    return newInstance(contextProvider.get());
  }

  public static InvoiceImageStore_Factory create(Provider<Context> contextProvider) {
    return new InvoiceImageStore_Factory(contextProvider);
  }

  public static InvoiceImageStore newInstance(Context context) {
    return new InvoiceImageStore(context);
  }
}
