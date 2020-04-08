package com.example.studywithme.ui.login

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.example.studywithme.data.LoginDataSource
import com.example.studywithme.data.LoginRepository

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
// viewmodel을 생성하기 위한 팩토리 클래스
// 커스텀 생성자를 갖는 viewmodel은 viewmodelprovider에게 해당 객체를 생성할 수 있는 방법을 제공해야 한다.
// 이런 경우를 다루기 위해 viewmodel 라이브러리는 개발자에게 viewModelProvider.factory인터페이스를 사용하도록 강제하고 있다.
class LoginViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(
                loginRepository = LoginRepository(
                    dataSource = LoginDataSource()
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
