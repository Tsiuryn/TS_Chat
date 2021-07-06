package com.ts.alex.ts_chat.presenter.screens.chat

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.ts.alex.ts_chat.databinding.FragmentChatBinding
import com.ts.alex.ts_chat.domain.models.Message
import com.ts.alex.ts_chat.presenter.screens.MIMETYPE_IMAGES
import com.ts.alex.ts_chat.presenter.screens.RC_IMAGE_PICKER
import com.ts.alex.ts_chat.presenter.screens.RECIPIENT_USER_ID
import com.ts.alex.ts_chat.presenter.screens.USER_NAME
import com.ts.alex.ts_chat.presenter.screens.chat.adapter.MessageAdapter
import kotlinx.coroutines.flow.collect
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

    private val getContent: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { imageUri: Uri? ->
            if (imageUri != null) {
                viewModel.uploadImage(
                    userName = userName,
                    recipient = recipient,
                    selectImageUri = imageUri
                )
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeVm()

        userName = requireArguments().getString(USER_NAME) ?: ""
        recipient = requireArguments().getString(RECIPIENT_USER_ID) ?: ""
        recyclerView = binding.vListMessagesRecycler
        buildMessageList()

        sendImageButton = binding.vInsertPhoto
        sendMessageButton = binding.vSendButton
        inputText = binding.vInputText

        inputText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(100))

        sendImageButton.setOnClickListener {
//            getImage()
            /**
             * get image with new api
             */
            getImageNewApi()
        }

        sendMessageButton.setOnClickListener {
            sendMessage(it)
        }

        viewModel.setUpUserChildEventListener()
        viewModel.setUpMessageChildEventListener(recipient)
    }

    private fun getImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/jpeg"
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        startActivityForResult(
            Intent.createChooser(intent, "Choose an image"),
            RC_IMAGE_PICKER
        )
    }

    private fun getImageNewApi() {
        getContent.launch(MIMETYPE_IMAGES)
    }

    private fun sendMessage(view: View) {
        if (inputText.text.toString().isNotEmpty()) {

            viewModel.sendMessage(
                Message(
                    name = userName,
                    text = inputText.text.toString(),
                    sender = null,
                    recipient = recipient,
                    imageUrl = null
                )
            )

            inputText.setText("")
        } else {
            Snackbar.make(view, "Message cant'b be empty", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun observeVm() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeUserName.collect { name ->
                userName = name
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.observeMessages.collect { message: Message ->
                listMessages.add(message)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun buildMessageList() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = MessageAdapter(listMessages)
        recyclerView.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_IMAGE_PICKER && resultCode == AppCompatActivity.RESULT_OK) {
            val selectImageUri = data?.data
            if (selectImageUri != null) {
                viewModel.uploadImage(
                    userName = userName,
                    recipient = recipient,
                    selectImageUri = selectImageUri
                )
            }
        }
    }
}