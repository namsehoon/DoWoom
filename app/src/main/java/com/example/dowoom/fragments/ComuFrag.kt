package com.example.dowoom.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dowoom.R
import com.example.dowoom.activity.comu.GuestWriteActivity
import com.example.dowoom.adapter.ComuAdapter
import com.example.dowoom.databinding.ComuFragmentBinding
import com.example.dowoom.factory.ComuViewModelFactory
import com.example.dowoom.fragments.childFragments.ComuGuest
import com.example.dowoom.fragments.childFragments.ComuHumor
import com.example.dowoom.fragments.childFragments.ComuBest
import com.example.dowoom.retrofit.GezipRepo
import com.example.dowoom.viewmodel.mainViewmodel.ComuViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

class ComuFrag : BaseFragment<ComuFragmentBinding>(TAG = "ComeFrag", R.layout.comu_fragment), View.OnClickListener {


    companion object {
        fun newInstance() = ComuFrag()
    }
    private val repository = GezipRepo()
    @ExperimentalCoroutinesApi
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


        observerData()
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

        //글작성
        binding.tvToWrite.setOnClickListener(this@ComuFrag)

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
                //글작성
                binding.llToWrite.visibility = View.GONE
                //todo : 데이터 불러오기 1, 2, 3, 4, 5
                viewModel.comuList.observe(viewLifecycleOwner, Observer { it ->
                    adapter.comuList.clear()
                    adapter.setContents(it)

                })
                binding.rvComuList.layoutManager = LinearLayoutManager(this.context)
                binding.rvComuList.adapter = adapter
            }
            R.id.tvBest -> {


            }
            R.id.tvGuest -> { //게스트
                //글작성
                binding.llToWrite.visibility = View.VISIBLE
                viewModel.guestList.observe(viewLifecycleOwner, Observer { it ->
                    adapter.comuList.clear()
                    adapter.setContents(it)
                })
                binding.rvComuList.layoutManager = LinearLayoutManager(this.context)
                binding.rvComuList.adapter = adapter
            }

            //글작성하러가기
            R.id.tvToWrite -> {
                val intent = Intent(context, GuestWriteActivity::class.java)
                context?.startActivity(intent)
            }

        }
    }

    private fun observerData() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.comuList.observe(viewLifecycleOwner, Observer { it ->
                adapter.setContents(it)

            })
        }
    }


}