package com.nearaid.core.domain.usecase;

import com.nearaid.core.domain.repository.CategoryRepository;
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
public final class ObserveCategoriesUseCase_Factory implements Factory<ObserveCategoriesUseCase> {
  private final Provider<CategoryRepository> repositoryProvider;

  public ObserveCategoriesUseCase_Factory(Provider<CategoryRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ObserveCategoriesUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ObserveCategoriesUseCase_Factory create(
      Provider<CategoryRepository> repositoryProvider) {
    return new ObserveCategoriesUseCase_Factory(repositoryProvider);
  }

  public static ObserveCategoriesUseCase newInstance(CategoryRepository repository) {
    return new ObserveCategoriesUseCase(repository);
  }
}
