package com.example.network.model

enum class ParamPlaceEnum {
    DO_NOT_NEED,        // 无需添加或获取
    FROM_OR_TO_HEADER,  // 在头文件中添加或获取
    FROM_OR_TO_BODY,    // 在请求或返回内容中添加或获取
    BODY_AND_HEADER,    // 在请求头或请求内容中添加
}