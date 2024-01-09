package com.example.ecooker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ecooker.databinding.ItemCommentBinding
import com.example.ecooker.models.Comment
import com.example.ecooker.models.CommentRequest
import com.example.ecooker.ui.dialogs.CommentActionsListener

class CommentAdapter(private val currentUserId: String?) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private var comments = listOf<Comment>()
    private var commentActionsListener: CommentActionsListener? = null

    inner class CommentViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.btnEdit.setOnClickListener {
                with(binding) {
                    tvComment.visibility = View.GONE
                    etCommentLayout.visibility = View.VISIBLE
                    etComment.visibility = View.VISIBLE
                    etComment.setText(comments[adapterPosition].comment)
                    btnEdit.visibility = View.GONE
                    btnSave.visibility = View.VISIBLE
                    btnDelete.visibility = View.VISIBLE
                    btnCancel.visibility = View.VISIBLE

                }
            }

            binding.btnCancel.setOnClickListener {
                with(binding) {
                    tvComment.visibility = View.VISIBLE
                    etCommentLayout.visibility = View.GONE
                    etComment.visibility = View.GONE
                    btnEdit.visibility = View.VISIBLE
                    btnSave.visibility = View.GONE
                    btnDelete.visibility = View.GONE
                    btnCancel.visibility = View.GONE
                }
            }

            binding.btnSave.setOnClickListener {
                val commentId = comments[adapterPosition].commentId
                val editedText = binding.etComment.text.toString()
                commentActionsListener?.onSaveComment(commentId, editedText)
                commentActionsListener?.onCommentUpdated(commentId)
                with(binding) {
                    tvComment.visibility = View.VISIBLE
                    etCommentLayout.visibility = View.GONE
                    etComment.visibility = View.GONE
                    btnEdit.visibility = View.VISIBLE
                    btnSave.visibility = View.GONE
                    btnDelete.visibility = View.GONE
                    btnCancel.visibility = View.GONE
                }
            }

            binding.btnDelete.setOnClickListener {
                val commentId = comments[adapterPosition].commentId
                commentActionsListener?.onDeleteComment(commentId)
                commentActionsListener?.onCommentDeleted(commentId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun getItemCount(): Int = comments.size

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]

        with(holder.binding) {
            tvAuthor.text = comment.user
            tvComment.text = comment.comment

            if ((currentUserId != null && currentUserId == comment.userId)) {
                btnEdit.visibility = View.VISIBLE
            } else {
                btnEdit.visibility = View.GONE
            }
        }
    }

    fun setComments(comments: List<Comment>) {
        this.comments = comments
        notifyDataSetChanged()
    }
    fun setCommentActionsListener(listener: CommentActionsListener) {
        this.commentActionsListener = listener
    }
    fun getComments(): List<Comment> {
        return comments
    }
}