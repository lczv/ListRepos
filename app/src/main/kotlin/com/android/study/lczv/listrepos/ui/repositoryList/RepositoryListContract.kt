package com.android.study.lczv.listrepos.ui.repositoryList

import com.android.study.lczv.listrepos.data.model.Repository

interface RepositoryListContract {

    interface View {

        fun displayRepositories(repositories: List<Repository>)

        fun displayError()

    }

    interface Presenter {

        fun onAttach(view: View)

        fun onDetach()

        fun loadRepositories(page: Int, sorting: String)

    }

}
