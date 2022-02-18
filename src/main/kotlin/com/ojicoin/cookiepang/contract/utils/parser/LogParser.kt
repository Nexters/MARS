package com.ojicoin.cookiepang.contract.utils.parser

import com.klaytn.caver.methods.response.KlayLogs
import com.ojicoin.cookiepang.contract.event.Event

/**
 * @author seongchan.kang
 */
interface LogParser<T : Event> {
    fun parse(logs: List<KlayLogs.Log>): List<T>
    fun parse(log: KlayLogs.Log): T
}
