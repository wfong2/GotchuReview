package com.gotchureviews.app.ui.onboarding;

import com.gotchureviews.app.data.local.TokenStore;
import com.gotchureviews.app.data.repository.AuthRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class OnboardingViewModel_Factory implements Factory<OnboardingViewModel> {
  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<TokenStore> tokenStoreProvider;

  public OnboardingViewModel_Factory(Provider<AuthRepository> authRepositoryProvider,
      Provider<TokenStore> tokenStoreProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
    this.tokenStoreProvider = tokenStoreProvider;
  }

  @Override
  public OnboardingViewModel get() {
    return newInstance(authRepositoryProvider.get(), tokenStoreProvider.get());
  }

  public static OnboardingViewModel_Factory create(Provider<AuthRepository> authRepositoryProvider,
      Provider<TokenStore> tokenStoreProvider) {
    return new OnboardingViewModel_Factory(authRepositoryProvider, tokenStoreProvider);
  }

  public static OnboardingViewModel newInstance(AuthRepository authRepository,
      TokenStore tokenStore) {
    return new OnboardingViewModel(authRepository, tokenStore);
  }
}
