package com.example.dowoom.Util

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.PermissionChecker.checkCallingOrSelfPermission
import com.example.dowoom.activity.register.CheckActivity

class PermissionCheck(var activity:Activity) {

    val permissionList = arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)


    fun checkPermission() {
        //현재 버전 6.0 미만이면 종료 --> 6이후 부터 권한 허락
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return

        //각 권한 허용 여부를 확인
        for (permission in permissionList) {
            val chk = activity.checkCallingOrSelfPermission(permission)
            //거부 상태라면
            if (chk == PackageManager.PERMISSION_DENIED) {
                //사용자에게 권한 허용여부를 확인하는 창을 띄운다.
                activity.requestPermissions(permissionList, 0) //권한 검사 필요한 것들만 남는다.
                break
            }

        }

    }
}