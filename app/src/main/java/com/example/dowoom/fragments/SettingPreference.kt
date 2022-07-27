package com.example.dowoom.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.dowoom.R


//참고 : https://blog.naver.com/PostView.nhn?isHttpsRedirect=true&blogId=pointer98&logNo=221990036609&categoryNo=0&parentCategoryNo=0&viewDate=&currentPage=1&postListTopCurrentPage=1&from=postView
class SettingPreference : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey)
    }
}