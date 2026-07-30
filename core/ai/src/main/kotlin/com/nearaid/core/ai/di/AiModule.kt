package com.nearaid.core.ai.di

import com.nearaid.core.ai.CompositeTextEmbedder
import com.nearaid.core.domain.ai.TextEmbedder
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiModule {
    @Binds
    @Singleton
    abstract fun bindTextEmbedder(impl: CompositeTextEmbedder): TextEmbedder
}
