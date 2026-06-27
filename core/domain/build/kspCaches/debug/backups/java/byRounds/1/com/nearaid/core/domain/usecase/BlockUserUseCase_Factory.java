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
public final class BlockUserUseCase_Factory implements Factory<BlockUserUseCase> {
  private final Provider<SafetyRepository> repositoryProvider;

  public BlockUserUseCase_Factory(Provider<SafetyRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public BlockUserUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static BlockUserUseCase_Factory create(Provider<SafetyRepository> repositoryProvider) {
    return new BlockUserUseCase_Factory(repositoryProvider);
  }

  public static BlockUserUseCase newInstance(SafetyRepository repository) {
    return new BlockUserUseCase(repository);
  }
}
