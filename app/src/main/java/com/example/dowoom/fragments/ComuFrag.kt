package com.example.dowoom.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import com.example.dowoom.R
import com.example.dowoom.databinding.ComuFragmentBinding
import com.example.dowoom.fragments.childFragments.ComuGuest
import com.example.dowoom.fragments.childFragments.ComuHumor
import com.example.dowoom.fragments.childFragments.ComuBest
import com.example.dowoom.viewmodel.mainViewmodel.ComuViewModel

class ComuFrag : BaseFragment<ComuFragmentBinding>(TAG = "ComeFrag", R.layout.comu_fragment), View.OnClickListener {


    companion object {
        fun newInstance() = ComuFrag()
    }

    val viewModel by viewModels<ComuViewModel>()

    //child fragment
    var humor = ComuHumor()
    var best = ComuBest()
    var guest = ComuGuest()

    //관찰
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = requireActivity()
    }

    //버튼 리스너, recyclerview 어뎁터
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)  {
        super.onViewCreated(view, savedInstanceState)

        binding.tvHumor.setOnClickListener(this@ComuFrag)
        binding.tvBest.setOnClickListener(this@ComuFrag)
        binding.tvGuest.setOnClickListener(this@ComuFrag)
    }

    //자식 프로그먼트로 교체


    override fun onClick(v: View?) {
        val childManager = childFragmentManager.beginTransaction() //트랜잭션 객체 생성
        when(v?.id) {
            R.id.tvHumor -> {
                childManager
                    .replace(R.id.childFragment, humor)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
            R.id.tvBest -> {
                childManager
                    .replace(R.id.childFragment, best)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
            R.id.tvGuest -> {
                childManager
                    .replace(R.id.childFragment, guest)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }

        }
    }


}