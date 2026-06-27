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
public final class SetLanguageUseCase_Factory implements Factory<SetLanguageUseCase> {
  private final Provider<PreferencesRepository> repositoryProvider;

  public SetLanguageUseCase_Factory(Provider<PreferencesRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public SetLanguageUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static SetLanguageUseCase_Factory create(
      Provider<PreferencesRepository> repositoryProvider) {
    return new SetLanguageUseCase_Factory(repositoryProvider);
  }

  public static SetLanguageUseCase newInstance(PreferencesRepository repository) {
    return new SetLanguageUseCase(repository);
  }
}
