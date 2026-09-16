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
public final class ContractorDetailViewModel_Factory implements Factory<ContractorDetailViewModel> {
  private final Provider<ContractorRepository> contractorRepositoryProvider;

  public ContractorDetailViewModel_Factory(
      Provider<ContractorRepository> contractorRepositoryProvider) {
    this.contractorRepositoryProvider = contractorRepositoryProvider;
  }

  @Override
  public ContractorDetailViewModel get() {
    return newInstance(contractorRepositoryProvider.get());
  }

  public static ContractorDetailViewModel_Factory create(
      Provider<ContractorRepository> contractorRepositoryProvider) {
    return new ContractorDetailViewModel_Factory(contractorRepositoryProvider);
  }

  public static ContractorDetailViewModel newInstance(ContractorRepository contractorRepository) {
    return new ContractorDetailViewModel(contractorRepository);
  }
}
