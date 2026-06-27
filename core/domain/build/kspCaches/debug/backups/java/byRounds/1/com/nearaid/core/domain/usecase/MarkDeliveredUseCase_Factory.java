package com.nearaid.core.domain.usecase;

import com.nearaid.core.domain.repository.ClaimRepository;
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
    "deprecation"
})
public final class MarkDeliveredUseCase_Factory implements Factory<MarkDeliveredUseCase> {
  private final Provider<ClaimRepository> repositoryProvider;

  public MarkDeliveredUseCase_Factory(Provider<ClaimRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public MarkDeliveredUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static MarkDeliveredUseCase_Factory create(Provider<ClaimRepository> repositoryProvider) {
    return new MarkDeliveredUseCase_Factory(repositoryProvider);
  }

  public static MarkDeliveredUseCase newInstance(ClaimRepository repository) {
    return new MarkDeliveredUseCase(repository);
  }
}
