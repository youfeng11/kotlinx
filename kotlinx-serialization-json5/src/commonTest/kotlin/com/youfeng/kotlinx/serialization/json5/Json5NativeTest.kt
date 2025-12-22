package com.youfeng.kotlinx.serialization.json5

import kotlinx.serialization.Serializable
import kotlin.test.*

class Json5NativeTest {
    @Test
    fun testNativeBasics() {
        @Serializable
        data class NativeData(val value: String)
        
        val json5 = "{ value: 'Native Test' }"
        val data = Json5.decodeFromString<NativeData>(json5)
        
        assertEquals("Native Test", data.value)
    }

    @Test
    fun testMemoryEfficiency() {
        @Serializable
        data class SmallObject(val id: Int)
        
        // 测试多次解析不会导致内存问题
        repeat(100) { i ->
            val json5 = "{ id: $i }"
            val obj = Json5.decodeFromString<SmallObject>(json5)
            assertEquals(i, obj.id)
        }
    }
}