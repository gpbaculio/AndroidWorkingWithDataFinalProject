package com.example.littlelemon

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.littlelemon.ui.theme.LittleLemonTheme
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private val httpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(contentType = ContentType("text", "plain"))
        }
    }

    private val database by lazy {
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "database").build()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LittleLemonTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    // add databaseMenuItems code here
                    val databaseMenuItems by database.menuItemDao().getAll().observeAsState(emptyList())

                    // add orderMenuItems variable here
                    var orderMenuItems by remember { mutableStateOf(false) }

                    // add menuItems variable here
                    var menuItems by remember { mutableStateOf<List<MenuItemRoom>>(emptyList()) }

                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "logo",
                            modifier = Modifier.padding(50.dp)
                        )

                        // add Button code here
                        Button(
                            onClick = {
                                orderMenuItems = true
                                menuItems = databaseMenuItems.sortedBy { it.title }
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)

                        ) {
                            Text(text = "Tap to Order By Name")
                        }

                        // add searchPhrase variable here
                        var searchPhrase by remember { mutableStateOf("") }

                        // Add OutlinedTextField
                        Row(
                            modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 50.dp, end = 50.dp)
                        ) {
                            OutlinedTextField(
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                    )
                                },
                                modifier = Modifier.weight(.6f),
                                value = searchPhrase,
                                onValueChange = { value -> searchPhrase = value },
                                label = {
                                    Text("Search")
                                }
                            )
                        }



                        // add is not empty check here
                        if (databaseMenuItems.isNotEmpty()) {

                            if(orderMenuItems && searchPhrase.isEmpty()) {
                                MenuItemsList(menuItems)
                            } else if(orderMenuItems && searchPhrase.isNotEmpty()) {
                                val filteredMenuItems =  menuItems.filter {
                                    it.title.contains(searchPhrase, ignoreCase = true)
                                }

                                MenuItemsList(filteredMenuItems)
                            }  else if (searchPhrase.isNotEmpty()) {
                                val filteredMenuItems =  databaseMenuItems.filter {
                                    it.title.contains(searchPhrase, ignoreCase = true)
                                }

                                MenuItemsList(filteredMenuItems)
                            }  else {
                                MenuItemsList(databaseMenuItems)
                            }
                        }
                    }

                }
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            if (database.menuItemDao().isEmpty()) {
                // add code here

                val menuItems = fetchMenu()
                saveMenuToDatabase(menuItems)
            }
        }
    }

    private suspend fun fetchMenu(): List<MenuItemNetwork> {
        val response: MenuNetwork =
            httpClient.get("https://raw.githubusercontent.com/Meta-Mobile-Developer-PC/Working-With-Data-API/main/littleLemonSimpleMenu.json")
                .body()

        return response.menu
    }

    private fun saveMenuToDatabase(menuItemsNetwork: List<MenuItemNetwork>) {
        val menuItemsRoom = menuItemsNetwork.map { it.toMenuItemRoom() }
        database.menuItemDao().insertAll(*menuItemsRoom.toTypedArray())
    }
}

@Composable
private fun MenuItemsList(items: List<MenuItemRoom>) {


    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .padding(top = 20.dp)
    ) {


        items(
            items = items,
            itemContent = { menuItem ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(menuItem.title)
                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .padding(5.dp),
                        textAlign = TextAlign.Right,
                        text = "%.2f".format(menuItem.price),
                    )
                }
            }
        )
    }
}
