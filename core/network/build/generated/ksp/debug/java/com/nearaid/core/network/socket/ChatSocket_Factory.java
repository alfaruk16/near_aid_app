package com.nearaid.core.network.socket;

import com.nearaid.core.datastore.AuthPreferencesDataSource;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.serialization.json.Json;
import okhttp3.OkHttpClient;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("com.nearaid.core.network.di.WsUrl")
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
public final class ChatSocket_Factory implements Factory<ChatSocket> {
  private final Provider<OkHttpClient> clientProvider;

  private final Provider<Json> jsonProvider;

  private final Provider<AuthPreferencesDataSource> authPrefsProvider;

  private final Provider<String> wsUrlProvider;

  public ChatSocket_Factory(Provider<OkHttpClient> clientProvider, Provider<Json> jsonProvider,
      Provider<AuthPreferencesDataSource> authPrefsProvider, Provider<String> wsUrlProvider) {
    this.clientProvider = clientProvider;
    this.jsonProvider = jsonProvider;
    this.authPrefsProvider = authPrefsProvider;
    this.wsUrlProvider = wsUrlProvider;
  }

  @Override
  public ChatSocket get() {
    return newInstance(clientProvider.get(), jsonProvider.get(), authPrefsProvider.get(), wsUrlProvider.get());
  }

  public static ChatSocket_Factory create(Provider<OkHttpClient> clientProvider,
      Provider<Json> jsonProvider, Provider<AuthPreferencesDataSource> authPrefsProvider,
      Provider<String> wsUrlProvider) {
    return new ChatSocket_Factory(clientProvider, jsonProvider, authPrefsProvider, wsUrlProvider);
  }

  public static ChatSocket newInstance(OkHttpClient client, Json json,
      AuthPreferencesDataSource authPrefs, String wsUrl) {
    return new ChatSocket(client, json, authPrefs, wsUrl);
  }
}
