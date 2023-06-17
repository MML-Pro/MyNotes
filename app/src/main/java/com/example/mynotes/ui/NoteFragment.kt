package com.example.mynotes.ui

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.mynotes.R
import com.example.mynotes.databinding.FragmentNoteBinding
import com.example.mynotes.util.Resource
import com.example.mynotes.util.onItemClick
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Timer

private const val TAG = "NoteFragment"

@AndroidEntryPoint
class NoteFragment : Fragment() {

    private lateinit var binding: FragmentNoteBinding

    private val noteViewModel by activityViewModels<NoteViewModel>()

    private lateinit var noteAdapter: NoteAdapter

    private var staggeredGridLayoutManager: StaggeredGridLayoutManager? = null

//    private var menuHost: MenuHost? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNoteBinding.inflate(inflater)

//        menuHost = requireActivity()
//        menuHost?.addMenuProvider(this,viewLifecycleOwner,Lifecycle.State.STARTED)

        noteAdapter = NoteAdapter()


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteViewModel.getAllNotes()

        binding.NotesRV.apply {
            staggeredGridLayoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).also {
                    it.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
                }
            adapter = noteAdapter
            layoutManager = staggeredGridLayoutManager

        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                noteViewModel.getAllNotesAsFlow.collect { resourceResult ->

                    when (resourceResult) {
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            resourceResult.data?.let {
                                noteAdapter.differ.submitList(it)
                            }

                        }

                        is Resource.Error -> {

                        }

                        else -> {}
                    }

                }
            }
        }

        binding.addNoteFAB.setOnClickListener {
            findNavController().navigate(NoteFragmentDirections.actionNoteFragmentToSaveOrDeleteFragment())
        }

        binding.NotesRV.onItemClick { _, position, _ ->

            val note = noteAdapter.differ.currentList[position]

            findNavController().navigate(
                NoteFragmentDirections.actionNoteFragmentToSaveOrDeleteFragment(
                    note
                )
            )

        }

        binding.searchNotesED.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {


                if (noteAdapter.differ.currentList.isNotEmpty() && s.toString().isNotEmpty()) {
                    noteViewModel.searchNote(s.toString())

                }else if(s.toString().trim().isEmpty()){
                    noteViewModel.getAllNotes()
                }
            }

        })


        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                noteViewModel.searchNoteAsFlow.collect { searchResult ->

                    when (searchResult) {
                        is Resource.Loading -> {}
                        is Resource.Success -> {

                            if(searchResult.data == null){
                                Toast.makeText(
                                    requireContext(),
                                    "There's no notes with this query",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            searchResult.data?.let {

                                if (it.isNotEmpty()) {
                                    noteAdapter.differ.submitList(null)
                                    noteAdapter.differ.submitList(it)
                                }
                            }

                        }

                        is Resource.Error -> {
                            Toast.makeText(
                                requireContext(),
                                searchResult.message.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        else -> {}
                    }

                }
            }
        }


    }

//    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//
//        menuInflater.inflate(R.menu.search_menu,menu)
//
//        val searchManager =
//            requireContext().getSystemService(Context.SEARCH_SERVICE) as SearchManager
//        val searchView = menu.findItem(R.id.app_bar_search).actionView as SearchView
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
//        searchView.queryHint = resources.getString(R.string.search_for_notes)
//
//        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                if (query.isNullOrEmpty()) {
//                    Snackbar.make(
//                        requireView(),
//                        "please enter keyword to search",
//                        Snackbar.LENGTH_SHORT
//                    ).show()
//                }else{
//                    if (noteAdapter.differ.currentList.isNotEmpty()) {
//                        noteViewModel.searchNote(query)
//                    }
//                }
//
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//               return false
//            }
//
//        })
//    }
//
//    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//        return false
//    }


}