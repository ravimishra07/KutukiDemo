package com.ravi.kutukidemo

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ravi.kutukidemo.adapter.CategoriesAdapter
import com.ravi.kutukidemo.model.VideoCategories
import com.ravi.kutukidemo.util.NetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), VideoClickListener {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var progressBar: ProgressBar

    private lateinit var categoriesRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window?.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window?.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        categoriesRecyclerView = findViewById(R.id.rvCategories)
        progressBar = findViewById(R.id.pbCategories)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        getVideoCategories()
    }

    private fun setRecyclerView(categories:List<VideoCategories>){
        val adapter = CategoriesAdapter(categories,this)
        categoriesRecyclerView.setHasFixedSize(true)
        categoriesRecyclerView.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false)
        categoriesRecyclerView.adapter = adapter
    }

    /** get video categories from remote server*/
    private fun getVideoCategories() {
        mainViewModel.getVideoCategories()
        mainViewModel.videoCategoryList.observe(this, { response ->
            when (response) {
                is NetworkResult.Success -> {

                    response.data?.let {
                        showLoader(false)
                        setRecyclerView(it)
                    }
                }
                is NetworkResult.Error -> {
                    showLoader(false)
                    Toast.makeText(this,
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                  showLoader(true)
                }
            }
        })
    }

private fun showLoader(show:Boolean){
    progressBar.visibility =  if(show) View.VISIBLE else View.GONE
}
    /**Open activity with selected index*/
    override fun getVideo(index: Int) {
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra("vidIndex",index)
        startActivity(intent)
    }

}