package com.project.advertisementapp

data class AdvertiseData(
    var itemImage: String? = "",
    var itemImageUrl: String? = "",
    var itemId : String? ="",
    var itemName: String? ="",
    var itemPrice: String? ="",
    var itemDescription: String? ="",
    var verified: String? = "",
    var userID : String="",
)

data class UserData(
    var userName : String ="",
    var emailId :String ="",
    var uuid: String ="",
    var contact: String ="",
)

data class ItemPosted(
    var itemId: String?= "",
    var useruid : String?=""
)

data class SubscriberData(
    var subscriberID: String =""
)

data class SubscriberMessageLog(
    var message: String ="",
    var forUserId: String =""
)