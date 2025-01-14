package com.solid.bankrecipe.ui.recipe

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.solid.bankrecipe.Utils.FBAuth
import com.solid.bankrecipe.databinding.FragmentRecipePostBinding
import com.google.firebase.firestore.FirebaseFirestore

class RecipePostFragment : Fragment() {
    private var _binding: FragmentRecipePostBinding? = null
    private val binding get() = _binding!!
    lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {
        _binding = FragmentRecipePostBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()

        binding.recipePostReg.setOnClickListener {
            if (FBAuth.getDisplayName() == "null")
                Toast.makeText(context, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            else
            {
                Intent(this.context, RecipePostRegisterActivity::class.java).run { context?.startActivity(this) }
            }
        }

        var itemList = ArrayList<RecipePostData>()
        val recipePostAdapter = RecipePostAdapter(this.context, itemList)
        recipePostAdapter.notifyDataSetChanged()

        binding.recipePostRv.adapter = recipePostAdapter
        binding.recipePostRv.layoutManager = LinearLayoutManager(this.context)
        return binding.root
    }
}