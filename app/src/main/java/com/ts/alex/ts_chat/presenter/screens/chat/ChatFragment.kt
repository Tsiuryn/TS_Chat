package com.ts.alex.ts_chat.presenter.screens.chat

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.InputFilter
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.ts.alex.ts_chat.ChatActivity
import com.ts.alex.ts_chat.R
import com.ts.alex.ts_chat.databinding.FragmentChatBinding
import com.ts.alex.ts_chat.domain.models.Message
import com.ts.alex.ts_chat.presenter.screens.RECIPIENT_USER_ID
import com.ts.alex.ts_chat.presenter.screens.USER_NAME
import com.ts.alex.ts_chat.presenter.screens.chat.adapter.MessageAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatFragment : Fragment() {

    private val viewModel: ChatViewModel by viewModel()
    private lateinit var binding: FragmentChatBinding

    private lateinit var sendImageButton: ImageView
    private lateinit var sendMessageButton: ImageView
    private lateinit var inputText: EditText
    private lateinit var progress: ProgressBar

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessageAdapter
    private var listMessages = ArrayList<Message>()

    private var userName: String = "Default User"
    private var recipient: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userName = requireArguments().getString(USER_NAME)?:""
        recipient = requireArguments().getString(RECIPIENT_USER_ID)?:""
        recyclerView = binding.vListMessagesRecycler
        buildMessageList()

        sendImageButton = binding.vInsertPhoto
        sendMessageButton = binding.vSendButton
        inputText = binding.vInputText

        inputText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(100))

        sendImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/jpeg"
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(
                Intent.createChooser(intent, "Choose an image"),
                ChatActivity.RC_IMAGE_PICKER
            )

        }
        sendMessageButton.setOnClickListener {
            if (inputText.text.toString().isNotEmpty()) {

                viewModel.sendMessage(Message(
                    userName,
                    inputText.text.toString(),
                    auth.currentUser?.uid,
                    recipient,
                    null
                ))

                messagesDatabaseReference.push().setValue(message)
                inputText.setText("")
            } else {
                Snackbar.make(it, "Message cant'b be empty", Snackbar.LENGTH_SHORT).show()
            }
            setUpChildEventListener()

        }
    }

    private fun buildMessageList() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MessageAdapter(listMessages)
        recyclerView.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ChatActivity.RC_IMAGE_PICKER && resultCode == AppCompatActivity.RESULT_OK) {
            val selectImageUri = data?.data
            if (selectImageUri != null) {
                val imageReference: StorageReference =
                    storageReference.child(selectImageUri.lastPathSegment!!)
                val uploadTask: UploadTask = imageReference.putFile(selectImageUri)


                val urlTask = uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    imageReference.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        val messages = Message()
                        messages.imageUrl = downloadUri.toString()
                        messages.name = userName
                        messages.sender = auth.currentUser?.uid
                        messages.recipient = recipient
                        messagesDatabaseReference.push().setValue(messages)

                    } else {
                        // Handle failures
                        // ...
                    }
                }

            }
        }
    }

}