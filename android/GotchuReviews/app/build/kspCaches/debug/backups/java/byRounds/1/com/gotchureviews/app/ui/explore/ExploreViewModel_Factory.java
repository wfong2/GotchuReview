package com.gotchureviews.app.ui.explore;

import com.gotchureviews.app.data.repository.ContractorRepository;
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
public final class ExploreViewModel_Factory implements Factory<ExploreViewModel> {
  private final Provider<ContractorRepository> contractorRepositoryProvider;

  public ExploreViewModel_Factory(Provider<ContractorRepository> contractorRepositoryProvider) {
    this.contractorRepositoryProvider = contractorRepositoryProvider;
  }

  @Override
  public ExploreViewModel get() {
    return newInstance(contractorRepositoryProvider.get());
  }

  public static ExploreViewModel_Factory create(
      Provider<ContractorRepository> contractorRepositoryProvider) {
    return new ExploreViewModel_Factory(contractorRepositoryProvider);
  }

  public static ExploreViewModel newInstance(ContractorRepository contractorRepository) {
    return new ExploreViewModel(contractorRepository);
  }
}
