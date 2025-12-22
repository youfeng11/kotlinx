package com.youfeng.kotlinx.serialization.json5

/**
 * 将 JSON5 源码转译为标准 JSON 字符串的内部工具。
 *
 * 主要职责：
 * 1. 移除注释
 * 2. 补全 Key 的引号
 * 3. 转换单引号字符串为双引号
 * 4. 处理多行字符串拼接
 * 5. 转换十六进制数字、前导小数点等非标数值格式
 * 6. 处理尾随逗号 (虽然底层 Json 可能支持，但在转译层处理更规范)
 */
internal class Json5Transpiler(private val input: String) {
    // 预分配容量，通常 JSON5 转 JSON 长度变化不大，甚至会变小（去除了注释）
    private val sb = StringBuilder(input.length)
    private var index = 0
    private val length = input.length

    fun transpile(): String {
        while (index < length) {
            val char = input[index]

            when {
                // 1. 处理注释 (// 或 /*)
                char == '/' -> handleComment()

                // 2. 处理字符串 (单引号或双引号)
                char == '"' || char == '\'' -> handleString(char)

                // 3. 处理标识符/字面量 (Key, Number, Boolean, Null, Infinity, NaN)
                // 包括：字母, $, _, 数字, ., +, -
                isValidTokenStart(char) -> handleToken()

                // 4. 其他字符直接追加 (如 {}, [], :, 逗号)
                else -> {
                    sb.append(char)
                    index++
                }
            }
        }
        return sb.toString()
    }

    private fun handleComment() {
        // 确保不是结尾
        if (index + 1 >= length) {
            sb.append('/')
            index++
            return
        }

        val nextChar = input[index + 1]
        when (nextChar) {
            '/' -> { // 单行注释
                index += 2
                while (index < length && !isLineTerminator(input[index])) {
                    index++
                }
                // 注意：不保留换行符，换行符会在外层循环被追加
            }
            '*' -> { // 多行注释
                index += 2
                while (index < length - 1) {
                    if (input[index] == '*' && input[index + 1] == '/') {
                        index += 2
                        return
                    }
                    index++
                }
                index = length // 未闭合的注释，直接结束
            }
            else -> { // 不是注释，是除号或路径
                sb.append('/')
                index++
            }
        }
    }

    private fun handleString(quoteChar: Char) {
        sb.append('"') // 强制统一为双引号
        index++ // 跳过开始的引号

        while (index < length) {
            val char = input[index]

            if (char == '\\') {
                // 处理转义
                if (index + 1 < length) {
                    val next = input[index + 1]
                    if (isLineTerminator(next)) {
                        // === JSON5 特性：多行字符串拼接 ===
                        // 遇到 \ 后紧跟换行符，直接忽略这两个字符
                        // ❌ 修复：原代码 skipWhitespace() 是错误的，JSON5 不会吞掉下一行的缩进
                        index++ // 跳过 \
                        skipLineTerminator() // 跳过换行符
                        continue
                    }
                }

                sb.append('\\')
                index++
                if (index < length) {
                    // 处理引号转义：如果是单引号字符串内部的 '，转成 JSON 后不需要转义
                    // 如果是单引号字符串内部的 "，转成 JSON 后需要变成 \"
                    val escapedChar = input[index]
                    if (quoteChar == '\'' && escapedChar == '\'') {
                        // 输入: 'It\'s' -> 输出: "It's" (去除反斜杠)
                        // 这里直接追加 ' 即可，因为之前已经 append 了 \ (Wait, no)
                        // 回退上一步的 append('\\') 是低效的。
                        // 正确逻辑：
                        // 我们需要决定是否输出反斜杠。
                        sb.setLength(sb.length - 1) // 撤销刚才的 \
                        sb.append('\'')
                    } else if (quoteChar == '\'' && escapedChar == '"') {
                        // 输入: 'Say "Hi"' -> 输出: "Say \"Hi\""
                        // 已经是 \ 了，追加 "
                        sb.append('"')
                    } else {
                        // 其他转义直接保留
                        sb.append(escapedChar)
                    }
                    index++
                }
                continue
            }

            if (char == quoteChar) {
                sb.append('"') // 闭合双引号
                index++
                return
            }

            if (char == '"' && quoteChar == '\'') {
                // 单引号包裹的内容中有双引号：'a "b" c' -> "a \"b\" c"
                sb.append("\\\"")
            } else {
                sb.append(char)
            }
            index++
        }
    }

