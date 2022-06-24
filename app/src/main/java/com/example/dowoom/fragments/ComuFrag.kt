package com.example.dowoom.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dowoom.R
import com.example.dowoom.adapter.ComuAdapter
import com.example.dowoom.databinding.ComuFragmentBinding
import com.example.dowoom.factory.ComuViewModelFactory
import com.example.dowoom.fragments.childFragments.ComuGuest
import com.example.dowoom.fragments.childFragments.ComuHumor
import com.example.dowoom.fragments.childFragments.ComuBest
import com.example.dowoom.retrofit.GezipRepo
import com.example.dowoom.viewmodel.mainViewmodel.ComuViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.lang.Exception

class ComuFrag : BaseFragment<ComuFragmentBinding>(TAG = "ComeFrag", R.layout.comu_fragment), View.OnClickListener {


    companion object {
        fun newInstance() = ComuFrag()
    }
    private val repository = GezipRepo()
    private val viewModel:ComuViewModel by viewModels { ComuViewModelFactory(repository)}
    //adapter
    private lateinit var adapter: ComuAdapter

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


        adapter = ComuAdapter(requireActivity())

        binding.rvComuList.layoutManager = LinearLayoutManager(this.context)
        binding.rvComuList.adapter = adapter

        binding.tvHumor.setOnClickListener(this@ComuFrag)
        binding.tvBest.setOnClickListener(this@ComuFrag)
        binding.tvGuest.setOnClickListener(this@ComuFrag)

        val ge = GezipRepo().loadGezipNotice(1,this@ComuFrag)

    }

    //자식 프로그먼트로 교체
//    childManager
//    .replace(R.id.childFragment, humor)
//    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//    .commit()
    override fun onClick(v: View?) {
        val childManager = childFragmentManager.beginTransaction() //트랜잭션 객체 생성
        when(v?.id) {
            R.id.tvHumor -> {

            }
            R.id.tvBest -> {

            }
            R.id.tvGuest -> {

            }

        }
    }


}