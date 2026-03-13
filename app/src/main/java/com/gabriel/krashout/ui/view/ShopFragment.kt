package com.gabriel.krashout.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.gabriel.krashout.data.local.entity.ShopItemEntity
import com.gabriel.krashout.databinding.FragmentShopBinding
import com.gabriel.krashout.ui.adapter.ShopAdapter

class ShopFragment : Fragment() {

    private var _binding: FragmentShopBinding? = null
    private val binding get() = _binding!!

    // Necesitamos 3 adaptadores, uno para cada sección
    private lateinit var headAdapter: ShopAdapter
    private lateinit var topAdapter: ShopAdapter
    private lateinit var botAdapter: ShopAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentShopBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        loadMockData()
    }

    private fun setupAdapters() {
        // ¿Qué pasa cuando tocamos el botón de comprar? Por ahora, un simple aviso.
        val onBuyClick: (ShopItemEntity) -> Unit = { item ->
            Toast.makeText(requireContext(), "Has seleccionado: ${item.name}", Toast.LENGTH_SHORT).show()
        }

        // Instanciamos los 3 adaptadores con esa misma acción
        headAdapter = ShopAdapter(onBuyClick)
        topAdapter = ShopAdapter(onBuyClick)
        botAdapter = ShopAdapter(onBuyClick)

        // Enganchamos cada adaptador a su RecyclerView correspondiente
        binding.rvHeadgear.adapter = headAdapter
        binding.rvTops.adapter = topAdapter
        binding.rvBots.adapter = botAdapter
    }

    private fun loadMockData() {
        // Creamos nuestro inventario falso para la prueba visual
        val mockItems = listOf(
            ShopItemEntity(1, "Gorro Pirata", 500, "🎩", "HEAD"),
            ShopItemEntity(2, "Casco Espacial", 1200, "🧑‍🚀", "HEAD"),
            ShopItemEntity(3, "Corona", 5000, "👑", "HEAD"),

            ShopItemEntity(4, "Camiseta Azul", 300, "👕", "TOP"),
            ShopItemEntity(5, "Armadura", 1500, "🛡️", "TOP"),
            ShopItemEntity(6, "Chaqueta", 2500, "🧥", "TOP"),

            ShopItemEntity(7, "Pantalones Cortos", 200, "🩳", "BOT"),
            ShopItemEntity(8, "Vaqueros", 600, "👖", "BOT"),
            ShopItemEntity(9, "Botas de Hierro", 1800, "🥾", "BOT")
        )

        // Filtramos la lista maestra y le mandamos a cada estante solo lo suyo
        headAdapter.submitList(mockItems.filter { it.category == "HEAD" })
        topAdapter.submitList(mockItems.filter { it.category == "TOP" })
        botAdapter.submitList(mockItems.filter { it.category == "BOT" })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}