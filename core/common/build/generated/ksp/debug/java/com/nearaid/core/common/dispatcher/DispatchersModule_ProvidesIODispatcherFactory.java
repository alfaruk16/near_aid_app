package com.nearaid.core.common.dispatcher;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import kotlinx.coroutines.CoroutineDispatcher;

@ScopeMetadata
@QualifierMetadata("com.nearaid.core.common.dispatcher.Dispatcher")
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
public final class DispatchersModule_ProvidesIODispatcherFactory implements Factory<CoroutineDispatcher> {
  @Override
  public CoroutineDispatcher get() {
    return providesIODispatcher();
  }

  public static DispatchersModule_ProvidesIODispatcherFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CoroutineDispatcher providesIODispatcher() {
    return Preconditions.checkNotNullFromProvides(DispatchersModule.INSTANCE.providesIODispatcher());
  }

  private static final class InstanceHolder {
    private static final DispatchersModule_ProvidesIODispatcherFactory INSTANCE = new DispatchersModule_ProvidesIODispatcherFactory();
  }
}
