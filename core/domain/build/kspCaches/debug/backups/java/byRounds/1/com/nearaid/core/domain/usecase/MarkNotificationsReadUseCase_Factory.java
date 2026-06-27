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
public final class MarkNotificationsReadUseCase_Factory implements Factory<MarkNotificationsReadUseCase> {
  private final Provider<NotificationRepository> repositoryProvider;

  public MarkNotificationsReadUseCase_Factory(Provider<NotificationRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public MarkNotificationsReadUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static MarkNotificationsReadUseCase_Factory create(
      Provider<NotificationRepository> repositoryProvider) {
    return new MarkNotificationsReadUseCase_Factory(repositoryProvider);
  }

  public static MarkNotificationsReadUseCase newInstance(NotificationRepository repository) {
    return new MarkNotificationsReadUseCase(repository);
  }
}
