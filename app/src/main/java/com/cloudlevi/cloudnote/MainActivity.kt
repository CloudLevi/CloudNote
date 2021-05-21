package com.cloudlevi.cloudnote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.cloudlevi.cloudnote.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}

const val VIEW_TYPE_NOTE = 0
const val VIEW_TYPE_FOLDER = 1

const val ITEM_TYPE_NOTE = 0
const val ITEM_TYPE_FOLDER = 1

const val HOME_TYPE_LISTVIEW = 0
const val HOME_TYPE_GRIDVIEW = 1

const val FRAGMENT_TYPE_HOME = 0
const val FRAGMENT_TYPE_FOLDER = 1

const val NAVIGATION_DESTINATION_MAIN = 0
const val NAVIGATION_DESTINATION_FOLDER = 1