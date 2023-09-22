package com.example.natifetesttask.ui.gifmainlist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.natifetesttask.R
import com.example.natifetesttask.data.ApiResponseData
import com.example.natifetesttask.databinding.ItemGifBinding
import com.squareup.picasso.Picasso

class GifMainListAdapter(
    private val listener: GifClickListener,
    private val context: Context
) : PagingDataAdapter<ApiResponseData.DataEntity, GifMainListAdapter.GifHolder>(
    DiffGifCallBack()
) {

    inner class GifHolder(private val binding: ItemGifBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    item?.let { listener.onClick(it.images.original.url) }
                }
            }
        }

        fun bind(gif: ApiResponseData.DataEntity) {
            Glide.with(context)
                .load(gif.images.original.url)
                .apply(RequestOptions().override(gif.images.original.width, gif.images.original.height))
                .error(R.drawable.error_24)
                .placeholder(R.drawable.ic_load)
                .into(binding.imageViewGifItem)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifHolder {
        val binding = ItemGifBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GifHolder(binding)
    }

    override fun onBindViewHolder(holder: GifHolder, position: Int) {
        val currentItem = getItem(position)
        currentItem?.let { holder.bind(it) }
    }

    interface GifClickListener {
        fun onClick(item: String)
    }

    class DiffGifCallBack : DiffUtil.ItemCallback<ApiResponseData.DataEntity>() {
        override fun areItemsTheSame(
            oldItem: ApiResponseData.DataEntity,
            newItem: ApiResponseData.DataEntity
        ): Boolean =
            oldItem.url == newItem.url

        override fun areContentsTheSame(
            oldItem: ApiResponseData.DataEntity,
            newItem: ApiResponseData.DataEntity
        ): Boolean =
            oldItem == newItem
    }
}