package com.cloudlevi.cloudnote.ui.main

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.cloudlevi.cloudnote.*
import com.cloudlevi.cloudnote.data.Folder
import com.cloudlevi.cloudnote.data.Note
import com.cloudlevi.cloudnote.databinding.ListNoteItemListviewBinding
import com.cloudlevi.cloudnote.databinding.ListNoteItemGridviewBinding
import java.lang.ClassCastException
import java.text.SimpleDateFormat

class NotesAdapter(private val receivedViewType: Int, private val listener: ItemClickListener) :
    ListAdapter<Any, RecyclerView.ViewHolder>(DiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_FOLDER -> {
                val binding =
                    if (receivedViewType == HOME_TYPE_LISTVIEW) ListNoteItemListviewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    else ListNoteItemGridviewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )

                FoldersViewHolder(binding, receivedViewType)
            }
            VIEW_TYPE_NOTE -> {
                val binding =
                    if (receivedViewType == HOME_TYPE_LISTVIEW) ListNoteItemListviewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    else ListNoteItemGridviewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )

                NotesViewHolder(binding, receivedViewType)
            }
            else -> {
                val binding =
                    if (receivedViewType == HOME_TYPE_LISTVIEW) ListNoteItemListviewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    else ListNoteItemGridviewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )

                NotesViewHolder(binding, receivedViewType)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = getItem(position)

        when (getItemViewType(position)) {
            VIEW_TYPE_NOTE -> {
                (holder as NotesViewHolder).bind(currentItem as Note)
            }

            VIEW_TYPE_FOLDER -> {
                (holder as FoldersViewHolder).bind(currentItem as Folder)
            }
        }
    }

    inner class FoldersViewHolder(
        private val binding: ViewBinding, private val viewType: Int
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(folder: Folder) {
            when (viewType) {
                HOME_TYPE_LISTVIEW -> {
                    (binding as ListNoteItemListviewBinding).apply {
                        noteTitleTV.text = folder.title
                        iconImageView.setImageResource(R.drawable.ic_folder)

                        root.setOnClickListener {
                            listener.OnFolderClickListener(folder)
                        }
                    }
                }

                HOME_TYPE_GRIDVIEW -> {
                    (binding as ListNoteItemGridviewBinding).apply {
                        noteTitleTV.text = folder.title
                        noteTitleTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_folder, 0,0,0)
                        noteTitleTV.maxLines = 2
                        noteDescriptionTV.visibility = View.GONE
                        noteDateTV.visibility = View.GONE

                        root.setOnClickListener {
                            listener.OnFolderClickListener(folder)
                        }
                    }
                }
            }
        }
    }

    inner class NotesViewHolder(
        private val binding: ViewBinding, private val viewType: Int
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note) {
            when (viewType) {
                HOME_TYPE_LISTVIEW -> {

                    (binding as ListNoteItemListviewBinding).apply {

                        if (note.pinned) pinnedImageView.visibility = View.VISIBLE
                        else pinnedImageView.visibility = View.GONE

                        iconImageView.visibility = View.VISIBLE

                        if (note.password.isNotEmpty()){
                            if (note.hideTitle) {
                                iconImageView.visibility = View.GONE
                                noteTitleTV.visibility = View.GONE

                                passwordIcon.post {
                                    val layoutParamsRelative = passwordIcon.layoutParams as RelativeLayout.LayoutParams
                                    layoutParamsRelative.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                                    layoutParamsRelative.marginStart = 0
                                    passwordIcon.layoutParams = layoutParamsRelative
                                    passwordIcon.visibility = View.VISIBLE
                                }
                                pinnedImageView.visibility = View.GONE
                            } else passwordIcon.visibility = View.VISIBLE
                            noteDateTV.visibility = View.GONE

                        } else passwordIcon.visibility = View.GONE

                        noteTitleTV.text = note.title
                        noteDateTV.text = convertTime(note.date_modified)
                        iconImageView.setImageResource(R.drawable.ic_note)

                        root.setOnClickListener {
                            listener.OnNoteClickListener(note)
                        }
                    }
                }

                HOME_TYPE_GRIDVIEW -> {
                    (binding as ListNoteItemGridviewBinding).apply {

                        if (note.pinned) pinnedImageView.visibility = View.VISIBLE
                        else pinnedImageView.visibility = View.GONE

                        if (note.password.isNotEmpty()){
                            if (note.hideTitle) {
                                noteTitleTV.visibility = View.GONE
                                pinnedImageView.visibility = View.GONE

                                passwordIcon.post {
                                    val layoutParams = passwordIcon.layoutParams as RelativeLayout.LayoutParams
                                    layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                                    passwordIcon.layoutParams = layoutParams
                                    passwordIcon.visibility = View.VISIBLE
                                }
                            } else passwordIcon.visibility = View.VISIBLE
                            noteDescriptionTV.visibility = View.GONE
                            noteDateTV.visibility = View.GONE
                        } else passwordIcon.visibility = View.GONE

                        noteTitleTV.text = note.title
                        noteDateTV.text = convertTime(note.date_modified)
                        noteTitleTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_note, 0,0,0)
                        noteDescriptionTV.text = note.description
                        if (note.description == "") {
                            noteDescriptionTV.visibility = View.GONE
                            noteTitleTV.maxLines = 2
                        }
                        else noteDescriptionTV.maxLines = 2

                        root.setOnClickListener {
                            listener.OnNoteClickListener(note)
                        }
                    }
                }

            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Note -> VIEW_TYPE_NOTE
            is Folder -> VIEW_TYPE_FOLDER
            else -> VIEW_TYPE_NOTE
        }
    }

    private fun convertTime(timeStamp: Long): String {
        val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
        return simpleDateFormat.format(timeStamp)
    }

}

class DiffCallBack : DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        return if (oldItem is Folder && newItem is Folder) oldItem.id == newItem.id
        else if (oldItem is Note && newItem is Note) oldItem.id == newItem.id
        else false
    }

    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        return if (oldItem is Folder && newItem is Folder) (oldItem as Folder) == newItem
        else if (oldItem is Note && newItem is Note) (oldItem as Note) == newItem
        else false
    }

}

interface ItemClickListener{
    fun OnFolderClickListener(folder: Folder)
    fun OnNoteClickListener(note: Note)
}