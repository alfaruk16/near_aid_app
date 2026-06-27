package com.nearaid.core.domain.usecase;

import com.nearaid.core.domain.repository.PreferencesRepository;
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
public final class ObserveLanguageUseCase_Factory implements Factory<ObserveLanguageUseCase> {
  private final Provider<PreferencesRepository> repositoryProvider;

  public ObserveLanguageUseCase_Factory(Provider<PreferencesRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ObserveLanguageUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ObserveLanguageUseCase_Factory create(
      Provider<PreferencesRepository> repositoryProvider) {
    return new ObserveLanguageUseCase_Factory(repositoryProvider);
  }

  public static ObserveLanguageUseCase newInstance(PreferencesRepository repository) {
    return new ObserveLanguageUseCase(repository);
  }
}
