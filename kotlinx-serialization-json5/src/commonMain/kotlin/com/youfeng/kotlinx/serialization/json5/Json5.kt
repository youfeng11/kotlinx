package com.youfeng.kotlinx.serialization.json5

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.serializer

/**
 * JSON5 格式的序列化器入口。
 * 
 * 完全兼容 kotlinx-serialization 的 StringFormat 接口。
 */
@OptIn(ExperimentalSerializationApi::class)
public abstract class Json5(
    internal val configuration: Json5Configuration,
    public val json: Json // 改为 public，供扩展函数访问
) : StringFormat {

    public override val serializersModule: SerializersModule
        get() = json.serializersModule

    /**
     * 从 JSON5 字符串解码对象。
     * 
     * @param deserializer 反序列化策略
     * @param string JSON5 格式的字符串
     * @return 反序列化后的对象
     */
    public override fun <T> decodeFromString(
        deserializer: DeserializationStrategy<T>, 
        string: String
    ): T {
        // 1. 转译：JSON5 -> Standard JSON
        val standardJson = Json5Transpiler(string).transpile()
        // 2. 解析：Standard JSON -> Object
        return json.decodeFromString(deserializer, standardJson)
    }

    /**
     * 将对象编码为 JSON 字符串。
     * 
     * 注意：编码输出的是标准 JSON，而非 JSON5 格式。
     * 
     * @param serializer 序列化策略
     * @param value 待序列化的对象
     * @return JSON 字符串
     */
    public override fun <T> encodeToString(
        serializer: SerializationStrategy<T>, 
        value: T
    ): String {
        return json.encodeToString(serializer, value)
    }

    public companion object Default : Json5(
        Json5Configuration(),
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            allowTrailingComma = true
            allowComments = true
            allowSpecialFloatingPointValues = true
            prettyPrint = false
        }
    ) {
        /**
         * 创建自定义配置的 Json5 实例。
         * 
         * 示例:
         * ```kotlin
         * val json5 = Json5 {
         *     prettyPrint = true
         *     ignoreUnknownKeys = true
         *     serializersModule = MyModule
         * }
         * ```
         */
        public operator fun invoke(builderAction: Json5Builder.() -> Unit): Json5 {
            val builder = Json5Builder()
            builder.builderAction()
            return builder.build()
        }
    }
}

/**
 * Json5 的配置选项。
 */
public data class Json5Configuration(
    public val prettyPrint: Boolean = false,
    public val prettyPrintIndent: String = "    "
)

/**
 * Json5 构建器，用于创建自定义配置的 Json5 实例。
 */
@OptIn(ExperimentalSerializationApi::class)
public class Json5Builder {
    // === Json5 独有配置 ===
    public var prettyPrint: Boolean = false
    public var prettyPrintIndent: String = "    "

    // === 代理到底层 Json 的配置 ===
    public var encodeDefaults: Boolean = true
    public var ignoreUnknownKeys: Boolean = true
    public var isLenient: Boolean = true
    public var allowStructuredMapKeys: Boolean = false
    public var allowSpecialFloatingPointValues: Boolean = true
    public var coerceInputValues: Boolean = false
    public var classDiscriminator: String = "type"
    public var useArrayPolymorphism: Boolean = false
    public var serializersModule: SerializersModule = EmptySerializersModule()

    internal fun build(): Json5 {
        val jsonDelegate = Json {
            this.encodeDefaults = this@Json5Builder.encodeDefaults
            this.ignoreUnknownKeys = this@Json5Builder.ignoreUnknownKeys
            this.isLenient = this@Json5Builder.isLenient
            this.allowStructuredMapKeys = this@Json5Builder.allowStructuredMapKeys
            this.allowSpecialFloatingPointValues = this@Json5Builder.allowSpecialFloatingPointValues
            this.coerceInputValues = this@Json5Builder.coerceInputValues
            this.classDiscriminator = this@Json5Builder.classDiscriminator
            this.useArrayPolymorphism = this@Json5Builder.useArrayPolymorphism
            this.serializersModule = this@Json5Builder.serializersModule

            // 格式化输出控制
            this.prettyPrint = this@Json5Builder.prettyPrint
            this.prettyPrintIndent = this@Json5Builder.prettyPrintIndent

            // 强制开启以支持转译后的残留兼容性
            this.allowTrailingComma = true
            this.allowComments = true
        }

        // 使用 object 表达式创建匿名子类
        return object : Json5(
            Json5Configuration(prettyPrint, prettyPrintIndent),
            jsonDelegate
        ) {}
    }
}

// ============================================================================
// 扩展函数：提供类型安全的便捷 API
// ============================================================================

/**
 * 从 JSON5 字符串解码为指定类型的对象。
 * 
 * 使用示例:
 * ```kotlin
 * @Serializable
 * data class User(val name: String, val age: Int)
 * 
 * val user = Json5.decodeFromString<User>("""
 *     {
 *       name: 'Alice',  // 单引号
 *       age: 30,        // 尾随逗号
 *     }
 * """)
 * ```
 * 
 * @param T 目标类型（必须标注 @Serializable）
 * @param string JSON5 格式的字符串
 * @return 反序列化后的对象
 */
public inline fun <reified T> Json5.decodeFromString(string: String): T =
    decodeFromString(serializersModule.serializer(), string)

/**
 * 将对象编码为 JSON 字符串。
 * 
 * 注意：输出的是标准 JSON 格式，不包含 JSON5 特性（如单引号、注释等）。
 * 
 * @param T 对象类型（必须标注 @Serializable）
 * @param value 待编码的对象
 * @return JSON 字符串
 */
public inline fun <reified T> Json5.encodeToString(value: T): String =
    encodeToString(serializersModule.serializer(), value)

/**
 * 解析 JSON5 字符串到指定类型（别名方法，提高可读性）。
 */
public inline fun <reified T> Json5.parse(string: String): T =
    decodeFromString(string)

/**
 * 序列化对象到 JSON 字符串（别名方法，提高可读性）。
 */
public inline fun <reified T> Json5.stringify(value: T): String =
    encodeToString(value)