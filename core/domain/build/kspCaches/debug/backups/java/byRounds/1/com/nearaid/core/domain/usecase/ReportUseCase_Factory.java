package com.nearaid.core.domain.usecase;

import com.nearaid.core.domain.repository.SafetyRepository;
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
public final class ReportUseCase_Factory implements Factory<ReportUseCase> {
  private final Provider<SafetyRepository> repositoryProvider;

  public ReportUseCase_Factory(Provider<SafetyRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ReportUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ReportUseCase_Factory create(Provider<SafetyRepository> repositoryProvider) {
    return new ReportUseCase_Factory(repositoryProvider);
  }

  public static ReportUseCase newInstance(SafetyRepository repository) {
    return new ReportUseCase(repository);
  }
}
