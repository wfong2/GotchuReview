package com.gotchureviews.app.data.repository;

import com.gotchureviews.app.data.local.TokenStore;
import com.gotchureviews.app.data.remote.ApiService;
import com.gotchureviews.app.data.remote.FirebaseAuthApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("javax.inject.Named")
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
public final class AuthRepository_Factory implements Factory<AuthRepository> {
  private final Provider<ApiService> apiServiceProvider;

  private final Provider<FirebaseAuthApi> firebaseAuthApiProvider;

  private final Provider<FirebaseAuthApi> firebaseTokenApiProvider;

  private final Provider<TokenStore> tokenStoreProvider;

  public AuthRepository_Factory(Provider<ApiService> apiServiceProvider,
      Provider<FirebaseAuthApi> firebaseAuthApiProvider,
      Provider<FirebaseAuthApi> firebaseTokenApiProvider, Provider<TokenStore> tokenStoreProvider) {
    this.apiServiceProvider = apiServiceProvider;
    this.firebaseAuthApiProvider = firebaseAuthApiProvider;
    this.firebaseTokenApiProvider = firebaseTokenApiProvider;
    this.tokenStoreProvider = tokenStoreProvider;
  }

  @Override
  public AuthRepository get() {
    return newInstance(apiServiceProvider.get(), firebaseAuthApiProvider.get(), firebaseTokenApiProvider.get(), tokenStoreProvider.get());
  }

  public static AuthRepository_Factory create(Provider<ApiService> apiServiceProvider,
      Provider<FirebaseAuthApi> firebaseAuthApiProvider,
      Provider<FirebaseAuthApi> firebaseTokenApiProvider, Provider<TokenStore> tokenStoreProvider) {
    return new AuthRepository_Factory(apiServiceProvider, firebaseAuthApiProvider, firebaseTokenApiProvider, tokenStoreProvider);
  }

  public static AuthRepository newInstance(ApiService apiService, FirebaseAuthApi firebaseAuthApi,
      FirebaseAuthApi firebaseTokenApi, TokenStore tokenStore) {
    return new AuthRepository(apiService, firebaseAuthApi, firebaseTokenApi, tokenStore);
  }
}
