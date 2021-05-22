package com.cloudlevi.cloudnote.ui.main

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.cloudlevi.cloudnote.HOME_TYPE_GRIDVIEW
import com.cloudlevi.cloudnote.HOME_TYPE_LISTVIEW
import com.cloudlevi.cloudnote.R
import com.cloudlevi.cloudnote.data.Folder
import com.cloudlevi.cloudnote.data.Note
import com.cloudlevi.cloudnote.databinding.FragmentMainBinding
import com.cloudlevi.cloudnote.extensions.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main), ItemClickListener {

    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainFragmentViewModel by viewModels()
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var searchView: SearchView
    private lateinit var menu: Menu

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notesAdapter = NotesAdapter(
            viewModel.homeViewPreferenceLiveData.value ?: HOME_TYPE_LISTVIEW,
            this@MainFragment
        )

        binding = FragmentMainBinding.bind(view)

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Any>("deleteItem")
            ?.observe(viewLifecycleOwner) { result ->
                Log.d(TAG, "DELETE: $result")
                viewModel.onDeleteItem(result)
            }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Note>("note_pin")
            ?.observe(viewLifecycleOwner) { result ->
                viewModel.onPinnedNote(result)
            }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Int>("position")
            ?.observe(viewLifecycleOwner) { result ->
                notesAdapter.notifyItemChanged(result)
            }


        binding.apply {

            activity?.registerForContextMenu(mainNotesRecyclerView)

            viewModel.notes.observe(viewLifecycleOwner) {
                viewModel.notesChanged(it)
            }

            viewModel.folders.observe(viewLifecycleOwner) {
                viewModel.foldersChanged(it)
            }

            viewModel.queryNotesLiveData.observe(viewLifecycleOwner) {
                viewModel.queryResultChanged(it)
            }

            viewModel.homeViewPreferenceLiveData.observe(viewLifecycleOwner) {

                activity?.invalidateOptionsMenu()

                notesAdapter = NotesAdapter(it, this@MainFragment)
                notesAdapter.submitList(viewModel.dataList)

                mainNotesRecyclerView.apply {
                    adapter = notesAdapter
                    layoutManager = getFragmentLayoutManager()
                    setHasFixedSize(true)
                }
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

                    if (direction == ItemTouchHelper.RIGHT) {
                        val action =
                            MainFragmentDirections.actionMainFragmentToDeleteConfirmationDialog()
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
                    } else if (itemSwiped is Note) {
                        val action =
                            MainFragmentDirections
                                .actionMainFragmentToPinConfirmDialog(itemSwiped, viewHolder.adapterPosition)
                        findNavController().navigate(action)
                    }
                    else notesAdapter.notifyItemChanged(viewHolder.adapterPosition)
                }
            }).attachToRecyclerView(mainNotesRecyclerView)
        }

        setHasOptionsMenu(true)
    }

    private fun getFragmentLayoutManager(): RecyclerView.LayoutManager {
        return when (viewModel.homeViewPreferenceLiveData.value) {
            HOME_TYPE_LISTVIEW -> LinearLayoutManager(requireContext())
            HOME_TYPE_GRIDVIEW -> StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )
            else -> LinearLayoutManager(requireContext())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main_fragment, menu)

        Log.d(TAG, "onCreateOptionsMenu")

        when (viewModel.homeViewPreferenceLiveData.value) {
            HOME_TYPE_LISTVIEW ->
                menu.getItem(menu.size() - 1).icon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_listview)
            HOME_TYPE_GRIDVIEW ->
                menu.getItem(menu.size() - 1).icon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_gridview)
        }

        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChanged {
            viewModel.searchQuery.value = it
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_note -> {
                findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToAddItemFragment(
                        true
                    )
                )
                true
            }
            R.id.change_layout -> {
                viewModel.homeViewChanged()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun OnFolderClickListener(folder: Folder) {
        val action = MainFragmentDirections.actionMainFragmentToFolderViewFragment(folder)
        findNavController().navigate(action)
    }

    override fun OnNoteClickListener(note: Note) {
        val action = MainFragmentDirections.actionMainFragmentToNoteFragment(note)
        findNavController().navigate(action)
    }
}


