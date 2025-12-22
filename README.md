# Kotlin JSON5 format reflectionless serialization

> 为 **kotlinx-serialization** 提供 **JSON5** 格式支持的扩展库。

`kotlinx-serialization-json5` 旨在在保持 `kotlinx.serialization` 相同使用体验的前提下，增加对 **JSON5**（注释、单引号、尾随逗号等）的解析能力，使其更适合配置文件、Mod 描述文件等场景。

---

## ✨ 特性

* ✅ 基于 `kotlinx-serialization` 生态
* ✅ 支持 JSON5 语法特性
* ✅ API 风格与 `kotlinx.serialization.json.Json` 保持一致
* ✅ 可与现有 `@Serializable` 数据类直接配合使用
* ✅ 适用于 JVM / Android 项目

---

<!-- ## 📦 安装 -->
<!--  -->
<!-- > 依赖示例（以 Gradle Kotlin DSL 为例） -->
<!--  -->
<!-- ```kotlin -->
<!-- dependencies { -->
<!--     implementation("your.group:kotlinx-serialization-json5:<version>") -->
<!-- } -->
<!-- ``` -->
<!--  -->
<!-- --- -->

## 🚀 快速开始

### 定义数据类

```kotlin
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val name: String,
    val age: Int,
)
```

### 使用 JSON5 解析

```kotlin
import com.youfeng.kotlinx.serialization.json5.Json5

val json5Text = """
// 用户信息
{
  name: 'Alice', // 支持单引号
  age: 18,       // 支持尾随逗号
}
"""

val user = Json5.decodeFromString<User>(json5Text)
println(user)
```

---

## 🧩 与 kotlinx.serialization 的关系

`kotlinx-serialization-json5` **不是** 一个新的序列化框架，而是：

* 复用 `kotlinx.serialization` 的

  * `@Serializable`
  * `SerialDescriptor`
  * 编解码模型
* 扩展对 JSON5 语法的支持

如果你已经在项目中使用了 `kotlinx.serialization.json.Json`，迁移成本几乎为零。

---

## ⚠️ 注意事项

* JSON5 是 **JSON 的超集**，但并非所有 JSON5 特性都适合用于数据交换
* 推荐将其用于：

  * 本地配置
  * 非严格协议数据
* 若用于网络通信，请确保对端兼容

---

## 🧪 兼容性

* Kotlin: 与 `kotlinx-serialization` 保持一致
* 平台：

  * JVM
  * Android

> 由于其他平台缺乏测试，不能保证其兼容性。

---

## 📄 License

```
Copyright 2025 由风

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

---

## 🤝 致谢

* [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)
* [JSON5](https://json5.org/)

欢迎 Issue / PR，一起完善 JSON5 在 Kotlin 生态中的体验。
