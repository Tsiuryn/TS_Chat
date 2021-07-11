package com.ts.alex.ts_chat.presenter.screens.sign_in

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.ts.alex.ts_chat.R
import com.ts.alex.ts_chat.USER_NAME
import com.ts.alex.ts_chat.databinding.FragmentSignInBinding
import com.ts.alex.ts_chat.presenter.screens.list_users.ListUsersFragment
import com.ts.alex.ts_chat.presenter.screens.sign_in.util.isValidEmail
import com.ts.alex.ts_chat.presenter.screens.sign_in.util.isValidName
import com.ts.alex.ts_chat.presenter.screens.sign_in.util.isValidPassword
import com.ts.alex.ts_chat.presenter.screens.sign_in.util.isValidRptPsw
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignInFragment : Fragment() {

    private lateinit var binding: FragmentSignInBinding
    private val viewModel by viewModel<SignInViewModel>()
    private lateinit var vName: TextInputLayout
    private lateinit var vEmail: TextInputLayout
    private lateinit var vPsw: TextInputLayout
    private lateinit var vRptPsw: TextInputLayout
    private lateinit var btnSignIn: MaterialButton
    private lateinit var tabLogIn: TextView
    private lateinit var vLogo: ImageView
    private lateinit var vProgress: FrameLayout

    private var isActive = false
    private var userName = ""
    private var userEmail = ""
    private var userPsw = ""
    private var userRptPsw = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkUser()
        bingFields()
        observeVm()
        textChangeListener()
        textOnFocusChangeListener()
    }

    private fun checkUser() {
        if (viewModel.checkUser()) nextPage(userName)
    }

    private fun bingFields() {
        vName = binding.vName
        vEmail = binding.vEmail
        vPsw = binding.vPassword
        vRptPsw = binding.vRptPassword
        btnSignIn = binding.vBtnSignIn
        btnSignIn = binding.vBtnSignIn
        tabLogIn = binding.vTVLogIn
        vLogo = binding.vLogo
        vProgress = binding.vProgress
        setUpClick()
    }

    private fun setUpClick() {
        btnSignIn.setOnClickListener {
            vProgress.visibility = View.VISIBLE
            if (validateFields()) {
                Toast.makeText(requireContext(), "All fields are valid", Toast.LENGTH_SHORT).show()
                if (!isActive) {
                    viewModel.createUser(name = userName, email = userEmail, password = userPsw)
                } else {
                    viewModel.signInUser(password = userPsw, email = userEmail)
                }
            }

        }
        tabLogIn.setOnClickListener {
            tabOnClick()
        }

        vLogo.setOnLongClickListener {
            if (!isActive) {
                viewModel.createUser(
                    name = "Garold",
                    email = "test@test.com",
                    password = "123456"
                )
            } else {
                viewModel.signInUser(
                    password = "123456",
                    email = "test@test.com"
                )
            }
            return@setOnLongClickListener true
        }
    }

    private fun tabOnClick() {
        if (isActive) {
            btnSignIn.text = getString(R.string.log_in)
            tabLogIn.text = getString(R.string.or_sign_in)
            vRptPsw.visibility = View.VISIBLE
            isActive = false
        } else {
            btnSignIn.text = getString(R.string.sign_in)
            tabLogIn.text = getString(R.string.or_log_in)
            vRptPsw.visibility = View.GONE
            isActive = true
        }
    }

    private fun observeVm() {
        lifecycleScope.launchWhenStarted {
            viewModel.registrationResult.collect { task ->
                if (task.isSuccess) {
                    Toast.makeText(requireContext(), "Registration is Success!", Toast.LENGTH_SHORT)
                        .show()
                    vProgress.visibility = View.GONE
                    nextPage(userName)
                } else {
                    vProgress.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Registration failed: ${task.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun nextPage(userName: String) {
        val listUsersFragment = ListUsersFragment()
        val bundle = Bundle()
        bundle.putString(USER_NAME, userName)
        listUsersFragment.arguments = bundle
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.vMainContainer, listUsersFragment).commit()
    }

    private fun textChangeListener() {
        vName.editText!!.doAfterTextChanged {
            userName = it.toString()
        }
        vEmail.editText!!.doAfterTextChanged {
            userEmail = it.toString()
        }
        vPsw.editText!!.doAfterTextChanged {
            userPsw = it.toString()
        }
        vRptPsw.editText!!.doAfterTextChanged {
            userRptPsw = it.toString()
        }
    }

    private fun textOnFocusChangeListener() {
        vName.editText!!.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                vName.error = ""
            } else {
                setErrorName()
            }
        }
        vEmail.editText!!.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                vEmail.error = ""
            } else {
                setErrorEmail()
            }
        }
        vPsw.editText!!.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                vPsw.error = ""
            } else {
                setErrorPsw()
            }
        }
        vRptPsw.editText!!.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                vRptPsw.error = ""
            } else {
                setErrorRptPsw()
            }
        }
    }

    private fun setErrorName(): Boolean {
        val isValidName = isValidName(userName)
        if (!isValidName) {
            vName.error = getString(R.string.error_name)
        } else vName.error = ""
        return isValidName
    }

    private fun setErrorEmail(): Boolean {
        val isValidEmail = isValidEmail(userEmail)
        if (!isValidEmail) {
            vEmail.error = getString(R.string.error_email)
        } else vEmail.error = ""
        return isValidEmail
    }

    private fun setErrorPsw(): Boolean {
        val isValidPassword = isValidPassword(userPsw)
        if (!isValidPassword) {
            vPsw.error = getString(R.string.error_psw)
        } else vPsw.error = ""
        return isValidPassword
    }

    private fun setErrorRptPsw(): Boolean {
        if (isActive) return true
        val isValidRptPsw = isValidRptPsw(userPsw, userRptPsw)
        if (!isValidRptPsw) {
            vRptPsw.error = getString(R.string.error_psw)
        } else vRptPsw.error = ""
        return isValidRptPsw
    }

    private fun validateFields() = setErrorName()
            && setErrorEmail() && setErrorPsw() && setErrorRptPsw()
}