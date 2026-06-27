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
public final class RefreshCategoriesUseCase_Factory implements Factory<RefreshCategoriesUseCase> {
  private final Provider<CategoryRepository> repositoryProvider;

  public RefreshCategoriesUseCase_Factory(Provider<CategoryRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public RefreshCategoriesUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static RefreshCategoriesUseCase_Factory create(
      Provider<CategoryRepository> repositoryProvider) {
    return new RefreshCategoriesUseCase_Factory(repositoryProvider);
  }

  public static RefreshCategoriesUseCase newInstance(CategoryRepository repository) {
    return new RefreshCategoriesUseCase(repository);
  }
}
