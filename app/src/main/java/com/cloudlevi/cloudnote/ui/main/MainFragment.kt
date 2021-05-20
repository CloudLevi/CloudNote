package com.cloudlevi.cloudnote.ui.main

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cloudlevi.cloudnote.R
import com.cloudlevi.cloudnote.data.Folder
import com.cloudlevi.cloudnote.data.Note
import com.cloudlevi.cloudnote.databinding.ActivityMainBinding
import com.cloudlevi.cloudnote.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {

    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainFragmentViewModel by viewModels()
    private val notesAdapter = NotesAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMainBinding.bind(view)

        binding.apply {

            mainNotesRecyclerView.apply {
                adapter = notesAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                setHasFixedSize(true)
            }

            viewModel.notes.observe(viewLifecycleOwner) {
//                Log.d(TAG, "NOTES: $it")
//                notesAdapter.submitList(it)
//                notesAdapter.notifyDataSetChanged()
                viewModel.notesChanged(it)
            }

            viewModel.folders.observe(viewLifecycleOwner) {
//                Log.d(TAG, "FOLDERS: $it")
                viewModel.foldersChanged(it)
            }

            viewModel.folderNoteLiveData.observe(viewLifecycleOwner) {
                Log.d(TAG, "onViewCreated: $it")
                if (!it.contains(null)) {
                    notesAdapter.submitList(it)
                    notesAdapter.notifyDataSetChanged()
                }
            }

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                    val itemSwiped = notesAdapter.currentList[viewHolder.adapterPosition]
                    val action = MainFragmentDirections.actionMainFragmentToDeleteConfirmationDialog()

                    when (itemSwiped) {
                        is Note -> {
                            action.note = itemSwiped
                            findNavController().navigate(action)
                        }
                        is Folder -> {
                            action.folder = itemSwiped
                            findNavController().navigate(action)
                        }
                    }
                    viewModel.onItemSwiped(itemSwiped)
                }
            }).attachToRecyclerView(mainNotesRecyclerView)
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main_fragment, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_note -> {
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToAddItemFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}