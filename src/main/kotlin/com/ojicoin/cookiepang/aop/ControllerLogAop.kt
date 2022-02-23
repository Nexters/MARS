package com.ojicoin.cookiepang.aop

import org.apache.commons.lang3.StringUtils
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.configurationprocessor.json.JSONObject
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest


@Aspect
@Component
class ControllerLogAop {

    private val log: Logger = LoggerFactory.getLogger(ControllerLogAop::class.java)


    @Before("execution(* com.ojicoin.cookiepang.controller..*Controller.*(..)) || execution(* com.ojicoin.cookiepang.contract.controller..*Controller.*(..))")
    @Throws(Throwable::class)
    fun methodLogger(joinPoint: JoinPoint) {

        val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
        val params: MutableMap<String, Any> = HashMap()
        val controllerName: String = joinPoint.getSignature().getDeclaringType().getSimpleName()
        val methodName: String = joinPoint.getSignature().getName()
        var isMultipart = false
        if (request.contentType != null) {
            isMultipart = MediaType.MULTIPART_FORM_DATA_VALUE.equals(request.contentType.split(";").toTypedArray()[0])
        }
        try {
            params["params"] = getParams(request)
            params["body"] = if (isMultipart) "MULTIPART_DATA" else StringUtils.EMPTY
            params["domain"] = request.serverName
            params["controller"] = controllerName
            params["method"] = methodName
            params["log_time"] = LocalDateTime.now()
            params["request_uri"] = request.requestURI
            params["http_method"] = request.method
        } catch (e: Exception) {
            log.error("LoggerAspect error", e)
        }
        log.info("CONTROLLER LOG : {}", params)
    }

    private fun getParams(request: HttpServletRequest): JSONObject {
        val jsonObject = JSONObject()
        val params = request.parameterNames
        while (params.hasMoreElements()) {
            val param = params.nextElement()
            val replaceParam = param.replace("\\.".toRegex(), "-")
            jsonObject.put(replaceParam, request.getParameter(param))
        }
        return jsonObject
    }
}