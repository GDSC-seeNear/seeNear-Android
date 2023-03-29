package com.kgg.android.seenear.network.data

data class reportList(
    var reportList : List<Report>
)

data class Report(
    var date : String? = null,
    var sentiment : List<Sentiment>,
    var ner : List<Ner>,
    var statusCheck : List<StatusCheck>,
)

data class Ner(

    var id : Int = 0,
    var type : String? = null,
    var target : String? = null,
    var fullText : String? = null,
    var chatId : Int = 0,
    var elderlyId : Int = 0,

)

data class Sentiment(
    var label : String? = null,
    var count : Int = 0
)

data class StatusCheck(
    var type : String? = null,
    var done : Boolean = false
)
