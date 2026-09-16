package com.gotchureviews.app.data.local;

import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.serialization.json.Json;

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
public final class TokenStore_Factory implements Factory<TokenStore> {
  private final Provider<DataStore<Preferences>> dataStoreProvider;

  private final Provider<Json> jsonProvider;

  public TokenStore_Factory(Provider<DataStore<Preferences>> dataStoreProvider,
      Provider<Json> jsonProvider) {
    this.dataStoreProvider = dataStoreProvider;
    this.jsonProvider = jsonProvider;
  }

  @Override
  public TokenStore get() {
    return newInstance(dataStoreProvider.get(), jsonProvider.get());
  }

  public static TokenStore_Factory create(Provider<DataStore<Preferences>> dataStoreProvider,
      Provider<Json> jsonProvider) {
    return new TokenStore_Factory(dataStoreProvider, jsonProvider);
  }

  public static TokenStore newInstance(DataStore<Preferences> dataStore, Json json) {
    return new TokenStore(dataStore, json);
  }
}
