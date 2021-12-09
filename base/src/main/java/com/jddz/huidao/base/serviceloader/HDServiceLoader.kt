package com.jddz.huidao.base.serviceloader

import java.lang.Exception
import java.util.*

/**
 * 获取慧道ServiceLoader对象
 * @Author cc
 * @Date 2021/11/9-16:09
 * @version 1.0
 */
object HDServiceLoader {
    fun <S> load(clazz: Class<S>?): S? {
        return try {
            ServiceLoader.load(clazz).iterator().next()
        } catch (e: Exception) {
            null
        }
    }
}