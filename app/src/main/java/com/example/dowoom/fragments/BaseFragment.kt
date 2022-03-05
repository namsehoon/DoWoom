package com.example.dowoom.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.dowoom.R

abstract class BaseFragment<T:ViewBinding>(var TAG:String? = null, @LayoutRes private val layoutRes: Int) : Fragment() {

    var FragTag:String? = null

    private var _binding:T? = null
    val binding get() = _binding ?: error("View를 참조하기 위한 binding이 초기화되지 않았습니다.")


    init {
        this.FragTag = TAG
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        Log.d(FragTag,  FragTag + " onCreateView")
        _binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }



    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d(FragTag,  FragTag + " onCreate")
        super.onCreate(savedInstanceState)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(FragTag,  FragTag + " onViewCreated")

        super.onViewCreated(view, savedInstanceState)
    }


    override fun onResume() {
        Log.d(FragTag,  FragTag + " onResume")
        super.onResume()
    }

    override fun onDestroy() {
        Log.d(FragTag,  FragTag + " onDestroy")
        super.onDestroy()
    }

    override fun onDestroyView() {
        Log.d(FragTag,  FragTag + " onDestroyView")
        _binding = null
        super.onDestroyView()
    }

    override fun onStart() {
        Log.d(FragTag,  FragTag + " onStart")
        super.onStart()
    }

}