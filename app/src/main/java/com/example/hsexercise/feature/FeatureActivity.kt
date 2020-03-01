package com.example.hsexercise.feature

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hsexercise.R
import com.example.hsexercise.common.BaseActivity
import com.example.hsexercise.common.DatabaseProvider
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_feature.*

class FeatureActivity : BaseActivity<FeatureViewModel>() {
    override val viewModelClass = FeatureViewModel::class.java
    override val layoutResId = R.layout.activity_feature

    var compositeDisposable = CompositeDisposable()

    override fun provideViewModelFactory() = FeatureViewModel.Factory()

    override fun onViewLoad(savedInstanceState: Bundle?) {

        picturesRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        compositeDisposable.add(
            viewModel.getPictures().subscribe({picturesList->
                picturesRecyclerView.adapter = PicturesAdapter(picturesList)
                    //DatabaseProvider.provideRoomDatabase(application).featureTableDao().insertAll(picturesList)
            },{
                Log.d("TAG_X",it.message)
            })
        )
    }


}
