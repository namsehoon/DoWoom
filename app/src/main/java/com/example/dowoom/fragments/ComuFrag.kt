package com.example.dowoom.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dowoom.R
import com.example.dowoom.Util.GlideApp
import com.example.dowoom.activity.comu.GuestWriteActivity
import com.example.dowoom.adapter.CommentAdapter
import com.example.dowoom.adapter.ComuAdapter
import com.example.dowoom.databinding.ComuFragmentBinding
import com.example.dowoom.factory.ComuViewModelFactory
import com.example.dowoom.model.comunityModel.ComuModel
import com.example.dowoom.retrofit.GezipRepo
import com.example.dowoom.viewmodel.mainViewmodel.ComuViewModel
import kotlinx.android.synthetic.main.comu_fragment.*
import kotlinx.coroutines.*





class ComuFrag : BaseFragment<ComuFragmentBinding>(TAG = "ComeFrag", R.layout.comu_fragment), View.OnClickListener {


    companion object {
        fun newInstance() = ComuFrag()
    }
    private val repository = GezipRepo()
    @ExperimentalCoroutinesApi
    private val viewModel:ComuViewModel by viewModels { ComuViewModelFactory(repository)}
    //adapter
    private lateinit var adapter: ComuAdapter // 컨텐츠
    private lateinit var commentAdapter: CommentAdapter // 코멘트

    val HUMOR = "유머게시판"
    val ANONYMOUS = "익명게시판"
    var comuModelId:String? = null
    var kindOf : Int? = null

    //관찰
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = requireActivity()

