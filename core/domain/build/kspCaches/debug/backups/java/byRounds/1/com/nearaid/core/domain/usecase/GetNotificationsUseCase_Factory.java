package com.nearaid.core.domain.usecase;

import com.nearaid.core.domain.repository.NotificationRepository;
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
public final class GetNotificationsUseCase_Factory implements Factory<GetNotificationsUseCase> {
  private final Provider<NotificationRepository> repositoryProvider;

  public GetNotificationsUseCase_Factory(Provider<NotificationRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetNotificationsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetNotificationsUseCase_Factory create(
      Provider<NotificationRepository> repositoryProvider) {
    return new GetNotificationsUseCase_Factory(repositoryProvider);
  }

  public static GetNotificationsUseCase newInstance(NotificationRepository repository) {
    return new GetNotificationsUseCase(repository);
  }
}
