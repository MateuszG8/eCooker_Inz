package com.example.ecooker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecooker.R
import com.example.ecooker.models.Ingredient


interface IngredientActions {
    fun onEdit(position: Int)
    fun onDelete(position: Int)
}

class IngredientAdapter(
    private var ingredientList: List<Ingredient>,
    private val ingredientActions: IngredientActions
) : RecyclerView.Adapter<IngredientAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.ingredientNameTV)
        val amount: TextView = itemView.findViewById(R.id.ingredientAmountTV)
        val unit: TextView = itemView.findViewById(R.id.ingredientUnitTV)
        val edit: ImageView = itemView.findViewById(R.id.editIngredientIV)
        val delete: ImageView = itemView.findViewById(R.id.deleteIngredientIV)

        init {
            edit.setOnClickListener {
                // Handle edit action
            }

            delete.setOnClickListener {
                // Handle delete action
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ingredient_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ingredient = ingredientList[position]
        holder.name.text = ingredient.name
        holder.amount.text = ingredient.ammount.toString()
        holder.unit.text = ingredient.unit

        holder.edit.setOnClickListener { ingredientActions.onEdit(position) }
        holder.delete.setOnClickListener { ingredientActions.onDelete(position) }
    }

    override fun getItemCount() = ingredientList.size

    fun submitList(newList: List<Ingredient>) {
        ingredientList = newList
        notifyDataSetChanged()
    }
}
