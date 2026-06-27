package com.nearaid.core.database.di;

import com.nearaid.core.database.NearAidDatabase;
import com.nearaid.core.database.dao.ConversationCacheDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideConversationCacheDaoFactory implements Factory<ConversationCacheDao> {
  private final Provider<NearAidDatabase> dbProvider;

  public DatabaseModule_ProvideConversationCacheDaoFactory(Provider<NearAidDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ConversationCacheDao get() {
    return provideConversationCacheDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideConversationCacheDaoFactory create(
      Provider<NearAidDatabase> dbProvider) {
    return new DatabaseModule_ProvideConversationCacheDaoFactory(dbProvider);
  }

  public static ConversationCacheDao provideConversationCacheDao(NearAidDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideConversationCacheDao(db));
  }
}
