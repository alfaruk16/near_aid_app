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
public final class SetSearchRadiusUseCase_Factory implements Factory<SetSearchRadiusUseCase> {
  private final Provider<PreferencesRepository> repositoryProvider;

  public SetSearchRadiusUseCase_Factory(Provider<PreferencesRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public SetSearchRadiusUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static SetSearchRadiusUseCase_Factory create(
      Provider<PreferencesRepository> repositoryProvider) {
    return new SetSearchRadiusUseCase_Factory(repositoryProvider);
  }

  public static SetSearchRadiusUseCase newInstance(PreferencesRepository repository) {
    return new SetSearchRadiusUseCase(repository);
  }
}
