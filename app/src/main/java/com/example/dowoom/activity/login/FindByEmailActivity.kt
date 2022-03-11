package com.example.dowoom.activity.login

import android.os.Bundle
import androidx.activity.viewModels
import com.example.dowoom.R
import com.example.dowoom.activity.BaseActivity
import com.example.dowoom.databinding.ActivityFindByEmailBinding
import com.example.dowoom.viewmodel.registervm.FindByEmailViewmodel

class FindByEmailActivity : BaseActivity<ActivityFindByEmailBinding>(TAG = "FindByEmailActivity", R.layout.activity_find_by_email) {

    val viewModel: FindByEmailViewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}