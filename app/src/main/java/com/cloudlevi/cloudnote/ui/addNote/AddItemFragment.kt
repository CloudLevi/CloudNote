package com.cloudlevi.cloudnote.ui.addNote

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cloudlevi.cloudnote.ITEM_TYPE_FOLDER
import com.cloudlevi.cloudnote.ITEM_TYPE_NOTE
import com.cloudlevi.cloudnote.R
import com.cloudlevi.cloudnote.ui.addNote.AddItemEvent.*
import com.cloudlevi.cloudnote.databinding.FragmentAddItemBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_item.*
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddItemFragment : Fragment(R.layout.fragment_add_item) {

    private lateinit var binding: FragmentAddItemBinding
    private val viewModel: AddItemViewModel by viewModels()
    private lateinit var spinnerAdapter: ArrayAdapter<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddItemBinding.bind(view)

        binding.apply {
            choiceNoteButton.setOnClickListener {
                viewModel.choiceClicked(ITEM_TYPE_NOTE)
            }

            choiceFolderButton.setOnClickListener {
                viewModel.choiceClicked(ITEM_TYPE_FOLDER)
            }

            titleEditText.addTextChangedListener {
                viewModel.titleText = it.toString().trim()
            }

            descriptionEditText.addTextChangedListener {
                viewModel.descriptionText = it.toString().trim()
            }

            addButton.setOnClickListener {
                viewModel.onAddButtonClicked()
            }
        }

        viewModel.foldersLiveData.observe(viewLifecycleOwner){
            spinnerAdapter = ArrayAdapter<String>(requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                viewModel.getFolderTitles(it))

            spinnerFolders.adapter = spinnerAdapter
        }

        spinnerFolders.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                viewModel.chosenFolder = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addItemEvent.collect { event ->
                when (event) {
                    is ChangeProgressStatus -> changeProgressStatus(event.status)
                    is ChangeChoiceSelection -> changeChoiceSelection(event.choice)
                    is SendToastMessage -> sendToastMessage(event.message)
                    is NavigateToMainFragment -> findNavController()
                        .navigate(AddItemFragmentDirections.actionAddItemFragmentToMainFragment())
                }
            }
        }
    }

    private fun sendToastMessage(message: String) =
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()

    private fun changeProgressStatus(status: Int) {
        binding.progressBar.visibility = status
    }

    private fun changeChoiceSelection(newChoice: Int) {
        binding.apply {
            var stringAddButton = ""

            when (newChoice) {
                ITEM_TYPE_NOTE -> {
                    choiceNoteButton.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.buttonColorActive
                        )
                    )
                    choiceFolderButton.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.buttonColorInactive
                        )
                    )
                    descriptionEditText.visibility = View.VISIBLE
                    folderChoiceTV.visibility = View.VISIBLE
                    spinnerFolders.visibility = View.VISIBLE
                    stringAddButton = "Add Note"
                }
                ITEM_TYPE_FOLDER -> {
                    choiceNoteButton.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.buttonColorInactive
                        )
                    )
                    choiceFolderButton.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.buttonColorActive
                        )
                    )
                    descriptionEditText.visibility = View.GONE
                    folderChoiceTV.visibility = View.GONE
                    spinnerFolders.visibility = View.GONE
                    stringAddButton = "Add Folder"
                }
            }
            addButton.text = stringAddButton
        }
    }
}