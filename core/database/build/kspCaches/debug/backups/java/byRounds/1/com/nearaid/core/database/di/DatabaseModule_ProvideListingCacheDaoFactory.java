package com.nearaid.core.database.di;

import com.nearaid.core.database.NearAidDatabase;
import com.nearaid.core.database.dao.ListingCacheDao;
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
public final class DatabaseModule_ProvideListingCacheDaoFactory implements Factory<ListingCacheDao> {
  private final Provider<NearAidDatabase> dbProvider;

  public DatabaseModule_ProvideListingCacheDaoFactory(Provider<NearAidDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ListingCacheDao get() {
    return provideListingCacheDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideListingCacheDaoFactory create(
      Provider<NearAidDatabase> dbProvider) {
    return new DatabaseModule_ProvideListingCacheDaoFactory(dbProvider);
  }

  public static ListingCacheDao provideListingCacheDao(NearAidDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideListingCacheDao(db));
  }
}
