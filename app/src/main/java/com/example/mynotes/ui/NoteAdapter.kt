package com.example.mynotes.ui

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mynotes.databinding.NoteItemLayoutBinding
import com.example.mynotes.domain.model.Note

class NoteAdapter : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Note> () {
        override fun areItemsTheSame (oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame (oldItem: Note, newItem: Note): Boolean {
            return oldItem.noteContent == newItem.noteContent
        }

    }

    val differ = AsyncListDiffer (this, differCallback)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            NoteItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = differ.currentList[position]
        holder.bind(note)
    }

    class NoteViewHolder(private val binding: NoteItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(note:Note){
                binding.apply {
                    noteItemTitle.text = note.title
                    noteContentTV.text = note.noteContent
                    noteDate.text = note.dateTime
                    val gradientDrawable: GradientDrawable = noteLayoutCard.background as GradientDrawable

                    if(note.color != null){
                        gradientDrawable.setColor(Color.parseColor(note.color))
                    }else {
                        gradientDrawable.setColor(Color.parseColor("#333333"))
                    }

                    if (!note.imagePath.isNullOrEmpty()){
                        imageNote.setImageBitmap(BitmapFactory.decodeFile(note.imagePath))
                        imageNote.visibility = View.VISIBLE
                    }else {
                        imageNote.visibility = View.GONE
                    }
                }
            }

    }
}