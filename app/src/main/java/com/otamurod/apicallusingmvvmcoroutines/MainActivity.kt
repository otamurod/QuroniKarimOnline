package com.otamurod.apicallusingmvvmcoroutines

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.otamurod.apicallusingmvvmcoroutines.databinding.ActivityMainBinding
import com.otamurod.apicallusingmvvmcoroutines.fragment.RecyclerListFragment

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}