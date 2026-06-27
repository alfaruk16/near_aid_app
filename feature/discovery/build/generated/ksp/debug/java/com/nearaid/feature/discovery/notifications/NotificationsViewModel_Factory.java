package com.nearaid.feature.discovery.notifications;

import com.nearaid.core.domain.usecase.GetNotificationsUseCase;
import com.nearaid.core.domain.usecase.MarkNotificationsReadUseCase;
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
public final class NotificationsViewModel_Factory implements Factory<NotificationsViewModel> {
  private final Provider<GetNotificationsUseCase> getNotificationsProvider;

  private final Provider<MarkNotificationsReadUseCase> markNotificationsReadProvider;

  public NotificationsViewModel_Factory(Provider<GetNotificationsUseCase> getNotificationsProvider,
      Provider<MarkNotificationsReadUseCase> markNotificationsReadProvider) {
    this.getNotificationsProvider = getNotificationsProvider;
    this.markNotificationsReadProvider = markNotificationsReadProvider;
  }

  @Override
  public NotificationsViewModel get() {
    return newInstance(getNotificationsProvider.get(), markNotificationsReadProvider.get());
  }

  public static NotificationsViewModel_Factory create(
      Provider<GetNotificationsUseCase> getNotificationsProvider,
      Provider<MarkNotificationsReadUseCase> markNotificationsReadProvider) {
    return new NotificationsViewModel_Factory(getNotificationsProvider, markNotificationsReadProvider);
  }

  public static NotificationsViewModel newInstance(GetNotificationsUseCase getNotifications,
      MarkNotificationsReadUseCase markNotificationsRead) {
    return new NotificationsViewModel(getNotifications, markNotificationsRead);
  }
}
