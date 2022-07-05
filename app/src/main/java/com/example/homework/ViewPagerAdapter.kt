package com.example.homework

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter (fragmentActivity: FragmentActivity): FragmentStateAdapter (fragmentActivity) {
    val fragments = mutableListOf<Fragment>()

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return MenuSelectCoffeeFragment()
            1 -> return MenuSelectBeverageFragment()
            2 -> return MenuSelectAdeFragment()
            3 -> return MenuSelectBakeryFragment()
            4 -> return MenuSelectGetCategoryFragment()
            5 -> return MenuSelectGetCategoryAnotherFragment()
        }
        return MenuSelectCoffeeFragment()
    }
}