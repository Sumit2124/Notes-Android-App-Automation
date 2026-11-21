package com.example.myapp.ui.todonote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapp.databinding.FragmentTodoNoteBinding
import com.example.myapp.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class TodoNoteFragment : Fragment() {

    private var _binding: FragmentTodoNoteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TodoNoteViewModel by viewModels()
    private lateinit var todoAdapter: TodoAdapter
    private val args: TodoNoteFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.todoNoteNameEditText.doAfterTextChanged {
            if (!it.isNullOrBlank()) binding.todoTitleErrorTextView.visibility = View.GONE
        }

        setupRecyclerView()

        viewModel.loadNote(args.noteId)
        viewModel.currentNote.observe(viewLifecycleOwner) { note ->
            note?.let {
                binding.todoNoteNameEditText.setText(it.name)
            }
        }

        viewModel.todoItemsLiveData.observe(viewLifecycleOwner) { items ->
            todoAdapter.updateData(items)
        }

        binding.addTodoItemButton.setOnClickListener {
            val newItemText = binding.newTodoItemEditText.text.toString().trim()
            if (newItemText.isNotEmpty()) {
                viewModel.addTodoItem(newItemText)
                binding.newTodoItemEditText.text.clear()
            }
        }

        binding.saveTodoNoteButton.setOnClickListener {
            val todoNoteName = binding.todoNoteNameEditText.text.toString().trim()
            if (todoNoteName.isNotEmpty()) {
                viewModel.saveTodoNote(todoNoteName)
                findNavController().navigateUp()
            } else {
                binding.todoNoteNameEditText.error = getString(R.string.title_required)
                binding.todoTitleErrorTextView.visibility = View.VISIBLE
            }
        }
    }

    private fun setupRecyclerView() {
        todoAdapter = TodoAdapter(mutableListOf(),
            onItemChanged = { item, position -> viewModel.updateTodoItem(item, position) },
            onEdit = { item, position ->
                val input = EditText(requireContext()).apply {
                    setText(item.text)
                    setSelection(text.length)
                }
                val dialog = MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.edit_item)
                    .setView(input)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.save_note_button, null)
                    .create()
                dialog.setOnShowListener {
                    dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val text = input.text.toString().trim()
                        if (text.isEmpty()) input.error = getString(R.string.item_required)
                        else {
                            viewModel.updateTodoItem(item.copy(text = text), position)
                            dialog.dismiss()
                        }
                    }
                }
                dialog.show()
            },
            onDelete = { position -> viewModel.deleteTodoItem(position) }
        )
        binding.todoItemsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = todoAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
