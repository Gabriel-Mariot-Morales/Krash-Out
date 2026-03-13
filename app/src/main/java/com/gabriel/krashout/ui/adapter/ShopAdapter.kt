package com.gabriel.krashout.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gabriel.krashout.data.local.entity.ShopItemEntity
import com.gabriel.krashout.databinding.ItemShopBinding

class ShopAdapter(private val onBuyClicked: (ShopItemEntity) -> Unit) :
    ListAdapter<ShopItemEntity, ShopAdapter.ShopViewHolder>(ShopDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val binding = ItemShopBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShopViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ShopViewHolder(private val binding: ItemShopBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ShopItemEntity) {
            binding.textItemName.text = item.name
            binding.textItemPrice.text = item.price.toString()

            // Accedemos al TextView del icono (el primer hijo del FrameLayout de fondo)
            val iconView = binding.viewItemIconBackground.getChildAt(0) as TextView
            iconView.text = item.iconEmoji

            // Si ya está comprado, cambiamos el diseño para reflejarlo
            if (item.isPurchased) {
                binding.textItemPrice.text = "VENDIDO"
                binding.buttonBuy.alpha = 0.5f
                binding.buttonBuy.isClickable = false
            } else {
                binding.buttonBuy.alpha = 1.0f
                binding.buttonBuy.isClickable = true
                binding.buttonBuy.setOnClickListener {
                    onBuyClicked(item)
                }
            }
        }
    }

    class ShopDiffCallback : DiffUtil.ItemCallback<ShopItemEntity>() {
        override fun areItemsTheSame(oldItem: ShopItemEntity, newItem: ShopItemEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShopItemEntity, newItem: ShopItemEntity): Boolean {
            return oldItem == newItem
        }
    }
}