package com.example.dowoom.dataStore

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

class DataStore(context: Context) {

    private val Context.datastore:androidx.datastore.core.DataStore<Preferences> by preferencesDataStore(name = "datastore")

    private val mDataStore: androidx.datastore.core.DataStore<Preferences> = context.datastore


    suspend fun storeData(key:String, value:String) {

        mDataStore.edit { preference ->
            preference[stringPreferencesKey(key)] = value
        }

        Log.d("abcd","datastore is : "+mDataStore.data.first().toString())

    }



    suspend fun readData(key:String) : String {

        val preferences = mDataStore.data.first()


        return preferences[stringPreferencesKey(key)]!!
    }

}


//동행 객체 : 객체다. static(클래스 맴버 : 인스턴스 없이 .을 찍어 참조). 근데, static임. class에 붙어있는 개체이기 때문에 한 인스턴스만 존재한다.
//companion object
class DataStoreST private constructor() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var dataStore:DataStore? = null

        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context

       fun getInstance(_context:Context) : DataStore {
           return dataStore ?: synchronized(this) {
               dataStore ?: DataStore(_context).also {
                   context = _context
                   dataStore = it
               }
           }
       }
    }
}

