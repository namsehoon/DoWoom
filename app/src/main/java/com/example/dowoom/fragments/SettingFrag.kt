package com.example.dowoom.fragments

import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.dowoom.R
import com.example.dowoom.activity.login.LoadingActivity
import com.example.dowoom.activity.login.StartActivity
import com.example.dowoom.viewmodel.mainViewmodel.SettingViewModel
import com.example.dowoom.databinding.SettingFragmentBinding
import com.example.dowoom.firebase.Ref

class SettingFrag : PreferenceFragmentCompat() {

    companion object {
        fun newInstance() = SettingFrag()
    }

    lateinit var prefs:SharedPreferences

    var keyLogout: Preference? = null

    private lateinit var viewModel: SettingViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return super.onCreateView(inflater, container, savedInstanceState)
    }


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        //xml inflate
        setPreferencesFromResource(R.xml.settings_preference, rootKey)
        prefs = PreferenceManager.getDefaultSharedPreferences(context!!)
        prefs.edit().putString("logOut","아니오").apply()
        prefs.registerOnSharedPreferenceChangeListener(prefListener)


    }

    val prefListener = SharedPreferences.OnSharedPreferenceChangeListener{ sharedPreferences, key ->
        Log.d("Abcd","key is : $key")
        when(key) {
            "logOut" -> {
                val result = prefs.getString("logOut","")
                //로그아웃
                if (result.equals("네")) {
                    Ref().firebaseAuth.signOut()
                    val intent = Intent(context,LoadingActivity::class.java).apply { Intent.FLAG_ACTIVITY_CLEAR_TOP }
                    startActivity(intent)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("abcd","SETTING FRAGMENT")
        prefs.registerOnSharedPreferenceChangeListener(prefListener)
    }

    override fun onPause() {
        super.onPause()
        prefs.unregisterOnSharedPreferenceChangeListener(prefListener)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SettingViewModel::class.java)
    }

}