package com.ojicoin.cookiepang.util

class NotificationMessageUtils {
    companion object {
        fun getAskMessage(cookieTitle: String): String {
            // TODO consider supporting other language

            return "익명의 누군가가 ${cookieTitle}라는 질문을 요청했습니다."
        }

        fun getTransactionMessage(nickname: String, cookieTitle: String, hammerCount: Long): String {
            // TODO consider supporting other language

            return "${nickname}님이 ${cookieTitle}를 망치 ${hammerCount}톤으로 구매했습니다."
        }
    }
}
