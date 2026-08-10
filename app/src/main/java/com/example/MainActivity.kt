package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.data.viewmodel.CricketViewModel
import com.example.ui.navigation.Screen
import com.example.ui.screens.*
import com.example.ui.theme.*

import androidx.compose.runtime.collectAsState

class MainActivity : ComponentActivity() {

    private val viewModel: CricketViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeSettings by viewModel.themeSettings.collectAsState()
            CricketAppTheme(settings = themeSettings) {
                MainAppStructure(viewModel)
            }
        }
    }
}

@Composable
fun MainAppStructure(viewModel: CricketViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Matches,
        Screen.Players,
        Screen.Statistics,
        Screen.More
    )

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Matches.route,
        Screen.Players.route,
        Screen.Statistics.route,
        Screen.More.route
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    bottomNavItems.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) screen.selectedIcon else screen.icon,
                                    contentDescription = screen.title
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CricketGreenDark,
                                selectedTextColor = CricketGreenDark,
                                indicatorColor = CricketGreenContainer,
                                unselectedIconColor = TextSecondaryLight,
                                unselectedTextColor = TextSecondaryLight
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToLiveScoring = { matchId -> navController.navigate("live_scoring/$matchId") },
                    onNavigateToScorecard = { matchId -> navController.navigate("scorecard/$matchId") },
                    onNavigateToPlayers = { navController.navigate(Screen.Players.route) },
                    onNavigateToFixtures = { navController.navigate(Screen.Fixtures.route) },
                    onNavigateToStats = { navController.navigate(Screen.Statistics.route) },
                    onNavigateToExpenses = { navController.navigate(Screen.Expenses.route) },
                    onNavigateToNews = { navController.navigate(Screen.News.route) },
                    onNavigateToSearch = { navController.navigate(Screen.GlobalSearch.route) },
                    onStartNewMatch = { navController.navigate(Screen.Matches.route) },
                    onNavigateToAiAssistant = { navController.navigate(Screen.AiAssistant.route) },
                    onNavigateToTournaments = { navController.navigate(Screen.Tournaments.route) },
                    onNavigateToPlayerComparison = { navController.navigate(Screen.PlayerComparison.route) },
                    onNavigateToRecords = { navController.navigate(Screen.Records.route) },
                    onNavigateToAbout = { navController.navigate(Screen.About.route) }
                )
            }

            composable(Screen.Matches.route) {
                MatchesScreen(
                    viewModel = viewModel,
                    onNavigateToLiveScoring = { matchId -> navController.navigate("live_scoring/$matchId") },
                    onNavigateToScorecard = { matchId -> navController.navigate("scorecard/$matchId") }
                )
            }

            composable(Screen.Players.route) {
                PlayersScreen(viewModel = viewModel)
            }

            composable(Screen.Statistics.route) {
                StatisticsScreen(viewModel = viewModel)
            }

            composable(Screen.More.route) {
                MoreScreen(
                    onNavigateToFixtures = { navController.navigate(Screen.Fixtures.route) },
                    onNavigateToNews = { navController.navigate(Screen.News.route) },
                    onNavigateToExpenses = { navController.navigate(Screen.Expenses.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToSearch = { navController.navigate(Screen.GlobalSearch.route) },
                    onNavigateToAbout = { navController.navigate(Screen.About.route) },
                    onNavigateToAiAssistant = { navController.navigate(Screen.AiAssistant.route) },
                    onNavigateToTournaments = { navController.navigate(Screen.Tournaments.route) },
                    onNavigateToPlayerComparison = { navController.navigate(Screen.PlayerComparison.route) },
                    onNavigateToOpponents = { navController.navigate(Screen.Opponents.route) },
                    onNavigateToRecords = { navController.navigate(Screen.Records.route) }
                )
            }

            // Sub Routes
            composable(
                route = Screen.LiveScoring.route,
                arguments = listOf(navArgument("matchId") { type = NavType.LongType })
            ) { backStackEntry ->
                val matchId = backStackEntry.arguments?.getLong("matchId") ?: 0L
                viewModel.setActiveMatch(matchId)
                LiveScoringScreen(
                    matchId = matchId,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToScorecard = { id -> navController.navigate("scorecard/$id") }
                )
            }

            composable(
                route = Screen.Scorecard.route,
                arguments = listOf(navArgument("matchId") { type = NavType.LongType })
            ) { backStackEntry ->
                val matchId = backStackEntry.arguments?.getLong("matchId") ?: 0L
                ScorecardScreen(
                    matchId = matchId,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Fixtures.route) {
                FixturesScreen(viewModel = viewModel)
            }

            composable(Screen.News.route) {
                NewsScreen(viewModel = viewModel)
            }

            composable(Screen.Expenses.route) {
                ExpensesScreen(viewModel = viewModel)
            }

            composable(Screen.Settings.route) {
                SettingsScreen(viewModel = viewModel)
            }

            composable(Screen.GlobalSearch.route) {
                GlobalSearchScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Version 3.0 Additions
            composable(Screen.About.route) {
                AboutScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable(Screen.AiAssistant.route) {
                AiAssistantScreen(viewModel = viewModel, onNavigateBack = { navController.popBackStack() })
            }

            composable(Screen.Tournaments.route) {
                TournamentsScreen(viewModel = viewModel, onNavigateBack = { navController.popBackStack() })
            }

            composable(Screen.PlayerComparison.route) {
                PlayerComparisonScreen(viewModel = viewModel, onNavigateBack = { navController.popBackStack() })
            }

            composable(Screen.Opponents.route) {
                OpponentsScreen(viewModel = viewModel, onNavigateBack = { navController.popBackStack() })
            }

            composable(Screen.Records.route) {
                RecordsScreen(viewModel = viewModel, onNavigateBack = { navController.popBackStack() })
            }
        }
    }
}
