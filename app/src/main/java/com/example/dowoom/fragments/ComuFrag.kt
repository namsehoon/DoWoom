package com.example.dowoom.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentTransaction
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

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


        adapter = ComuAdapter(requireActivity(), contentClicked = { comuModel, position ->
            //자식 프로그먼트로 교체
            Log.d("Abcd","부모에서 ComuAdapter 클릭됨")

            binding.llComuList.visibility = View.GONE // recyclerview 숨기기
            binding.childFragment.visibility = View.VISIBLE // child fragment 보이게
            binding.llToWrite.visibility = View.GONE //글작성

            //child fragment에 데이터 보내기

            val childManager = childFragmentManager.beginTransaction()
            if (comuModel.kindOf == 1) { // 유머게시판
                Log.d("abcd", "comufrag - adapter - humor : 유머게시판으로 ")

                //데이터 전송
                humor.comuModelFromParent = comuModel
                humor.position = position

                //commit
                childManager
                    .replace(R.id.childFragment, humor)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()

            } else { // 익명 게시판
                guest.comuModelFromParent = comuModel
                guest.position = position
                Log.d("abcd","comumodel content text is : ${comuModel.contentText}")
                guest.contentTextFromParent = comuModel.contentText
                Log.d("abcd", "comufrag - adapter - guest : 익명게시판으로 ")

                 //트랜잭션 객체 생성
                childManager
                    .replace(R.id.childFragment, guest)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }



        })

        binding.rvComuList.layoutManager = LinearLayoutManager(this.context)
        binding.rvComuList.adapter = adapter


        binding.tvHumor.setOnClickListener(this@ComuFrag)
        binding.tvBest.setOnClickListener(this@ComuFrag)
        binding.tvGuest.setOnClickListener(this@ComuFrag)

        //글작성
        binding.tvToWrite.setOnClickListener(this@ComuFrag)



    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.tvHumor -> {
                binding.llToWrite.visibility = View.GONE //글작성 버튼 숨기기
                binding.childFragment.visibility = View.GONE //child frag 숨기기
                binding.llComuList.visibility = View.VISIBLE
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
                binding.childFragment.visibility = View.GONE
                binding.llComuList.visibility = View.VISIBLE

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

            viewModel.progress.observe(viewLifecycleOwner, Observer { result ->
                binding.refresh.isRefreshing = result
            })

            binding.refresh.setOnRefreshListener {
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.getHumors()
                    viewModel.getGuest()
                }
            }

        }
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.getHumors()
            viewModel.getGuest()
        }
    }


}