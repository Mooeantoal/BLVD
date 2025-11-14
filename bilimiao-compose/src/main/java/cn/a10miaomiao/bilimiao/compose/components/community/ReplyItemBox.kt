package cn.a10miaomiao.bilimiao.compose.components.community

import android.view.MotionEvent
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.a10miaomiao.bilimiao.compose.R
import cn.a10miaomiao.bilimiao.compose.components.AsyncImage
import cn.a10miaomiao.bilimiao.compose.components.UrlImage
import cn.a10miaomiao.bilimiao.compose.local.LocalNavController
import cn.a10miaomiao.bilimiao.compose.local.LocalOwnerViewModel
import cn.a10miaomiao.bilimiao.compose.local.LocalUser
import cn.a10miaomiao.bilimiao.compose.navigation.NavDestination
import cn.a10miaomiao.bilimiao.compose.utils.formatDate
import cn.a10miaomiao.bilimiao.compose.utils.openBrowser
import com.a10miaomiao.bilimiao.comm.entity.video.VideoCommentReplyInfo
import kotlinx.parcelize.Parcelize
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.Navigator

@Parcelize
data class ReplyItemInfo(
    val reply: VideoCommentReplyInfo,
    val isTop: Boolean = false,
): android.os.Parcelable

@Composable
fun ReplyItemBox(
    modifier: Modifier = Modifier,
    item: ReplyItemInfo,
    onLike: (VideoCommentReplyInfo) -> Unit = {},
    onReply: (VideoCommentReplyInfo) -> Unit = {},
) {
    val navController = LocalNavController.current
    val ownerViewModel = LocalOwnerViewModel.current
    val user = LocalUser.current
    val reply = item.reply
    val isTop = item.isTop
    val interactionSource = remember { MutableInteractionSource() }
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(color = MaterialTheme.colors.onBackground.copy(alpha = 0.1f)),
            ) {
                // 点击事件处理
            }
            .padding(
                start = if (reply.parent == 0L) 0.dp else 30.dp,
                top = 8.dp,
                end = 16.dp,
                bottom = 8.dp
            )
    ) {
        if (isTop) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_top),
                    contentDescription = "置顶",
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colors.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "置顶",
                    fontSize = 12.sp,
                    color = MaterialTheme.colors.primary
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            UrlImage(
                url = reply.member.avatar,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable {
                        navController.navigate(
                            NavDestination.UserDetail(reply.member.mid),
                            NavOptions(launchSingleTop = true)
                        )
                    },
                contentScale = ContentScale.Crop,
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = reply.member.uname,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (reply.member.vip.vipType > 0) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color(0xFFFB7299),
                                    shape = RoundedCornerShape(2.dp)
                                )
                                .padding(horizontal = 2.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "大会员",
                                fontSize = 10.sp,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = formatDate(reply.ctime * 1000),
                        fontSize = 12.sp,
                        color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
                    )
                }

                Text(
                    text = reply.content.message,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    color = MaterialTheme.colors.onBackground
                )

                if (reply.reply_control.location != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_location_on),
                            contentDescription = "位置",
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = reply.reply_control.location!!,
                            fontSize = 12.sp,
                            color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }

                if (!reply.replies.isNullOrEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        reply.replies!!.forEach { childReply ->
                            ReplyItemBox(
                                item = ReplyItemInfo(childReply),
                                onLike = onLike,
                                onReply = onReply,
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable {
                                onLike(reply)
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (reply.action == 1) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "点赞",
                            modifier = Modifier.size(16.dp),
                            tint = if (reply.action == 1) Color(0xFFFB7299) else MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (reply.like > 0) reply.like.toString() else "点赞",
                            fontSize = 12.sp,
                            color = if (reply.action == 1) Color(0xFFFB7299) else MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable {
                                onReply(reply)
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Reply,
                            contentDescription = "回复",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (reply.rcount > 0) reply.rcount.toString() else "回复",
                            fontSize = 12.sp,
                            color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}