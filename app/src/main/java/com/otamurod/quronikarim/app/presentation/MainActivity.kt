package com.otamurod.quronikarim.app.presentation

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import com.otamurod.quronikarim.R
import com.otamurod.quronikarim.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = getString(R.string.ic_home)
        setNavigationView()
    }

    private fun setNavigationView() {
        binding.navView.itemIconTintList = null
        actionBarDrawerToggle =
            ActionBarDrawerToggle(
                this,
                binding.myDrawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            )

        binding.myDrawerLayout.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle!!.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    title = getString(R.string.ic_home)
                    binding.navHostFragment.findNavController().navigate(R.id.MainFragment)
                    binding.myDrawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.rule -> {
                    title = getString(R.string.rule)
                    binding.myDrawerLayout.closeDrawer(GravityCompat.START)
                    binding.navHostFragment.findNavController()
                        .navigate(R.id.rulesFragment)
                    true
                }
                R.id.rate -> {
                    title = getString(R.string.ic_rate)
                    binding.myDrawerLayout.closeDrawer(GravityCompat.START)
                    binding.navHostFragment.findNavController()
                        .navigate(R.id.rateFragment)
                    true
                }
                else -> {
                    title = getString(R.string.ic_about)
                    binding.myDrawerLayout.closeDrawer(GravityCompat.START)
                    binding.navHostFragment.findNavController()
                        .navigate(R.id.aboutFragment)
                    true
                }
            }
        }
    }

    override fun onBackPressed() {
        if (binding.navHostFragment.findNavController().currentDestination?.id == R.id.MainFragment) {
            finish()
        }
        binding.myDrawerLayout.closeDrawer(GravityCompat.START)
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle!!.onOptionsItemSelected(item)) {
            return true
        }
        binding.myDrawerLayout.closeDrawer(GravityCompat.START)
        return super.onOptionsItemSelected(item)
    }
}