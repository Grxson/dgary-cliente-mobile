package com.tuempresa.tuapp.ui.order.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tuempresa.tuapp.R
import com.tuempresa.tuapp.ui.order.adapter.OrdersAdapter
import com.tuempresa.tuapp.ui.order.viewmodel.OrdersListViewModel
import kotlinx.coroutines.launch

class OrdersListActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var rvOrders: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var tvLoading: TextView

    private lateinit var adapter: OrdersAdapter
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return OrdersListViewModel(this@OrdersListActivity) as T
                }
            }
        )[OrdersListViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_list)

        btnBack = findViewById(R.id.btn_back_orders_list)
        rvOrders = findViewById(R.id.rv_orders_list)
        tvEmpty = findViewById(R.id.tv_orders_empty)
        tvLoading = findViewById(R.id.tv_orders_loading)

        btnBack.setOnClickListener { finish() }

        rvOrders.layoutManager = LinearLayoutManager(this)
        adapter = OrdersAdapter(emptyList()) { order ->
            startActivity(Intent(this, OrderDetailActivity::class.java).putExtra("order_id", order.id.toString()))
        }
        rvOrders.adapter = adapter

        observeState()
        viewModel.loadOrders()
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                adapter.updateItems(state.orders)
                tvLoading.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                rvOrders.visibility = if (state.orders.isEmpty()) View.GONE else View.VISIBLE
                tvEmpty.visibility = if (!state.isLoading && state.orders.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }
}