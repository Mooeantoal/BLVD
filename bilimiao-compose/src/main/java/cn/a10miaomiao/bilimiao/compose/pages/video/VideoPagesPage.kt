package cn.a10miaomiao.bilimiao.compose.pages.video

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.fragment.app.Fragment
import bilibili.app.archive.v1.Page
import bilibili.app.view.v1.ViewGRPC
import bilibili.app.view.v1.ViewPage
import bilibili.app.view.v1.ViewReq
import bilibili.polymer.app.search.v1.Item.CardItem
import bilibili.polymer.app.search.v1.SearchGRPC
import bilibili.polymer.app.search.v1.Space
import cn.a10miaomiao.bilimiao.compose.base.ComposePage
import cn.a10miaomiao.bilimiao.compose.common.diViewModel
import cn.a10miaomiao.bilimiao.compose.common.flow.stateMap
import cn.a10miaomiao.bilimiao.compose.common.localContainerView
import cn.a10miaomiao.bilimiao.compose.common.mypage.PageConfig
import cn.a10miaomiao.bilimiao.compose.common.navigation.PageNavigation
import cn.a10miaomiao.bilimiao.compose.common.toPaddingValues
import cn.a10miaomiao.bilimiao.compose.components.status.BiliFailBox
import cn.a10miaomiao.bilimiao.compose.components.status.BiliLoadingBox
import com.a10miaomiao.bilimiao.comm.delegate.player.BasePlayerDelegate
import com.a10miaomiao.bilimiao.comm.delegate.player.VideoPlayerSource
import com.a10miaomiao.bilimiao.comm.entity.bangumi.EpisodeInfo
import com.a10miaomiao.bilimiao.comm.network.BiliGRPCHttp
import com.a10miaomiao.bilimiao.comm.store.PlayerStore
import com.a10miaomiao.bilimiao.comm.utils.NumberUtil
import com.a10miaomiao.bilimiao.store.WindowStore
import com.kongzue.dialogx.dialogs.PopTip
import cn.a10miaomiao.bilimiao.compose.pages.video.components.VideoDownloadDialog
import cn.a10miaomiao.bilimiao.compose.pages.video.components.VideoDownloadDialogState
import cn.a10miaomiao.bilimiao.download.DownloadService
import com.a10miaomiao.bilimiao.comm.network.BiliApiService
import cn.a10miaomiao.bilimiao.download.entry.BiliDownloadEntryInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.compose.rememberInstance
import org.kodein.di.instance

@Serializable
class VideoPagesPage(
    val aid: String,
) : ComposePage() {

    @Composable
    override fun Content() {
        val viewModel: VideoPagesPageViewModel = diViewModel(
            key = aid,
        ) {
            VideoPagesPageViewModel(it, aid)
        }
        VideoPagesPageContent(viewModel)
    }
}

private class VideoPagesPageViewModel(
    override val di: DI,
    val aid: String,
) : ViewModel(), DIAware {

    private val pageNavigation by instance<PageNavigation>()
    private val playerStore by instance<PlayerStore>()
    private val basePlayerDelegate by instance<BasePlayerDelegate>()
    private val fragment by instance<Fragment>()

    val loading = MutableStateFlow(false)
    val fail = MutableStateFlow<String?>(null)
    val pages = MutableStateFlow(listOf<Page>())

    var arcInfo: bilibili.app.archive.v1.Arc? = null
    var bvid: String = ""
    val currentPlay: StateFlow<PlayerStore.State> get() = playerStore.stateFlow

    val downloadDialogState = VideoDownloadDialogState(scope = viewModelScope)

    init {
        loadPages()
    }

    fun loadPages() = viewModelScope.launch(Dispatchers.IO) {
        try {
            loading.value = true
            fail.value = null
            val req = ViewReq(
                aid = aid.toLong()
            )
        val result = BiliGRPCHttp.request {
            ViewGRPC.view(req)
        }.awaitCall()
        arcInfo = result.arc
        bvid = result.bvid
        pages.value = result.pages.map {
            it.page
        }.filterNotNull()
        } catch (e: Exception) {
            e.printStackTrace()
            fail.value = e.message ?: e.toString()
        } finally {
            loading.value = false
        }
    }

    fun startPlayVideo(page: Page) {
        PopTip.show("此版本未提供播放")
    }

    fun openDownloadDialogAll() = viewModelScope.launch {
        val arc = arcInfo ?: return@launch
        val service = DownloadService.getService(fragment.requireContext())
        downloadDialogState.show(
            service,
            bvid,
            arc,
            pages.value,
        )
    }

    fun openDownloadDialogSingle(index: Int, page: Page) = viewModelScope.launch {
        val arc = arcInfo ?: return@launch
        val service = DownloadService.getService(fragment.requireContext())
        downloadDialogState.show(
            service,
            bvid,
            arc,
            pages.value,
        )
        downloadDialogState.checkedChange(page.cid, index)
    }

    fun quickCreateDownloadSingle(index: Int, page: Page) = viewModelScope.launch {
        try {
            val arc = arcInfo ?: return@launch
            val aid = arc.aid.toString()
            val cid = page.cid.toString()
            val res = BiliApiService.playerAPI.getVideoPalyUrl(aid, cid, 64, fnval = 4048)
            val acceptQuality = res.accept_quality
            val acceptDescription = res.accept_description
            val q = acceptQuality.maxOrNull() ?: 64
            val descIndex = acceptQuality.indexOf(q).takeIf { it >= 0 } ?: -1
            val desc = if (descIndex >= 0) acceptDescription.getOrNull(descIndex) ?: q.toString() else q.toString()

            val pageData = BiliDownloadEntryInfo.PageInfo(
                cid = page.cid,
                page = page.page,
                from = page.from,
                part = page.part,
                vid = page.vid,
                has_alias = false,
                tid = 0,
                width = 0,
                height = 0,
                rotate = 0,
                download_title = "视频已缓存完成",
                download_subtitle = arc.title,
            )
            val t = System.currentTimeMillis()
            val entry = BiliDownloadEntryInfo(
                media_type = 2,
                has_dash_audio = true,
                is_completed = false,
                total_bytes = 0,
                downloaded_bytes = 0,
                title = arc.title,
                type_tag = q.toString(),
                cover = arc.pic,
                prefered_video_quality = q,
                quality_pithy_description = desc,
                guessed_total_bytes = 0,
                total_time_milli = 0,
                danmaku_count = 1000,
                time_update_stamp = t,
                time_create_stamp = t,
                can_play_in_advance = true,
                interrupt_transform_temp_file = false,
                avid = arc.aid,
                spid = 0,
                season_id = null,
                ep = null,
                source = null,
                bvid = bvid,
                owner_id = arc.author?.mid ?: 0L,
                page_data = pageData,
            )
            val service = DownloadService.getService(fragment.requireContext())
            service.createDownload(entry)
            PopTip.show("已创建1条下载记录")
        } catch (e: Exception) {
            e.printStackTrace()
            PopTip.show(e.message ?: e.toString())
        }
    }
}

