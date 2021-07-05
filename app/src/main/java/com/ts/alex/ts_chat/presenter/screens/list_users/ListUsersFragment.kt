package com.ts.alex.ts_chat.presenter.screens.list_users

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ts.alex.ts_chat.ChatActivity
import com.ts.alex.ts_chat.R
import com.ts.alex.ts_chat.SignInActivity
import com.ts.alex.ts_chat.databinding.FragmentListUsersBinding
import com.ts.alex.ts_chat.domain.models.User
import com.ts.alex.ts_chat.presenter.screens.RECIPIENT_USER_ID
import com.ts.alex.ts_chat.presenter.screens.USER_NAME
import com.ts.alex.ts_chat.presenter.screens.chat.ChatFragment
import com.ts.alex.ts_chat.presenter.screens.list_users.adapter.UserAdapter
import com.ts.alex.ts_chat.presenter.screens.sign_in.SignInFragment
import com.ts.alex.ts_chat.presenter.screens.sign_in.SignInViewModel
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class ListUsersFragment: Fragment() {

    private lateinit var binding: FragmentListUsersBinding
    private val viewModel: ListUsersViewModel by viewModel()

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    private var listUsers = ArrayList<User>()
    private var userName = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListUsersBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        userName = requireArguments().getString(USER_NAME)?: ""
        buildRecycler()
        observeVM()
        viewModel.getUsers()

    }

    private fun observeVM() {
        lifecycleScope.launchWhenStarted {
            viewModel.users.collect {
                listUsers.add(it)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun buildRecycler() {
        recyclerView = binding.userListRecycler
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = UserAdapter(listUsers, object : UserAdapter.UserClickListener{
            override fun onClick(position: Int) {
                goToChat(position)
            }

        })
        recyclerView.adapter = adapter
    }

    private fun goToChat(position: Int) {
        val chatFragment = ChatFragment()
        val bundle = Bundle()
        bundle.putString(RECIPIENT_USER_ID, listUsers[position].id)
        bundle.putString(USER_NAME, userName)
        chatFragment.arguments = bundle
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.vMainContainer, chatFragment).addToBackStack(null).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.my_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.signOut -> {
                viewModel.signOut()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.vMainContainer, SignInFragment()).commit()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}