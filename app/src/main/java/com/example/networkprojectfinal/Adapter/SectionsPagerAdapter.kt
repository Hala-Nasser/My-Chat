package com.example.networkprojectfinal

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.networkprojectfinal.Fragments.GroupsFragment
import com.example.networkprojectfinal.Fragments.UserFragment

private val TAB_TITLES = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_2
)

@Suppress("DEPRECATION")
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {

        var fragment: Fragment? =null

        when (position) {
            0 -> fragment = UserFragment()
            1 -> fragment= GroupsFragment()
        }

        return fragment!!
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return 2
    }
}