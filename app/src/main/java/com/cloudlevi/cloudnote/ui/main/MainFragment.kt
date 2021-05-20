package com.cloudlevi.cloudnote.ui.main

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cloudlevi.cloudnote.R
import com.cloudlevi.cloudnote.data.Folder
import com.cloudlevi.cloudnote.data.Note
import com.cloudlevi.cloudnote.databinding.FragmentMainBinding
import com.cloudlevi.cloudnote.extensions.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {

    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainFragmentViewModel by viewModels()
    private val notesAdapter = NotesAdapter()
    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMainBinding.bind(view)

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Any>("deleteItem")?.observe(
            viewLifecycleOwner) { result ->
            Log.d(TAG, "DELETE: $result")
            viewModel.onDeleteItem(result)
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Int>("position")?.observe(
            viewLifecycleOwner) { result ->
            notesAdapter.notifyItemChanged(result)
        }

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

            viewModel.queryNotesLiveData.observe(viewLifecycleOwner){
                viewModel.queryResultChanged(it)
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
                    action.position = viewHolder.adapterPosition

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
                }
            }).attachToRecyclerView(mainNotesRecyclerView)
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main_fragment, menu)

        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChanged {
            viewModel.searchQuery.value = it
        }
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


