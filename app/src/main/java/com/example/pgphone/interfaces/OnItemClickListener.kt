package com.example.pgphone.interfaces

/**
 * 列表点击接口
 */
interface OnItemClickListener<T> {

    fun onClick(entity: T)

}