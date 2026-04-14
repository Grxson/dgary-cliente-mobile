package com.tuempresa.tuapp.data.local

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SelectedCoupon(
    val code: String,
    val title: String,
    val discountAmount: Double,
    val discountLabel: String
)

object SelectedCouponStore {

    private val _selectedCoupon = MutableStateFlow<SelectedCoupon?>(null)
    val selectedCoupon: StateFlow<SelectedCoupon?> = _selectedCoupon.asStateFlow()

    fun select(coupon: SelectedCoupon) {
        _selectedCoupon.value = coupon
    }

    fun clear() {
        _selectedCoupon.value = null
    }
}