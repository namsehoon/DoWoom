package com.example.dowoom.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dowoom.R
import com.example.dowoom.databinding.ChooseMultiImageBinding
import com.example.dowoom.databinding.ItemGameBinding
import com.example.dowoom.databinding.ReceivemsgItemBinding

class MultiImageAdapter(var uriList: MutableList<Uri>,val context: Context, val deleteClicked:(Uri , position:Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.choose_multi_image,parent,false)
        return ImageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return uriList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val imageUri = uriList[position]
        val viewHolder = holder as ImageViewHolder

        Glide.with(context)
            .load(imageUri)
            .placeholder(R.drawable.ic_baseline_placeholder_24) // 이미지로딩을 시작하기전에 보여줄 이미지
            .error(R.drawable.ic_baseline_image_not_supported_24) // 불러오다가 에러발생
            .fallback(R.drawable.ic_baseline_image_not_supported_24) // 이미지가 null
            .into(viewHolder.imageBinding.ivTookImage)

        //fb 클릭 시, 삭제
        viewHolder.imageBinding.fbDeleteBtn.setOnClickListener {
            uriList.remove(imageUri)
            deleteClicked(imageUri,position)
        }
    }

    fun removeUri(uri: Uri) {
        uriList.remove(uri)
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageBinding : ChooseMultiImageBinding = ChooseMultiImageBinding.bind(itemView)
    }
}