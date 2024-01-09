package com.example.ecooker.di

import android.content.Context
import com.example.ecooker.repository.CommentRepository
import com.example.ecooker.repository.RecipeRepository
import com.example.ecooker.repository.TokenRepository
import com.example.ecooker.repository.UserAuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRecipeRepository(): RecipeRepository = RecipeRepository()

    @Provides
    @Singleton
    fun provideUserAuthRepository(): UserAuthRepository = UserAuthRepository()

    @Provides
    @Singleton
    fun provideTokenRepository(@ApplicationContext context: Context): TokenRepository = TokenRepository(context)

    @Provides
    @Singleton
    fun provideCommentRepository(): CommentRepository = CommentRepository()
}