package com.nefrit.users.presentation.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.nefrit.common.base.BaseFragment
import com.nefrit.common.di.FeatureUtils
import com.nefrit.common.utils.SimpleEvent
import com.nefrit.feature_user_api.di.UserFeatureApi
import com.nefrit.users.R
import com.nefrit.users.UsersRouter
import com.nefrit.users.databinding.FragmentUserBinding
import com.nefrit.users.di.UserFeatureComponent
import com.nefrit.users.presentation.details.model.UserDetailsModel
import javax.inject.Inject

class UserFragment : BaseFragment<UserViewModel>() {

    companion object {
        private const val KEY_USER_ID = "user_id"

        fun createBundle(userId: Long): Bundle {
            return Bundle().apply { putLong(KEY_USER_ID, userId) }
        }
    }

    @Inject lateinit var router: UsersRouter

    private lateinit var binding: FragmentUserBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun inject() {
        val userId = requireArguments().getLong(KEY_USER_ID, 0)

        FeatureUtils.getFeature<UserFeatureComponent>(this, UserFeatureApi::class.java)
            .userComponentFactory()
            .create(this, userId)
            .inject(this)
    }

    override fun initViews() {
        with(binding.toolbar) {
            setTitle(getString(R.string.user_title))
            setHomeButtonListener { viewModel.backClicked() }
            showHomeButton()
        }
    }

    override fun subscribe(viewModel: UserViewModel) {
        viewModel.userLiveData.observe(::updateUserDetails)
        viewModel.returnToUsersLiveData.observeEvent(::navigateBackToUsers)

        viewModel.updateUser()
    }

    override fun showNavigationBar(): Boolean {
        return false
    }

    private fun updateUserDetails(userDetails: UserDetailsModel) {
        binding.userView.populate(userDetails.userPayload)
    }

    private fun navigateBackToUsers(event: SimpleEvent) {
        router.returnToUsers()
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == android.R.id.home) {
            viewModel.backClicked()
        }
        return super.onOptionsItemSelected(menuItem)
    }
}