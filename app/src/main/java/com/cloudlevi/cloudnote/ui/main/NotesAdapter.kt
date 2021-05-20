package com.cloudlevi.cloudnote.ui.main

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cloudlevi.cloudnote.R
import com.cloudlevi.cloudnote.VIEW_TYPE_FOLDER
import com.cloudlevi.cloudnote.VIEW_TYPE_NOTE
import com.cloudlevi.cloudnote.data.Folder
import com.cloudlevi.cloudnote.data.Note
import com.cloudlevi.cloudnote.databinding.ListNoteItemBinding
import java.text.SimpleDateFormat

class NotesAdapter: ListAdapter<Any, RecyclerView.ViewHolder>(DiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_FOLDER -> {
                val binding = ListNoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FoldersViewHolder(binding)
            }
            VIEW_TYPE_NOTE -> {
                val binding = ListNoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                NotesViewHolder(binding)
            }
            else -> {
                val binding = ListNoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                NotesViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = getItem(position)

        when (getItemViewType(position)){
            VIEW_TYPE_NOTE -> {
                (holder as NotesViewHolder).bind(currentItem as Note)
            }

            VIEW_TYPE_FOLDER -> {
                (holder as FoldersViewHolder).bind(currentItem as Folder)
            }
        }
    }

    inner class FoldersViewHolder(private val binding: ListNoteItemBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(folder: Folder){
            binding.apply {
                noteTitleTV.text = folder.title
                iconImageView.setImageResource(R.drawable.ic_folder_24)
            }
        }
    }

    inner class NotesViewHolder(private val binding: ListNoteItemBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(note: Note){
            binding.apply {
                noteTitleTV.text = note.title
                noteDateTV.text = convertTime(note.date_modified)
                iconImageView.setImageResource(R.drawable.ic_note_24)
            }
        }

        private fun convertTime(timeStamp: Long): String{
            val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
            return simpleDateFormat.format(timeStamp)
        }

    }

    class DiffCallBack: DiffUtil.ItemCallback<Any>(){
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return if (oldItem is Folder && newItem is Folder) oldItem.id == newItem.id
            else if (oldItem is Note && newItem is Note) oldItem.id == newItem.id
            else false
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return if (oldItem is Folder && newItem is Folder) (oldItem as Folder) == (newItem)
            else if (oldItem is Note && newItem is Note) (oldItem as Note) == newItem
            else false
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is Note -> VIEW_TYPE_NOTE
            is Folder -> VIEW_TYPE_FOLDER
            else -> VIEW_TYPE_NOTE
        }
    }
}