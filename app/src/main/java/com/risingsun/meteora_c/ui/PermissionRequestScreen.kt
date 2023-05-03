package com.risingsun.meteora_c.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState

@ExperimentalPermissionsApi
@Composable
fun PermissionRequestScreen(permissionsState: MultiplePermissionsState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "欢迎使用 Meteora ！\n为了能够正常工作，App 需要获取相关权限，请点击下方的按钮。",
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 20.dp),
            fontSize = 20.sp
        )
        if (permissionsState.shouldShowRationale) {
            Text(
                text = """
                看起来您拒绝授予权限，App 需要得到读取音频文件的权限才能将设备里音乐展示出来。请重新点击下面的按钮来授予权限。
                请注意，如果您再次拒绝，那么之后需要到系统设置中手动授予权限，才能正常使用 App 。
            """.trimIndent()
            )
        } else Text(text = "目前状态：权限未授予")
        Button(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
            Text(text = "点击发起权限申请")
        }
    }
}