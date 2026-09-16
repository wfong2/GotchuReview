package com.gotchureviews.app.ui.scan;

import com.gotchureviews.app.data.repository.InvoiceRepository;
import com.gotchureviews.app.data.repository.ReviewRepository;
import com.gotchureviews.app.data.repository.UserRepository;
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
public final class ScanViewModel_Factory implements Factory<ScanViewModel> {
  private final Provider<InvoiceRepository> invoiceRepositoryProvider;

  private final Provider<ReviewRepository> reviewRepositoryProvider;

  private final Provider<UserRepository> userRepositoryProvider;

  public ScanViewModel_Factory(Provider<InvoiceRepository> invoiceRepositoryProvider,
      Provider<ReviewRepository> reviewRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider) {
    this.invoiceRepositoryProvider = invoiceRepositoryProvider;
    this.reviewRepositoryProvider = reviewRepositoryProvider;
    this.userRepositoryProvider = userRepositoryProvider;
  }

  @Override
  public ScanViewModel get() {
    return newInstance(invoiceRepositoryProvider.get(), reviewRepositoryProvider.get(), userRepositoryProvider.get());
  }

  public static ScanViewModel_Factory create(Provider<InvoiceRepository> invoiceRepositoryProvider,
      Provider<ReviewRepository> reviewRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider) {
    return new ScanViewModel_Factory(invoiceRepositoryProvider, reviewRepositoryProvider, userRepositoryProvider);
  }

  public static ScanViewModel newInstance(InvoiceRepository invoiceRepository,
      ReviewRepository reviewRepository, UserRepository userRepository) {
    return new ScanViewModel(invoiceRepository, reviewRepository, userRepository);
  }
}
