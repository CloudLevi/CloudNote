package com.cloudlevi.cloudnote.ui.folderView

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
import com.cloudlevi.cloudnote.databinding.FragmentFolderViewBinding
import com.cloudlevi.cloudnote.extensions.onQueryTextChanged
import com.cloudlevi.cloudnote.ui.main.ItemClickListener
import com.cloudlevi.cloudnote.ui.main.MainFragmentDirections
import com.cloudlevi.cloudnote.ui.main.NotesAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderViewFragment: Fragment(R.layout.fragment_folder_view), ItemClickListener {

    private val viewModel: FolderViewViewModel by viewModels()
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var binding: FragmentFolderViewBinding
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notesAdapter = NotesAdapter(
            viewModel.homeViewPreferenceLiveData.value ?: HOME_TYPE_LISTVIEW,
            this@FolderViewFragment
        )


        if (arguments != null) {
            viewModel.folder = FolderViewFragmentArgs.fromBundle(requireArguments()).folder
            viewModel.folderID = viewModel.folder.id
        }

        binding = FragmentFolderViewBinding.bind(view)

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Any>("deleteItem")
            ?.observe(
                viewLifecycleOwner
            ) { result ->
                viewModel.onDeleteNote(result as Note)
            }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Int>("position")
            ?.observe(
                viewLifecycleOwner
            ) { result ->
                notesAdapter.notifyItemChanged(result)
            }

        binding.apply {

            val folderTitleText = " '${viewModel.folder.title}'"
            folderTV.text = folderTitleText

            viewModel.folderContents.observe(viewLifecycleOwner){
                viewModel.folderContentsChanged(it)
            }


            viewModel.homeViewPreferenceLiveData.observe(viewLifecycleOwner){
                activity?.invalidateOptionsMenu()

                notesAdapter = NotesAdapter(it, this@FolderViewFragment)
                notesAdapter.submitList(viewModel.dataList.value)
                notesAdapter.notifyDataSetChanged()
                updateRecyclerView()
            }

            viewModel.dataList.observe(viewLifecycleOwner){
                notesAdapter.submitList(it)
                notesAdapter.notifyDataSetChanged()
                updateRecyclerView()
            }

            viewModel.queryNotesLiveData.observe(viewLifecycleOwner){
                viewModel.queryResultChanged(it)
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
                    val action =
                        FolderViewFragmentDirections.actionFolderViewFragmentToDeleteConfirmationDialog()
                    action.position = viewHolder.adapterPosition

                    when (itemSwiped) {
                        is Note -> {
                            action.note = itemSwiped
                            findNavController().navigate(action)
                        }
                    }
                }
            }).attachToRecyclerView(folderViewRecyclerView)
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
                val action = FolderViewFragmentDirections.actionFolderViewFragmentToAddItemFragment(false)
                action.folder = viewModel.folder
                findNavController().navigate(action)
                true
            }
            R.id.change_layout -> {
                viewModel.homeViewChanged()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateRecyclerView(){
        binding.folderViewRecyclerView.apply {
            adapter = notesAdapter
            layoutManager = getFragmentLayoutManager()
            setHasFixedSize(true)
        }
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

    override fun OnFolderClickListener(folder: Folder) {
        TODO("Not yet implemented")
    }

    override fun OnNoteClickListener(note: Note) {
        TODO("Not yet implemented")
    }
}