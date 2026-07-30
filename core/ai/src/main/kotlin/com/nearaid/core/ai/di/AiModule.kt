package com.nearaid.core.ai.di

import android.content.Context
import com.nearaid.core.ai.CompositeTextEmbedder
import com.nearaid.core.ai.EmbeddingSessionFactory
import com.nearaid.core.ai.HashingTextEmbedder
import com.nearaid.core.ai.MediaPipeTextEmbedder
import com.nearaid.core.domain.ai.TextEmbedder
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiModule {

    @Binds
    @Semantic
    abstract fun bindSemanticEmbedder(impl: MediaPipeTextEmbedder): TextEmbedder

    @Binds
    @Lexical
    abstract fun bindLexicalEmbedder(impl: HashingTextEmbedder): TextEmbedder

    /** The app-facing embedder: semantic when available, lexical otherwise. */
    @Binds
    @Singleton
    abstract fun bindTextEmbedder(impl: CompositeTextEmbedder): TextEmbedder

    companion object {
        @Provides
        @Singleton
        fun provideEmbeddingSessionFactory(
            @ApplicationContext context: Context,
        ): EmbeddingSessionFactory = mediaPipeSessionFactory(context)
    }
}
