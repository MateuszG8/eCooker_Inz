package com.example.ecooker.ui.dialogs

interface CommentActionsListener {
    fun onSaveComment(commentId: String, editedText: String)
    fun onDeleteComment(commentId: String)
    fun onCommentUpdated(commentId: String)
    fun onCommentDeleted(commentId: String)
}