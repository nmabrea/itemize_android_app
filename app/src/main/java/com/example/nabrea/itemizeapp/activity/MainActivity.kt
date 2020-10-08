package com.example.nabrea.itemizeapp.activity

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.RecyclerView
import com.example.nabrea.itemizeapp.ExpandingFabAnimationInterface
import com.example.nabrea.itemizeapp.R
import com.example.nabrea.itemizeapp.databinding.ActivityMainBinding
import com.example.nabrea.itemizeapp.screens.home.ReceiptHistoryFragmentDirections
import com.example.nabrea.itemizeapp.screens.receipt.ReceiptFragment
import com.example.nabrea.itemizeapp.screens.receipt.ReceiptFragmentCommunication
import com.example.nabrea.itemizeapp.screens.receipt.uidisplay.BottomSheetClass
import com.example.nabrea.itemizeapp.screens.receipt.uidisplay.MenuClass
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import timber.log.Timber
import java.util.*
import kotlin.concurrent.schedule

// The MainActivity is the NavigationHost for all of the Fragments that follow it
// The Main Floating Action button animations are controlled within this Activity
// It can also communicate and respond to methods and behaviors from the Receipt Fragment
class MainActivity : AppCompatActivity(),
    NavigationHostInterface,
    ExpandingFabAnimationInterface,
    ReceiptFragmentCommunication {

    // Data Binding is applied to the Main Activity xml file
    private lateinit var activityMainBinding: ActivityMainBinding

    private lateinit var sharedViewModel: ItemizeViewModel

    // Variable for identifying the BottomAppBar for hosting Navigation Options
    private lateinit var mainBab: BottomAppBar

    // Variable for identifying the primary FAB for Navigation
    private lateinit var primaryAction: PrimaryButtonClass

    // Variable for identifying the primary navigation button
    private lateinit var primaryButton: FloatingActionButton

    // Variable for primary button's icon
    private lateinit var plusIcon: Drawable

    // Variable for primary button's secondary icon
    private lateinit var twoCheckIcon: Drawable

    // Variable for a modified app bar
    private lateinit var appBarConfiguration: AppBarConfiguration

    // Variables inherited by the NavigationHost interface
    // Identifying the NavController
    override lateinit var navController: NavController

    // Variable to set up the Navigation Drawer
    override lateinit var drawerLayout: DrawerLayout

    // Variable establishing the animation context
    override lateinit var animationContext: Context

    // Variable establishing the NavDirections for the Primary Button
    private var primaryNavDirection: NavDirections? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Internal message to track when the Activity is created
        Timber.i("onCreate() is called")

        // Setting the content view to the layout with Data Binding applied
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        sharedViewModel = ViewModelProvider(this).get(ItemizeViewModel::class.java)

        // Code for Navigation Drawer and Up Button below:
        // Associating Main Activity as Navigation Host
        navController = (this.findNavController(R.id.myNavHostFragment))

        // Associating drawerLayout properties with the Drawer Layout View within the layout xml
        drawerLayout = activityMainBinding.drawerLayoutView

        // NavigationHostInterface method to keep the NavigationDrawer only in the Home fragment
        drawerMode()

        // Identifying the BottomAppBar's associated view within the MainActivity layout xml
        mainBab = activityMainBinding.bottomAppBar

        // Establishing the NavDirections for the Primary Button at its current state
        primaryNavDirection = ReceiptHistoryFragmentDirections
            .actionReceiptHistoryFragmentToReceiptFragment()

        // Establishing the Main Activity as the context for future animations
        animationContext = this

        // Identifying the primary button's associated view within the MainActivity layout xml
        primaryButton = activityMainBinding.buttonPrimaryAction

        // Identifying the primary FAB Button used for Navigation throughout the app
        primaryAction = PrimaryButtonClass(
            primaryButton,
            navController,
            mainBab,
            primaryNavDirection!!,
            animationContext)

        // Locating the plus icon to be used for the Primary Button
        plusIcon = ResourcesCompat.getDrawable(resources,
            R.drawable.itm_icon_plus_white, null)!!

        // Locating the double check icon to be used for the Primary Button
        twoCheckIcon = ResourcesCompat.getDrawable(resources,
            R.drawable.itm_icon_double_check_white, null)!!

        // Identifying the BottomAppBar as the ActionBar for the app
        setSupportActionBar(mainBab)

        // Allowing UpButton Navigation within the action bar
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // Modified appbar configuration to remove the UpButton from the listed layouts when applied
        appBarConfiguration = AppBarConfiguration.Builder(
            R.id.receiptHistoryFragment,
            R.id.receiptFragment
        ).build()

        // Setting up the default configuration for the NavigationDrawer and UpButton
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
    }




    override fun onResume() {
        super.onResume()

        // Setting certain behaviors based on Navigation destination changes
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {

                // Navigation UI configuration when going to ReceiptHistoryFragment
                R.id.receiptHistoryFragment -> {

                    // Logcat message to notify when the destination has changed to ReceiptHistoryFragment
                    Timber.i("destination.id is ReceiptHistoryFragment")

                    // Reestablishing the icon to be displayed if it changes
                    primaryAction.button.setImageDrawable(plusIcon)

                    // Establishing the primary button alignment at this destination
                    mainBab.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER

                    // Establishes the behavior of primary button based on BottomAppBar alignment
                    primaryAction.setMainClickBehavior()

                    // Establishing NavDirections for the primary button at this destination
                    primaryNavDirection = ReceiptHistoryFragmentDirections
                        .actionReceiptHistoryFragmentToReceiptFragment()
                }

                // Navigation UI configuration when in ReceiptFragment
                R.id.receiptFragment -> {
                    // Logcat message to notify when the destination has changed to the Receipt Fragment
                    Timber.i("destination.id is ReceiptFragment")

                    // Establishing the primary button alignment at this destination
                    mainBab.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END

                    // The primaryAction should not navigate within this Fragment
                    primaryNavDirection = null

                    NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)

                }

                // Navigation UI configuration when in ReceiptSummaryFragment
                R.id.receiptSummaryFragment -> {

                    // Logcat message to notify when the destination has changed to the Receipt Summary Fragment
                    Timber.i("destination.id is ReceiptSummaryFragment")

                    // Initiating the animation to close the Primary Action Button if it was opened as a menu
                    primaryAction.animateFabAntiClockwise(primaryAction.button)

                    // Returning its isClosed variable to the default state
                    primaryAction.isClosed = true

                    // This button does not have a NavDirection associated with it yet.
                    primaryNavDirection = null
                }
            }
        }
    }





    // Override function inherited from the FragmentCommunication interface within ReceiptFragment()
    override fun onBottomSheetCollapsed() {

        // Logcat message notifies when a bottom sheet from the Receipt Fragment has been collapsed
        Timber.i("onBottomSheetCollapse() is called")

        // When a BottomSheet is STATE_COLLAPSED, primary button is shown closed
        primaryAction.isClosed = true
        primaryAction.button.show()
        primaryAction.animateFabAntiClockwise(primaryAction.button)

        // Snackbar display is delayed to not overlap the FAB. Snackbar display is for user feedback.
        Timer("BottomSheetBehavior STATE_COLLAPSE delay", false).schedule(400) {
            ItemizeSnackbar()
                .displaySnackbarNoAction(
                    activityMainBinding.root,
                    "The form is closed.",
                    primaryAction.button
                )
        }

        // Default navigation bar is restored
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
    }





    // Override function inherited from the FragmentCommunication interface within ReceiptFragment()
    override fun onBottomSheetExpanded() {
        Timber.i("onBottomSheetExpanded is called")

        // When a BottomSheet is STATE_EXPANDED, primary button is hidden and closed
        primaryAction.isClosed = true
        primaryAction.button.hide()
        primaryAction.animateFabAntiClockwise(primaryAction.button)

        // The modified appbar configuration is applied
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
    }





    // Override function inherited from the FragmentCommunication interface within ReceiptFragment()
    override fun setMenuAwareness(menu: MenuClass, background: LinearLayoutCompat, host: ReceiptFragment) {

        // menu: MenuClass parameter is passed from the ReceiptFragment() instance when called

        // Establishes the behavior of the primary button based on its BottomAppBar alignment
        primaryAction.setMainClickBehavior()

        // Applies an onClickListener that makes it act like an expanding menu
        primaryAction.button.setOnClickListener { button ->

            when (primaryAction.isClosed) {
                true -> {

                    // Setting the animation behavior for the primary button to open
                    animateFabClockwise(button as FloatingActionButton)

                    // Menu is animated to open
                    menu.setOpeningMenuAnimations()

                    // Menu actions with Navigation actions are clickable and shown
                    menu.setNavigationActionListener()

                    // Menu actions with BottomSheetBehavior actions are clickable and shown
                    menu.setBottomSheetListener()

                    background.startAnimation(fadeIn)

                    background.visibility = LinearLayoutCompat.VISIBLE

                    host.setMenuFocus()

                    // PrimaryAction as expanded menu is now open
                    primaryAction.isClosed = false
                }
                false -> {

                    // Setting the animation behavior for the expanding button to close
                    animateFabAntiClockwise(button as FloatingActionButton)

                    // Setting the animation behavior for all of the menu items to close
                    menu.setClosingMenuAnimations()

                    background.startAnimation(fadeOut)

                    background.visibility = LinearLayoutCompat.GONE

                    host.setMenuUnfocused()

                    // PrimaryAction as expanded menu is now closed
                    primaryAction.isClosed = true
                }
            }
        }
    }





    // Function is used to hide the keyboard after user behaviors trigger specific functions
    override fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }





    // Function to establish the snackbar behavior depending on the FAB visibility state
    override fun displaySnackbar(message: String) {
        when (primaryAction.button.visibility) {

            // When FAB is visible, snackbar displays the message above it.
            FloatingActionButton.VISIBLE -> {
                ItemizeSnackbar()
                    .displaySnackbarNoAction(activityMainBinding.root, message, primaryAction.button)
            }
            // When the FAB is Invisible, snackbar displays the message above the bottomappbar
            FloatingActionButton.INVISIBLE -> {
                ItemizeSnackbar()
                    .displaySnackbarNoAction(activityMainBinding.root, message, mainBab)
            }
            // Similar to the Invisible state
            FloatingActionButton.GONE -> {
                ItemizeSnackbar()
                    .displaySnackbarNoAction(activityMainBinding.root, message, mainBab)
            }
        }
    }





    override fun setNavigationScrollVisibility(
        recyclerView: RecyclerView,
        expenseBottomSheet: BottomSheetClass,
        patronBottomSheet: BottomSheetClass
    ) {

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                when {
                    dy > 1 -> {
                        mainBab.performHide()
                        primaryAction.button.hide()
                        primaryAction.button.isEnabled = false
                        primaryAction.button.isClickable = false
                        primaryAction.button.isLongClickable = false
                    }
                    dy < 0 -> {
                        mainBab.performShow()
                        primaryAction.button.show()
                        primaryAction.button.isEnabled = true
                        primaryAction.button.isClickable = true
                        primaryAction.button.isLongClickable = true
                    }
                    dy == 0 && (
                            expenseBottomSheet.bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED &&
                                    patronBottomSheet.bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED
                            ) -> {
                        mainBab.performShow()
                        primaryAction.button.show()
                        primaryAction.button.isEnabled = true
                        primaryAction.button.isClickable = true
                        primaryAction.button.isLongClickable = true
                    }

                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })


    }





    // Function to establish UpButton navigation for the bottom app bar
    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }


}