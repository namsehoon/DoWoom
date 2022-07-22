package com.example.dowoom.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.Menu
import com.example.dowoom.R
import com.example.dowoom.viewmodel.mainViewmodel.SettingViewModel
import com.example.dowoom.databinding.SettingFragmentBinding

class SettingFrag : BaseFragment<SettingFragmentBinding>("SettingFrag", R.layout.setting_fragment) {

    companion object {
        fun newInstance() = SettingFrag()
    }

    private lateinit var viewModel: SettingViewModel




    override fun onPrepareOptionsMenu(menu: Menu) {
        //툴바 filter item 보이게 하기
        menu.findItem(R.id.settingItem).isVisible = true
        super.onPrepareOptionsMenu(menu)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SettingViewModel::class.java)
    }

}