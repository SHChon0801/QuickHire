package my.edu.tarc.quickhire.ui.notifications

import android.graphics.Bitmap

class NotificationData {
    var notificationTitle:String?=null
    var notificationDec:String?=null
    var notificationTime:String?=null
    var notificationImage:String?=null

    constructor(
        notificationTitle:String?,
        notificationDec:String?,
        notificationTime:String?,
        notificationImage: String?
    )
    {
        this.notificationTitle=notificationTitle
        this.notificationDec=notificationDec
        this.notificationTime=notificationTime
        this.notificationImage= notificationImage
    }

    constructor()
    {

    }
}