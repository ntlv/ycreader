package se.ntlv.newsbringer.newsthreads

import android.animation.Animator
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.widget.ImageView
import org.jetbrains.anko.*
import se.ntlv.newsbringer.BuildConfig
import se.ntlv.newsbringer.Navigator
import se.ntlv.newsbringer.R
import se.ntlv.newsbringer.adapter.DataLoadingFacilitator
import se.ntlv.newsbringer.adapter.ObservableData
import se.ntlv.newsbringer.network.NewsThreadUiData
import kotlin.LazyThreadSafetyMode.NONE

interface NewsThreadsViewBinder {
    fun indicateDataLoading(isLoading: Boolean): Unit

    fun presentData(data: ObservableData<NewsThreadUiData>?)

    fun showStatusMessage(@StringRes messageResource: Int)

    fun showLongStatusMessage(content: String)

    fun toggleDynamicLoading()
}

class NewsThreadsActivity : AppCompatActivity(),
        AppBarLayout.OnOffsetChangedListener,
        NewsThreadsViewBinder,
        AnkoLogger,
        DataLoadingFacilitator {

    val mAdapter: NewsThreadAdapter by lazy(NONE) { NewsThreadAdapter(R.layout.list_item_news_thread, this) }

    private val mAppBar: AppBarLayout by lazy(NONE) { find<AppBarLayout>(R.id.appbar) }

    private val mSwipeView: SwipeRefreshLayout by lazy(NONE) { find<SwipeRefreshLayout>(R.id.swipe_view) }

    private val mRecyclerView: RecyclerView by lazy(NONE) { find<RecyclerView>(R.id.recycler_view) }

    private val mPresenter by lazy(NONE) {
        NewsThreadsPresenter(this, Navigator(this), NewsThreadsInteractor(this, loaderManager))
    }

    //ANDROID ACTIVITY CALLBACKS
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verbose("Get loader manager to avoid loader manager being in stopped state ${loaderManager.toString()}")

        setContentView(R.layout.activity_linear_vertical_content)
        val toolbar = find<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        title = getString(R.string.frontpage)

        mSwipeView.setOnRefreshListener { mPresenter.refreshData(false) }
        mSwipeView.setColorSchemeResources(R.color.accent_color)

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.layoutManager = LinearLayoutManager(this);
        mRecyclerView.adapter = mAdapter

        mAdapter.clickListener = { mPresenter.onItemClick(it?.id) }
        mAdapter.longClickListener = { mPresenter.onItemLongClick(it?.id) }

        find<FloatingActionButton>(R.id.fab).visibility = View.GONE

        mPresenter.onViewReady()
    }

    private var refreshButton: MenuItem? = null

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        refreshButton = menu.findItem(R.id.refresh)
        return true
    }

    override fun onStart() {
        super.onStart()
        mAppBar.addOnOffsetChangedListener(this);
    }

    override fun onStop() {
        super.onStop();
        mAppBar.removeOnOffsetChangedListener(this)
    }

    override fun onDestroy() {
        super.onStop()
        mPresenter.destroy()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.refresh -> {
            mPresenter.refreshData(true); true
        }
        R.id.toggle_show_starred_only -> {
            mPresenter.toggleShowOnlyStarred(); true
        }
        else -> super.onOptionsItemSelected(item)
    }

    //APP BAR CALLBACKS
    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        mSwipeView.isEnabled = 0.equals(verticalOffset)
    }

    //VIEW MODEL IMPLEMENTATION
    override fun presentData(data: ObservableData<NewsThreadUiData>?) = mAdapter.updateContent(data)

    override fun indicateDataLoading(isLoading: Boolean) {
        mSwipeView.isRefreshing = isLoading
        refreshButton?.isEnabled = !isLoading

        if (isLoading) {
            val image = ImageView(this)
            image.minimumWidth = dip(56)
            image.padding = dip(8)
            image.setImageResource(R.drawable.ic_action_refresh)
            image.animate().alpha(0.25f)
            refreshButton?.actionView = image
        } else {
            refreshButton?.actionView?.animate()?.alpha(1.0f)?.withEndAction {
                refreshButton?.actionView = null
            }
        }
    }

    override fun toggleDynamicLoading() = mAdapter.toggleDynamicLoading()

    override fun showStatusMessage(@StringRes messageResource: Int) = toast(messageResource)

    override fun showLongStatusMessage(content: String) {
        if (BuildConfig.DEBUG) alert(content, "CRITIAL ERROR", { show() })
    }

    //ADAPTER DYNAMIC LOADING CALLBACKS
    override fun onMoreDataNeeded(currentMaxItem: Int) = mPresenter.onMoreDataNeeded(currentMaxItem)
}

