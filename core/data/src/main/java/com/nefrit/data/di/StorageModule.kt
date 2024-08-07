package com.nefrit.data.di

import android.content.Context
import com.nefrit.data.storage.Preferences
import com.nefrit.common.di.ApplicationScope
import com.nefrit.data.storage.PreferencesImpl
import dagger.Module
import dagger.Provides

@Module
class StorageModule {

    @Provides
    @ApplicationScope
    fun providePreferences(context: Context): Preferences {
        return PreferencesImpl(context)
    }
}