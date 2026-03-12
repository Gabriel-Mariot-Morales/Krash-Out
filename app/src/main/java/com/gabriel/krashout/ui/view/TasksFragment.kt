package com.gabriel.krashout.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.gabriel.krashout.R
import com.gabriel.krashout.data.local.AppDatabase
import com.gabriel.krashout.data.local.entity.TaskEntity
import com.gabriel.krashout.data.repository.KrashOutRepository
import com.gabriel.krashout.databinding.DialogAddTaskBinding
import com.gabriel.krashout.databinding.FragmentTasksBinding
import com.gabriel.krashout.ui.adapter.TaskAdapter
import com.gabriel.krashout.ui.viewmodel.MainViewModel
import com.gabriel.krashout.ui.viewmodel.MainViewModelFactory
import kotlinx.coroutines.launch

class TasksFragment : Fragment() {

    // View Binding en plural restaurado
    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Instanciamos la base de datos y el ViewModel
        val database = AppDatabase.getDatabase(requireContext())
        val repository = KrashOutRepository(database.taskDao(), database.userStatsDao())
        val factory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(requireActivity(), factory)[MainViewModel::class.java]

        taskAdapter = TaskAdapter { task ->
            viewModel.completeTask(task)
        }

        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewTasks.adapter = taskAdapter

        // Observadores de datos
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userProfile.collect { profile ->
                if (profile != null) {
                    binding.textUserLevel.text = "Nivel ${profile.currentLevel}"
                    binding.textUserCoins.text = "Monedas: ${profile.virtualCoins}"
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pendingTasks.collect { tasks ->
                taskAdapter.submitList(tasks)
            }
        }

        binding.fabAddTask.setOnClickListener {
            showAddTaskDialog()
        }
    }

    private fun showAddTaskDialog() {
        val dialogBinding = DialogAddTaskBinding.inflate(layoutInflater)

        val textViews = listOf(
            dialogBinding.tvDiffVeryEasy, dialogBinding.tvDiffEasy,
            dialogBinding.tvDiffMedium, dialogBinding.tvDiffHard, dialogBinding.tvDiffVeryHard
        )
        var selectedXp = 0

        fun updateSegmentSelection(selectedTextView: android.widget.TextView, xp: Int) {
            selectedXp = xp
            textViews.forEach { it.alpha = 0.3f }
            selectedTextView.alpha = 1.0f
        }

        textViews.forEach { it.alpha = 0.3f }

        dialogBinding.tvDiffVeryEasy.setOnClickListener { updateSegmentSelection(dialogBinding.tvDiffVeryEasy, 20) }
        dialogBinding.tvDiffEasy.setOnClickListener { updateSegmentSelection(dialogBinding.tvDiffEasy, 40) }
        dialogBinding.tvDiffMedium.setOnClickListener { updateSegmentSelection(dialogBinding.tvDiffMedium, 60) }
        dialogBinding.tvDiffHard.setOnClickListener { updateSegmentSelection(dialogBinding.tvDiffHard, 80) }
        dialogBinding.tvDiffVeryHard.setOnClickListener { updateSegmentSelection(dialogBinding.tvDiffVeryHard, 100) }

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton("Añadir") { _, _ ->
                val title = dialogBinding.editTaskTitle.text.toString()
                val description = dialogBinding.editTaskDescription.text.toString()

                if (title.isNotEmpty() && selectedXp > 0) {
                    val newTask = TaskEntity(
                        title = title, description = description, xpReward = selectedXp,
                        isCompleted = false, assignedDateTimestamp = System.currentTimeMillis()
                    )
                    viewModel.addTask(newTask)
                } else {
                    android.widget.Toast.makeText(requireContext(), "Falta el título o elegir una dificultad", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}