package com.example.jawwna.datasource.repository

import com.example.jawwna.datasource.localdatasoource.ILocalDataSource
import com.example.jawwna.datasource.localdatasoource.shared_preferences_helper.location.IPreferencesLocationHelper
import com.example.jawwna.datasource.localdatasoource.shared_preferences_helper.settings.IPreferencesSettingsHelper
import com.example.jawwna.datasource.remotedatasource.IRemoteDataSource
import com.example.jawwna.helper.IUpdateLocale
import com.example.jawwna.helper.PreferencesLocationEum
import com.example.jawwna.helper.broadcastreceiver.INetworkManager
import com.example.jawwna.mapscreen.geocodingservice.IGeocodingService

interface IRepository: IRemoteDataSource, ILocalDataSource ,IPreferencesSettingsHelper,IPreferencesLocationHelper,IGeocodingService,INetworkManager{
    fun execute(preferencesLocationEum: PreferencesLocationEum)
    fun getLanguageCode() : String
    }