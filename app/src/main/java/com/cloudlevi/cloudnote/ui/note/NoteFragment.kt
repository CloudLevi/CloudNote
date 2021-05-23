package com.cloudlevi.cloudnote.ui.note

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cloudlevi.cloudnote.R
import com.cloudlevi.cloudnote.data.Note
import com.cloudlevi.cloudnote.databinding.FragmentNoteBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import com.cloudlevi.cloudnote.ui.note.NoteEvent.*
import yuku.ambilwarna.AmbilWarnaDialog

@AndroidEntryPoint
class NoteFragment: Fragment(R.layout.fragment_note) {

    private val viewModel: NoteViewModel by viewModels()
    private lateinit var binding: FragmentNoteBinding

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

        if (arguments != null) viewModel.currentNote = NoteFragmentArgs.fromBundle(requireArguments()).note

        binding = FragmentNoteBinding.bind(view)

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Int>("chosen_folder")
            ?.observe(viewLifecycleOwner) { result ->
                viewModel.currentNote.folder = result
                activity?.invalidateOptionsMenu()
            }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Note>("note_password")
            ?.observe(viewLifecycleOwner) { result ->
                viewModel.currentNote = result
                viewModel.updateNote(result)
                activity?.invalidateOptionsMenu()
            }

        binding.apply {
            titleEditText.setText(viewModel.currentNote.title)
            descriptionEditText.setText(viewModel.currentNote.description)
            mainFrameLayout.background = ColorDrawable(Color.parseColor(viewModel.currentNote.background_color))

            titleEditText.addTextChangedListener {
                if (it != null) viewModel.currentNote.title = it.toString().trim()
                else viewModel.currentNote.title = ""
            }

            descriptionEditText.addTextChangedListener {
                if (it != null) viewModel.currentNote.description = it.toString().trim()
                else viewModel.currentNote.description = ""
            }

        }

        viewModel.foldersLiveData.observe(viewLifecycleOwner){
            viewModel.onFoldersLiveDataChanged(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.noteEvent.collect { event ->
                when(event){
                    is NavigateToMainFragment -> findNavController()
                        .navigate(NoteFragmentDirections.actionNoteFragmentToMainFragment())
                    is NavigateToFolder -> findNavController()
                        .navigate(NoteFragmentDirections.actionNoteFragmentToFolderViewFragment(event.folder))
                    is SendToastMessage -> sendToastMessage(event.message)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_note_fragment, menu)

        if (viewModel.currentNote.pinned) menu.findItem(R.id.pin_note).icon = getDrawable(R.drawable.ic_pin_filled)
        else menu.findItem(R.id.pin_note).icon = getDrawable(R.drawable.ic_pin_outline)

        if (viewModel.currentNote.folder != -1) {
            menu.findItem(R.id.add_folder).icon = getDrawable(R.drawable.ic_folder_filled)
            menu.findItem(R.id.add_folder).icon.setTint(ContextCompat.getColor(requireContext(), R.color.white))
        }
        else menu.findItem(R.id.add_folder).icon = getDrawable(R.drawable.ic_folder_outline)

        if (viewModel.currentNote.password.isNotEmpty()) menu.findItem(R.id.password).icon = getDrawable(R.drawable.ic_lock_locked)
        else menu.findItem(R.id.password).icon = getDrawable(R.drawable.ic_lock_open)

        menu.findItem(R.id.bg_color).icon.setTint(Color.parseColor(viewModel.currentNote.background_color.trim()))

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.pin_note -> {
                determinePinIcon(item)
                viewModel.onPinClicked()
                true
            }
            R.id.add_folder -> {
                findNavController()
                    .navigate(NoteFragmentDirections.actionNoteFragmentToChangeFolderDialog(viewModel.currentNote))
                true
            }
            R.id.password -> {
                findNavController()
                    .navigate(NoteFragmentDirections.actionNoteFragmentToChangePasswordDialog(viewModel.currentNote))
                true
            }
            R.id.bg_color -> {
                openColorPicker(Color.parseColor(viewModel.currentNote.background_color.trim()))
                true
            }
            R.id.share -> {
                viewModel.onPauseCalled()
                openShareIntent()
                true
            }
            R.id.delete -> {
                viewModel.onDeleteClicked()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun sendToastMessage(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun determinePinIcon(item: MenuItem){
        when(viewModel.currentNote.pinned){
            true -> item.icon = getDrawable(R.drawable.ic_pin_outline)
            false -> item.icon = getDrawable(R.drawable.ic_pin_filled)
        }
    }

    private fun openShareIntent(){
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, viewModel.createShareText())
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share using:")
        startActivity(shareIntent)
    }

    private fun getDrawable(id: Int): Drawable?{
        return ContextCompat.getDrawable(requireContext(), id)
    }

    private fun openColorPicker(currentColor: Int){
        val colorPicker = AmbilWarnaDialog(
            requireContext(),
            currentColor,
            object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {
                }

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    val hexString = "#${Integer.toHexString(color)}"
                    viewModel.updateBGColor(hexString)
                    activity?.invalidateOptionsMenu()
                    binding.mainFrameLayout.background =
                        ColorDrawable(Color.parseColor(viewModel.currentNote.background_color))
                }

            })

        colorPicker.show()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPauseCalled()
    }

}