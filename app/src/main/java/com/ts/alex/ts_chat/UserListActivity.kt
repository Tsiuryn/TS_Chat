package com.ts.alex.ts_chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.ts.alex.ts_chat.ChatActivity.Companion.RECIPIENT_USER_ID
import com.ts.alex.ts_chat.ChatActivity.Companion.USER_NAME
import com.ts.alex.ts_chat.domain.models.User
import com.ts.alex.ts_chat.presenter.screens.list_users.adapter.UserAdapter

class UserListActivity : AppCompatActivity() {

    private lateinit var userDatabaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var userChildEventListener: ChildEventListener? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    private var listUsers = ArrayList<User>()
    private var userName : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        if(intent.getStringExtra(ChatActivity.USER_NAME) != null){
            userName = intent.getStringExtra(ChatActivity.USER_NAME)!!
        }

        auth = FirebaseAuth.getInstance()

        buildRecycler()
        attachDataBaseUserListener()
    }

    private fun attachDataBaseUserListener() {
        userDatabaseReference = FirebaseDatabase.getInstance().reference.child("users")
        if(userChildEventListener == null){
            userChildEventListener = object : ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val user = snapshot.getValue(User::class.java)
                    user?.avatarMockUpResource = R.drawable.user_image
                    if(user != null && user.id != auth.currentUser?.uid){
                        listUsers.add(user)
                        adapter.notifyDataSetChanged()
                    }

                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            }
            userDatabaseReference.addChildEventListener(userChildEventListener as ChildEventListener)
        }
    }

    private fun buildRecycler() {
        recyclerView = findViewById(R.id.userListRecycler)
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = UserAdapter(listUsers, object : UserAdapter.UserClickListener{
            override fun onClick(position: Int) {
                goToChat(position)
            }

        })
        recyclerView.adapter = adapter
    }

    private fun goToChat(position: Int) {
        val intent = Intent (this, ChatActivity::class.java)
        intent.putExtra(RECIPIENT_USER_ID, listUsers[position].id)
        intent.putExtra(USER_NAME, userName)
        startActivity(intent)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.my_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.signOut -> {
                Firebase.auth.signOut()
                startActivity(Intent(this, SignInActivity::class.java))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}