        observerData()

    }

    override fun onDestroy() {
        super.onDestroy()
        comuModelId = null
        kindOf = null
    }

    // 보일 때 : 뒤로가기 한번 누를 때,
    // 숨겨 질 때 : 컨텐츠 누를 때

    //버튼 리스너, recyclerview 어뎁터
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)  {
                   super.onViewCreated(view, savedInstanceState)


        adapter = ComuAdapter(requireActivity(), contentClicked = { comuModel, position ->
            //자식 프로그먼트로 교체

            Log.d("Abcd","부모에서 ComuAdapter 클릭됨")

            binding.llToWrite.visibility = View.GONE //글작성
            binding.llComuList.visibility = View.GONE // 컨텐츠 리스트 .. todo: no adapter onttach 에러 ;;
            binding.llconTents.visibility = View.VISIBLE // 컨텐츠 보여주는 뷰
            // 이전 및 다음 페이지
            binding.tvNextPage.visibility = View.GONE
            binding.tvBeforePage.visibility = View.GONE

            //댓글 작성을 위한
            comuModelId = comuModel.uid
            kindOf = comuModel.kindOf

            //스크롤 맨위로
            binding.scroll.fullScroll(View.FOCUS_UP)
            binding.scroll.fullScroll(ScrollView.FOCUS_UP)

            binding.comuModel = comuModel

            //child fragment에 데이터 보내기
            CoroutineScope(Dispatchers.Main).launch {
                binding.llImages.removeAllViews()
                viewModel.getComments(comuModel)
            }

            if (comuModel.kindOf == 1) { // 유머게시판
                Log.d("abcd", "comufrag - adapter - humor : 유머게시판 ")

                // 게시판 이름
                binding.tvKindOf.text = HUMOR



                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.getHumorContent(comuModel)
                }


            } else { // 익명 게시판
                Log.d("abcd", "comufrag - adapter - guest : 익명게시판으로 ")

                binding.llName.visibility = View.VISIBLE //익명 : 이름
                binding.llPassword.visibility = View.VISIBLE// 익명 : 패스워드

                // 게시판 이름
                binding.tvKindOf.text = ANONYMOUS

            }


        })


        //댓글 어뎁터
        commentAdapter = CommentAdapter(requireContext())

        binding.rvMainComuList.adapter = adapter
        binding.rvComment.adapter = commentAdapter

        binding.rvMainComuList.setHasFixedSize(true)
        binding.rvComment.setHasFixedSize(true)

        binding.rvMainComuList.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        binding.rvComment.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)



        binding.tvHumor.setOnClickListener(this@ComuFrag)
        binding.tvBest.setOnClickListener(this@ComuFrag)
        binding.tvGuest.setOnClickListener(this@ComuFrag)
        binding.tvBackToRv.setOnClickListener(this@ComuFrag)

        //글작성
        binding.tvToWrite.setOnClickListener(this@ComuFrag)
        binding.commentInsertBtn.setOnClickListener(this@ComuFrag)



    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.tvHumor -> {
                binding.llToWrite.visibility = View.GONE //글작성 버튼 숨기기
                binding.llconTents.visibility  = View.GONE //컨텐츠 표시 view 숨기기
                binding.llComuList.visibility = View.VISIBLE
                //todo : 데이터 불러오기 1, 2, 3, 4, 5
                viewModel.comuList.observe(viewLifecycleOwner, Observer { it ->
                    adapter.setContents(it)
                })

            }
            R.id.tvGuest -> { //게스트
                //글작성
                binding.llToWrite.visibility = View.VISIBLE
                binding.llconTents.visibility = View.GONE //컨텐츠 표시 view 숨기기
                binding.llComuList.visibility = View.VISIBLE

                viewModel.guestList.observe(viewLifecycleOwner, Observer { it ->
                    adapter.setContents(it)
                })

            }
            //글작성하러가기
            R.id.tvToWrite -> {
                val intent = Intent(context, GuestWriteActivity::class.java)
                context?.startActivity(intent)
            }
            //댓글 작성
            R.id.commentInsertBtn -> {
                if (kindOf != 0 && kindOf == 1) { // 유머게시판
                    val commentText = binding.etComment.text.toString() // 내용
                    if (!commentText.isNullOrEmpty() && comuModelId != null) {
                        viewModel.insertComment(comuModelId!!, commentText,kindOf!!,null)
                    } else {
                        Toast.makeText(context,"내용을 작성 해주세요.",Toast.LENGTH_SHORT).show()
                    }
                } else { //익명 게시판
                    val commentText = binding.etComment.text.toString() // 내용
                    val passwordText = binding.etPassword.text.toString()
                    if (!commentText.isNullOrEmpty() && comuModelId != null) {
                        if (!passwordText.isNullOrEmpty() && passwordText != null) {
                            viewModel.insertComment(comuModelId!!, commentText, kindOf!!, passwordText)

                        } else {
                            Toast.makeText(context,"패스워드를 입력 해주세요.",Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context,"내용을 입력 해주세요.",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            //목록으로 돌아가기
            R.id.tvBackToRv -> {
                Log.d("abcd","버튼 클릭됨")
                binding.llconTents.visibility = View.GONE
                binding.tvBeforePage.visibility = View.VISIBLE
                binding.tvNextPage.visibility = View.VISIBLE
                binding.llComuList.visibility = View.VISIBLE
            }
        }
    }

    private fun observerData() {

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.comuList.observe(viewLifecycleOwner, Observer { it ->
                Log.d("abcd","it is : ${it}")
                adapter.setContents(it)

            })

            // 새로고침 progress
            viewModel.progress.observe(viewLifecycleOwner, Observer { result ->
                binding.refresh.isRefreshing = result
            })

            //새로고침
            binding.refresh.setOnRefreshListener {
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.getHumors()
                    viewModel.getGuest()
                }
            }

            viewModel.commentList.observe(viewLifecycleOwner, Observer { comments ->
                commentAdapter.setComments(comments)

            })

        }

        //fragment 시작할 때, 데이터 가져오기
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.getHumors()
            viewModel.getGuest()
        }

        //이미지 가져와서 추가
        viewModel.comuContent.observe(viewLifecycleOwner, Observer { comuModel ->
            Log.d("abcd","comuContent is : ${comuModel}")
            binding.comuModel = comuModel

            if (comuModel.contentImg != null) {
                addImageToll(comuModel)
            }

        })

    }

    //컨텐츠 이미지 추가
    fun addImageToll(comuModel: ComuModel) {
        binding.llImages.removeAllViews()
        for (i in 0 until comuModel.contentImg?.count()!!) {
            val imgView = ImageView(context).apply {
                adjustViewBounds = true
            }
            GlideApp.with(this@ComuFrag).load(comuModel.contentImg!![i]).into(imgView)
            binding.llImages.addView(imgView)

        }

    }




}

