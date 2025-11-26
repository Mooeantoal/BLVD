package cn.a10miaomiao.bilimiao.compose.pages

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.a10miaomiao.bilimiao.compose.StartViewContent
import cn.a10miaomiao.bilimiao.compose.StartViewWrapper
import cn.a10miaomiao.bilimiao.compose.base.ComposePage
import cn.a10miaomiao.bilimiao.compose.common.mypage.PageConfig
import kotlinx.serialization.Serializable
import org.kodein.di.compose.rememberInstance

@Serializable
class StartPage : ComposePage() {

    @Composable
    override fun Content() {
        val startViewWrapper by rememberInstance<StartViewWrapper>()
        
        PageConfig(
            title = "bilimiao"
        )

        fun openSearch() {
            startViewWrapper.openSearchDialog("", 0, true)
        }

        StartViewContent(
            modifier = Modifier,
            startTopHeight = 10.dp, // 稍微留一点顶部间距
            navigateTo = startViewWrapper.navigateTo,
            navigateUrl = startViewWrapper.navigateUrl,
            openSearch = ::openSearch,
            openScanner = startViewWrapper.openScanner,
            isSearchVisible = startViewWrapper.showSearchDialog,
            searchInitKeyword = startViewWrapper.searchInitKeyword,
            searchInitMode = startViewWrapper.searchInitMode,
            pageSearchMethod = startViewWrapper.pageSearchMethod,
            onCloseSearch = startViewWrapper::closeSearchDialog,
            searchAnimation = true
        )
    }
}
