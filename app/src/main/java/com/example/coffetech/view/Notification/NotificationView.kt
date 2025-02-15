import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.coffetech.common.TopBarWithBackArrow
import com.example.coffetech.common.NotificationCard
import com.example.coffetech.common.NotificationOrderDropdown
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Composable function that renders the notifications screen.
 * This screen displays a list of notifications to the user, allowing them to accept or reject invitations.
 *
 * @param navController The [NavController] used for navigation between screens.
 * @param viewModel The [NotificationViewModel] that manages the state and logic for notifications.
 */
@Composable
fun NotificationView(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: NotificationViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var selectedOrder by remember { mutableStateOf("Más reciente") }

    LaunchedEffect(Unit) {
        viewModel.loadNotifications(context)
    }

    val notifications by viewModel.notifications.collectAsState()
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    fun formatNotificationDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }

    val sortedNotifications = remember(selectedOrder, notifications) {
        when (selectedOrder) {
            "Más antiguo" -> notifications.sortedBy { it.date }
            "Más reciente" -> notifications.sortedByDescending { it.date }
            else -> notifications
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        TopBarWithBackArrow(
            onBackClick = { navController.popBackStack() },
            title = "Notificaciones"
        )

        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            NotificationOrderDropdown(
                selectedOrder = selectedOrder,
                onSelectedOrderChange = { selectedOrder = it },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                errorMessage.isNotEmpty() -> {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                sortedNotifications.isNotEmpty() -> {
                    sortedNotifications.forEach { notification ->
                        NotificationCard(
                            title = when (notification.notification_type) {
                                "Invitation" -> "Nueva Invitación"
                                "Invitation_accepted" -> "Invitación Aceptada"
                                "Asignacion_tarea" -> "Asignación de Tarea"
                                else -> "Notificación"
                            },
                            description = notification.message,
                            date = formatNotificationDate(notification.date),
                            onRejectClick = if (notification.notification_type == "Invitation" && notification.status == "Pendiente") {
                                {
                                    viewModel.respondToInvitation(
                                        context,
                                        notification.invitation_id ?: 0,
                                        "reject",
                                        onSuccess = {
                                            viewModel.loadNotifications(context)
                                        },
                                        onFailure = { message ->
                                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            } else null,
                            onAcceptClick = if (notification.notification_type == "Invitation" && notification.status == "Pendiente") {
                                {
                                    viewModel.respondToInvitation(
                                        context,
                                        notification.invitation_id ?: 0,
                                        "accept",
                                        onSuccess = {
                                            viewModel.loadNotifications(context)
                                        },
                                        onFailure = { message ->
                                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            } else null
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                else -> {
                    Text(
                        text = "Aun no tienes notificaciones.",
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(10.dp)
                    )
                }
            }
        }
    }
}

