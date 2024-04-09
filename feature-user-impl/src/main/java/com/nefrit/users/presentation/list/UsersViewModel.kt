package com.nefrit.users.presentation.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nefrit.common.base.BaseViewModel
import com.nefrit.common.core.resources.ResourceManager
import com.nefrit.common.utils.Event
import com.nefrit.feature_user_api.domain.interfaces.UserInteractor
import com.nefrit.feature_user_api.domain.model.User
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class UsersViewModel(
    private val interactor: UserInteractor,
    private val resourceManager: ResourceManager,
) : BaseViewModel() {

    private val _usersLiveData = MutableLiveData<List<UsersAdapter.ListItem>>()
    val usersLiveData: LiveData<List<UsersAdapter.ListItem>> = _usersLiveData

    private val _openUserEvent = MutableLiveData<Event<User>>()
    val openUserEvent: LiveData<Event<User>> = _openUserEvent

    init {
        disposables += interactor.observeUsers()
            .subscribeOn(Schedulers.io())
            .map(::mapUsers)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::observeUsersSuccess, ::observeUsersError)
    }

    private fun mapUsers(users: List<User>): List<UsersAdapter.ListItem> {
        val sortedByName = users.sortedBy { it.firstName }
        return addHeaders(sortedByName)
    }

    private fun addHeaders(users: List<User>): List<UsersAdapter.ListItem> {
        val resultList = mutableListOf<UsersAdapter.ListItem>()

        val firstUser = users.firstOrNull() ?: return resultList
        var headerLetter = firstUser.firstName.firstOrNull()?.toString() ?: ""

        val firstHeader = UsersAdapter.ListItem.HeaderListItem(headerLetter)
        resultList.add(firstHeader)

        users.forEach {
            val userListItem = mapUser(it)
            val letter = it.firstName.firstOrNull()?.toString() ?: ""
            if (headerLetter != letter) {
                headerLetter = letter
                val header = UsersAdapter.ListItem.HeaderListItem(letter)
                resultList.add(header)
            }
            resultList.add(userListItem)
        }

        return resultList
    }

    private fun mapUser(user: User): UsersAdapter.ListItem {
        return with(user) {
            UsersAdapter.ListItem.UserListItem(id, firstName, lastName)
        }
    }

    private fun observeUsersSuccess(users: List<UsersAdapter.ListItem>) {
        _usersLiveData.value = users
    }

    private fun observeUsersError(error: Throwable) {
    }

    fun userClicked(userListItem: UsersAdapter.ListItem.UserListItem) {
        val user = with(userListItem) {
            User(id, firstName, lastName)
        }
        _openUserEvent.value = Event(user)
    }

    fun updateUsers() {
        disposables += interactor.updateUsers()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::updateUsersSuccess, ::updateUsersError)
    }

    private fun updateUsersSuccess() {
    }

    private fun updateUsersError(error: Throwable) {
    }
}