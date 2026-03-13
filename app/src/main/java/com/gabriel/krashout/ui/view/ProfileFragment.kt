package com.gabriel.krashout.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gabriel.krashout.databinding.FragmentProfileBinding
import com.gabriel.krashout.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import kotlin.math.pow

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Nos conectamos al mismo ViewModel central que ya esta usando la MainActivity
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        // Escuchamos los cambios en el perfil de forma reactiva
        lifecycleScope.launch {
            viewModel.userProfile.collect { profile ->
                if (profile != null) {
                    // Reflejamos los datos exactos de la base de datos
                    binding.textProfileLevel.text = "Nivel ${profile.currentLevel}"
                    binding.textTotalXp.text = profile.totalXp.toString()

                    // --- MATEMATICAS DE LA BARRA DE PROGRESO ---
                    // XP base del nivel actual = ((Nivel - 1) * 10)^2
                    val currentLevelBaseXp = ((profile.currentLevel - 1) * 10).toDouble().pow(2).toInt()

                    // XP necesaria para alcanzar el siguiente nivel = (Nivel * 10)^2
                    val nextLevelXp = (profile.currentLevel * 10).toDouble().pow(2).toInt()

                    // Calculamos el progreso relativo dentro del nivel actual
                    val currentProgress = profile.totalXp - currentLevelBaseXp
                    val totalRequiredForNext = nextLevelXp - currentLevelBaseXp

                    // Ajustamos la barra visual
                    binding.progressXp.max = totalRequiredForNext
                    binding.progressXp.progress = currentProgress
                }
            }
        }

        // Dejamos el boton de exportacion preparado para la futura fase de seguridad
        binding.buttonBackup.setOnClickListener {
            // Aqui ira la logica de copia de seguridad local
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}