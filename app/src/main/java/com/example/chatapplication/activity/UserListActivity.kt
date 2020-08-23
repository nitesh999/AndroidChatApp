package com.example.chatapplication.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapplication.R
import com.example.chatapplication.adapter.UserListAdapter
import com.example.chatapplication.databinding.ActivityUserListBinding
import com.example.chatapplication.db.User
import com.example.chatapplication.viewmodels.UserListViewModel


class UserListActivity : AppCompatActivity(){

    lateinit var listViewModel: UserListViewModel
    private var searchView: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityUserListBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_list)

        listViewModel = ViewModelProviders.of(this, UserListViewModel.Factory()).get(UserListViewModel::class.java)
        // Set the lifecycleOwner so DataBinding can observe LiveData
        binding.lifecycleOwner = this
        binding.viewModel = listViewModel
        binding.executePendingBindings()

        val userListAdapter = UserListAdapter(onUserClick)
        binding.rvUser.apply{
            layoutManager = LinearLayoutManager(application)
            adapter = userListAdapter
        }

        initObservables(binding, userListAdapter)
    }

    private fun initObservables(binding: ActivityUserListBinding, userListAdapter: UserListAdapter) {
        listViewModel.userList.observe(this, Observer<List<User>> { userList ->
            //searchView?.onActionViewExpanded()
            binding.progressBar.visibility = View.GONE
            binding.tvUserStatus.visibility = View.GONE
            listViewModel.getFilteredListByEmailOrName("")
        })

        //viewModel.getFilteredListByEmailOrName("")
        listViewModel.userListUI.observe(this, Observer<List<User>> { userList ->
            userList?.apply {
                userListAdapter.userList = userList
            }
        })

        // Observer for the network error.
        listViewModel.eventNetworkError.observe(this, Observer<Boolean> { isNetworkError ->
            if (isNetworkError) onNetworkError()
        })
    }

    /**
     * Method for displaying a Toast error message for network errors.
     */
    private fun onNetworkError() {
        if(!listViewModel.isNetworkErrorShown.value!!) {
            Toast.makeText(this, "Network Error", Toast.LENGTH_LONG).show()
            listViewModel.onNetworkErrorShown()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_searchview, menu)
        val searchItem = menu!!.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        searchView?.setSubmitButtonEnabled(true)
        searchView?.setFocusable(true);
        searchView?.requestFocusFromTouch();
        searchView?.setQueryHint("Search either - MindOrks, GetMeAnApp, BestContentApp, Hackerspace")
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                listViewModel.getFilteredListByEmailOrName(newText)
                return true
            }
            override fun onQueryTextSubmit(query: String): Boolean {
                listViewModel.getFilteredListByEmailOrName(query)
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    /*fun getFilteredListByEmailOrName(searchText: String): LiveData<List<User>> {
        return Transformations.switchMap(viewModel.userList, it.filter { it.name.contains(searchText) })
    }*/

    /**
     * Click listener for Videos. By giving the block a name it helps a reader understand what it does.
     *
     */
    /*class UserClick(val block: (User) -> Unit) {
        *//**
         * Called when a user is clicked
         *
         * @param user the user that was clicked
         *//*
        fun onClick(user: User) = block(user)
    }*/

    val onUserClick: (User) -> Unit = { user ->
        startActivity(Intent(this, ChatActivity::class.java).putExtra("toUserId", user._id))
    }

}