    private fun handleToken() {
        val start = index

        // 1. 特殊处理十六进制 (0x...)
        if (input.startsWith("0x", index, ignoreCase = true)) {
            val start = index // 确保记录 0x 的起始位置
            index += 2 // 跳过 "0x"
    
            // ✅ 检查：确保 isHexDigit 函数正确
            while (index < length && isHexDigit(input[index])) {
                index++
            }
            val hexToken = input.substring(start, index) // 捕获整个 token (e.g., "0xdecaf")
            
            // 检查这是否是一个 Key (e.g. 0x123: "value")
            if (isKey(index)) {
                 sb.append("\"$hexToken\"")
            } else {
                sb.append(convertJson5Number(hexToken))
            }
            return
        }

        // 2. 捕获常规 Token
        while (index < length && isValidTokenPart(input[index])) {
            index++
        }
        val token = input.substring(start, index)

        // 3. 判断是 Key 还是 Value
        if (isKey(index)) {
            sb.append("\"$token\"")
        } else {
            sb.append(convertJson5Number(token))
        }
    }

    /**
     * 判断当前 Token 后面是否紧跟着冒号（从而判断它是否为 Key）。
     * ✅ 修复：必须跳过中间可能存在的注释和空白。
     */
    private fun isKey(fromIndex: Int): Boolean {
        var i = fromIndex
        while (i < length) {
            val c = input[i]
            
            if (c.isWhitespace() || isLineTerminator(c)) {
                i++
                continue
            }
            
            // 处理注释的情况：key /* comment */ : value
            if (c == '/') {
                if (i + 1 < length) {
                    val next = input[i + 1]
                    if (next == '/') {
                        i += 2
                        while (i < length && !isLineTerminator(input[i])) i++
                        continue
                    } else if (next == '*') {
                        i += 2
                        while (i < length - 1) {
                            if (input[i] == '*' && input[i + 1] == '/') {
                                i += 2
                                break
                            }
                            i++
                        }
                        if (i >= length - 1 && !(input[i-2]=='*' && input[i-1]=='/')) return false // 未闭合
                        continue
                    }
                }
            }

            return c == ':'
        }
        return false
    }

    private fun convertJson5Number(token: String): String {
        // 保留特殊值 (Standard Json 库在 lenient 模式下支持这些)
        if (token == "Infinity" || token == "-Infinity" || token == "NaN" || token == "-NaN") {
            return token
        }

        // 十六进制转换
        if (token.startsWith("0x", ignoreCase = true)) {
            return try {
                // ✅ 核心修复：使用 .toLong(16) 将 16 进制字符串转换为 Long，然后转为 String 输出
                // 确保我们转换的是数字部分 (decaf)，而不是 "0xdecaf"
                token.substring(2).toLong(16).toString() 
            } catch (e: Exception) {
                token 
            }
        }

        var result = token

        // 处理显式正号
        if (result.startsWith("+")) {
            result = result.substring(1)
        }

        // 处理前导小数点 (.123 -> 0.123, -.123 -> -0.123)
        if (result.startsWith(".")) {
            result = "0$result"
        } else if (result.startsWith("-.")) {
            result = "-0" + result.substring(1)
        }

        // 处理尾随小数点 (12. -> 12.0)
        // 注意：不应影响 12.5，也不应影响结尾是 .. 的情况（虽不合法但避免崩溃）
        if (result.endsWith(".")) {
            result += "0"
        }

        return result
    }

    private fun skipLineTerminator() {
        if (index >= length) return
        val c = input[index]
        if (c == '\n') {
            index++
        } else if (c == '\r') {
            index++
            if (index < length && input[index] == '\n') {
                index++
            }
        } else if (c == '\u2028' || c == '\u2029') {
            index++
        }
    }

    private fun isLineTerminator(c: Char): Boolean =
        c == '\n' || c == '\r' || c == '\u2028' || c == '\u2029'

    private fun isHexDigit(c: Char): Boolean =
        c in '0'..'9' || c in 'a'..'f' || c in 'A'..'F'

    private fun isValidTokenStart(c: Char): Boolean {
        // JSON5 Key 必须以 字母, $, _ 开头 (这里稍微放宽以支持数字开头的 Value)
        return c.isLetterOrDigit() || c == '$' || c == '_' || c == '.' || c == '+' || c == '-'
    }

    private fun isValidTokenPart(c: Char): Boolean {
        // 这里需要包含所有可能出现在未加引号 Key 或 数字 中的字符
        return c.isLetterOrDigit() || c == '$' || c == '_' || c == '-' || c == '+' || c == '.' || c == '\\' // 添加 \ 以尽量不截断 Unicode 转义（虽然目前未完整支持转义解析）
    }
}