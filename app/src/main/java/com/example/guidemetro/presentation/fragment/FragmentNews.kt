package com.example.guidemetro.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.guidemetro.presentation.adapter.NewsAdapter
import com.example.guidemetro.domain.model.NewsItem
import com.example.guidemetro.R
import com.example.guidemetro.databinding.FragmentNewsBinding

class FragmentNews : Fragment() {
    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val newsList = listOf(
            // Заполнение листа
            NewsItem(R.drawable.train, "News Info 1"),
            NewsItem(R.drawable.train, "News Info 2"),
            NewsItem(R.drawable.train, "News Info 3"),
            NewsItem(R.drawable.train, "News Info 4"),
            NewsItem(R.drawable.train, "News Info 5"),
            NewsItem(R.drawable.train, "News Info 6")
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = NewsAdapter(newsList)
        }
    }
}