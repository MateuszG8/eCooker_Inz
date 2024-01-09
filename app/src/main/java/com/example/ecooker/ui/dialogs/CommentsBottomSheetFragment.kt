package com.example.ecooker.ui.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecooker.CommentViewModel
import com.example.ecooker.R
import com.example.ecooker.UserViewModel
import com.example.ecooker.adapters.CommentAdapter
import com.example.ecooker.databinding.FragmentCommentsBottomSheetBinding
import com.example.ecooker.models.Comment
import com.example.ecooker.models.CommentRequest
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel


@AndroidEntryPoint
class CommentsBottomSheetFragment : BottomSheetDialogFragment(), CommentActionsListener {



    private lateinit var getComments: Observer<List<Comment>>
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var binding: FragmentCommentsBottomSheetBinding
    private val userViewModel by activityViewModels<UserViewModel>()
    private val commentViewModel: CommentViewModel by viewModels()
    private var recipeId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

       binding = FragmentCommentsBottomSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUserId = userViewModel.userData.value?.userId

        recipeId = arguments?.getString("recipeId") ?: return

        if(recipeId != null){
            val currentRecipeId = recipeId!!
            commentViewModel.getComments(currentRecipeId)
        }

        commentAdapter = CommentAdapter(currentUserId)

        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = commentAdapter

        commentAdapter.setCommentActionsListener(this)

        getComments = Observer { comments ->
            commentAdapter.setComments(comments)
        }

        commentViewModel.comments.observe(viewLifecycleOwner,getComments)


        if (currentUserId == null) {
            binding.addCommentButton.setOnClickListener {
                Toast.makeText(context, "Zaloguj się, aby dodać komentarz", Toast.LENGTH_SHORT).show()
            }
        } else {
            binding.addCommentButton.setOnClickListener {
                binding.etAddCommentLayout.visibility = View.VISIBLE
                binding.etAddComment.visibility = View.VISIBLE
                binding.saveCommentButton.visibility = View.VISIBLE
                binding.cancelAddCommentButton.visibility = View.VISIBLE
            }
        }

        binding.cancelAddCommentButton.setOnClickListener {
            binding.etAddCommentLayout.visibility = View.GONE
            binding.etAddComment.visibility = View.GONE
            binding.saveCommentButton.visibility = View.GONE
            binding.cancelAddCommentButton.visibility = View.GONE
        }

        binding.saveCommentButton.setOnClickListener {
            val comment = binding.etAddComment.text.toString()
            val commentRequest = CommentRequest(comment)
            commentViewModel.addComment(commentRequest,recipeId!!)
            binding.etAddComment.text.clear()
            binding.etAddCommentLayout.visibility = View.GONE
            binding.etAddComment.visibility = View.GONE
            binding.saveCommentButton.visibility = View.GONE
            binding.cancelAddCommentButton.visibility = View.GONE
        }


    }

    override fun onStart() {
        super.onStart()
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
        val behavior = BottomSheetBehavior.from(bottomSheet!!)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.peekHeight = 600
    }

    override fun onSaveComment(commentId: String, editedText: String) {
        val currentRecipeId = recipeId
        if (currentRecipeId != null) {
            val commentRequest = CommentRequest(editedText)
            commentViewModel.updateComment(commentRequest, currentRecipeId, commentId) {
                // Callback po aktualizacji
                onCommentUpdated(commentId)
            }
        } else {
          Log.d("CommentSaveError","recipeId is null")
        }
    }

    override fun onDeleteComment(commentId: String) {
        val currentRecipeId = recipeId
        if (currentRecipeId != null) {
            commentViewModel.deleteComment(currentRecipeId, commentId)
        } else {
            Log.d("CommentDeleteError","recipeId is null")
        }
    }
    override fun onCommentUpdated(commentId: String) {
        val currentRecipeId = recipeId
        if (currentRecipeId != null) {
            // Odśwież listę komentarzy po zaktualizowaniu jednego z nich
            commentViewModel.getComments(currentRecipeId)
        } else {
            Log.d("CommentUpdateError", "recipeId is null")
        }
    }


    override fun onCommentDeleted(commentId: String) {
        // Usuń komentarz z listy i odśwież
        val updatedComments = commentAdapter.getComments().filter { it.commentId != commentId }
        commentAdapter.setComments(updatedComments)
    }

}