@Composable
private fun VideoPagesPageContent(
    viewModel: VideoPagesPageViewModel
) {
    PageConfig(
        title = "视频分P"
    )
    val windowStore: WindowStore by rememberInstance()
    val windowState = windowStore.stateFlow.collectAsState().value
    val windowInsets = windowState.getContentInsets(localContainerView())

    val pages by viewModel.pages.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val failMessage by viewModel.fail.collectAsState()
    val currentPlay by viewModel.currentPlay.collectAsState()

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    fun scrollToCurrentPlay() {
        if (viewModel.aid != currentPlay.aid) return
        val index = pages.indexOfFirst {
            it.cid.toString() == currentPlay.cid
        }
        if (index != -1) {
            scope.launch {
                listState.animateScrollToItem(index)
            }
        }
    }

    LaunchedEffect(pages) {
        scrollToCurrentPlay()
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (loading) {
            BiliLoadingBox(
                modifier = Modifier
                    .fillMaxSize()
            )
        } else if (failMessage != null) {
            BiliFailBox(
                e = failMessage!!,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
        LazyColumn(
            state = listState,
            contentPadding = windowInsets.toPaddingValues()
        ) {
            items(pages.size, { pages[it].cid }) { index ->
                val page = pages[index]
                val isCurrentPlay = currentPlay.cid == page.cid.toString()
                Box(Modifier.padding(vertical = 5.dp, horizontal = 10.dp)) {
                    Surface(
                        modifier = Modifier.fillMaxWidth()
                            .heightIn(min = 50.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = if (isCurrentPlay) BorderStroke(
                            1.dp, color = MaterialTheme.colorScheme.primary
                        ) else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = {
                                    viewModel.startPlayVideo(page)
                                })
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "P${index + 1} " + page.part,
                                fontSize = 18.sp,
                                modifier = Modifier.weight(1f)
                            )
                            if (isCurrentPlay) {
                                Text(
                                    text = "正在播放",
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(end = 5.dp),
                                    color = MaterialTheme.colorScheme.outline,
                                )
                            } else {
                                Text(
                                    text = NumberUtil.converDuration(page.duration),
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(end = 5.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                            TextButton(
                                onClick = { viewModel.openDownloadDialogSingle(index, page) }
                            ) { Text("下载此分P") }
                            TextButton(
                                onClick = { viewModel.quickCreateDownloadSingle(index, page) }
                            ) { Text("快速下载") }
                        }
                    }
                }
            }
            if (currentPlay.aid == viewModel.aid) {
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            Color.Transparent,
                        )
                    )
                )
        )
        if (currentPlay.aid == viewModel.aid) {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
                    .padding(
                        bottom = windowInsets.bottomDp.dp
                    ),
                onClick = ::scrollToCurrentPlay,
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(text = "当前播放：")
                    Text(
                        text = currentPlay.title
                    )
                }
            }
        }

        FloatingActionButton(
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp)
                .padding(bottom = windowInsets.bottomDp.dp),
            onClick = viewModel::openDownloadDialogAll,
        ) {
            Text(text = "选择画质并创建下载")
        }

    }

    VideoDownloadDialog(state = viewModel.downloadDialogState)

}