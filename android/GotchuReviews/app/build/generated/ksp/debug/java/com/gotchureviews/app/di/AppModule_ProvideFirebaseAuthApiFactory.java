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
public final class AppModule_ProvideFirebaseAuthApiFactory implements Factory<FirebaseAuthApi> {
  private final Provider<Json> jsonProvider;

  public AppModule_ProvideFirebaseAuthApiFactory(Provider<Json> jsonProvider) {
    this.jsonProvider = jsonProvider;
  }

  @Override
  public FirebaseAuthApi get() {
    return provideFirebaseAuthApi(jsonProvider.get());
  }

  public static AppModule_ProvideFirebaseAuthApiFactory create(Provider<Json> jsonProvider) {
    return new AppModule_ProvideFirebaseAuthApiFactory(jsonProvider);
  }

  public static FirebaseAuthApi provideFirebaseAuthApi(Json json) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideFirebaseAuthApi(json));
  }
}
