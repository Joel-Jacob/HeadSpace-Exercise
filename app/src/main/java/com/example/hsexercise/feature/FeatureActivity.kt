package com.example.hsexercise.feature

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hsexercise.R
import com.example.hsexercise.common.BaseActivity
import com.example.hsexercise.feature.database.FeatureModel
import com.google.android.material.snackbar.Snackbar
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_feature.*

class FeatureActivity : BaseActivity<FeatureViewModel>() {
    override val viewModelClass = FeatureViewModel::class.java
    override val layoutResId = R.layout.activity_feature

    var compositeDisposable = CompositeDisposable()
    lateinit var picturesList: MutableList<FeatureModel>
    lateinit var sharedPreferences: SharedPreferences
    val emptyList = arrayListOf<FeatureModel>()
    var recyclerViewPosition = 0
    var atBottom = false
    var page = 1
    val KEY_NAME = "PAGE"


    val countDownTimer = object : CountDownTimer(5000, 5000) {
        override fun onTick(millisUntilFinished: Long) {
        }

        override fun onFinish() {
            atBottom = false
        }

    }

    override fun provideViewModelFactory() = FeatureViewModel.Factory(application)

    override fun onViewLoad(savedInstanceState: Bundle?) {
        spinnerLayout.visibility = View.VISIBLE
        sharedPreferences = SharedPreferences(this)

        page = sharedPreferences.getPageSharedPref(KEY_NAME)

        if (page > 1)
            getRoomPictures()
        else
            getPictures()

        picturesRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }

    private fun getRoomPictures() {
        compositeDisposable.add(
            viewModel.getRoomItems().subscribe(
                {
                    picturesList = it as MutableList<FeatureModel>
                    setUpRecyclerView(picturesList)
                    spinnerLayout.visibility = View.GONE
                }, {
                    Log.d("TAG_X", it.message)
                    Snackbar.make(
                        picturesRecyclerView,
                        "Loading Pictures Failed From Storage, Pull Down To Retry ",
                        Snackbar.LENGTH_LONG
                    ).show()
                    setUpRecyclerView(emptyList)
                    spinnerLayout.visibility = View.GONE
                }
            )
        )
    }

    private fun getPictures() {
        compositeDisposable.add(
            viewModel.getPicturesPage(page).subscribe({

                if (::picturesList.isInitialized) {
                    picturesList.addAll(it)
                } else
                    picturesList = it as MutableList<FeatureModel>

                //viewModel.insertList(picturesList, application)
                //DatabaseProvider.provideRoomDatabase(application).featureTableDao().insert(picturesList[0])

                setUpRecyclerView(picturesList)
                spinnerLayout.visibility = View.GONE
            }, {
                Log.d("TAG_X", it.message)
                Snackbar.make(
                    picturesRecyclerView,
                    "Loading Pictures Failed, Pull Down To Retry",
                    Snackbar.LENGTH_LONG
                ).show()
                --page
                setUpRecyclerView(emptyList)
                spinnerLayout.visibility = View.GONE
            })
        )
    }

    private fun setUpRecyclerView(picturesList: List<FeatureModel>) {
        picturesRecyclerView.adapter = PicturesAdapter(picturesList)

        picturesRecyclerView.scrollToPosition(recyclerViewPosition)

        val onScrollListener: RecyclerView.OnScrollListener =
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if(isOnline()) {
                        if (!recyclerView.canScrollVertically(1) && !atBottom) {
                            page++
                            atBottom = true
                            recyclerViewPosition = picturesList.size - 2
                            countDownTimer.start()
                            getPictures()
                            spinnerLayout.visibility = View.VISIBLE

                        }
                    }else if((!recyclerView.canScrollVertically(1) && !atBottom)){
                        Snackbar.make(
                            picturesRecyclerView,
                            "Check Internet Connectivity",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
        picturesRecyclerView.addOnScrollListener(onScrollListener)
    }

    fun isOnline(): Boolean {
        val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
        return networkInfo?.isConnected == true
    }

    override fun onPause() {
        super.onPause()
        sharedPreferences.savePageSharedPref(KEY_NAME, page)
    }

    override fun onStop() {
        super.onStop()
        countDownTimer.cancel()
        compositeDisposable.dispose()
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}
