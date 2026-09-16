package com.gotchureviews.app.di;

import com.gotchureviews.app.data.remote.FirebaseAuthApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.serialization.json.Json;

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
public final class AppModule_ProvideFirebaseTokenApiFactory implements Factory<FirebaseAuthApi> {
  private final Provider<Json> jsonProvider;

  public AppModule_ProvideFirebaseTokenApiFactory(Provider<Json> jsonProvider) {
    this.jsonProvider = jsonProvider;
  }

  @Override
  public FirebaseAuthApi get() {
    return provideFirebaseTokenApi(jsonProvider.get());
  }

  public static AppModule_ProvideFirebaseTokenApiFactory create(Provider<Json> jsonProvider) {
    return new AppModule_ProvideFirebaseTokenApiFactory(jsonProvider);
  }

  public static FirebaseAuthApi provideFirebaseTokenApi(Json json) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideFirebaseTokenApi(json));
  }
}
