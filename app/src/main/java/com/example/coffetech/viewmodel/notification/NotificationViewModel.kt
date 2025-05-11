import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffetech.model.ApiResponse
import com.example.coffetech.model.FarmInstance
import com.example.coffetech.model.InvitationInstance
import com.example.coffetech.model.Notification
import com.example.coffetech.model.NotificationInstance
import com.example.coffetech.model.NotificationResponse
import com.example.coffetech.model.RetrofitInstance
import com.example.coffetech.utils.SharedPreferencesHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class NotificationViewModel : ViewModel() {

    // Estado de las notificaciones
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    // Estado del orden de las notificaciones
    private val _sortOrder = MutableStateFlow("Más reciente")
    val sortOrder: StateFlow<String> = _sortOrder.asStateFlow()

    // Estado de error
    private val _errorMessage = MutableStateFlow("")

    // Estado de carga
    private val _isLoading = MutableStateFlow(true)

    // Notificaciones ordenadas
    val sortedNotifications: StateFlow<List<Notification>> = combine(_notifications, _sortOrder) { notifications, sortOrder ->
        when (sortOrder) {
            "Más antiguo" -> notifications.sortedBy { it.notification_date }
            "Más reciente" -> notifications.sortedByDescending { it.notification_date }
            else -> notifications
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Función para actualizar el orden de las notificaciones.
     *
     * @param newOrder El nuevo orden seleccionado ("Más reciente" o "Más antiguo").
     */
    fun updateSortOrder(newOrder: String) {
        _sortOrder.value = newOrder
    }

    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf("")

    /**
     * Función para cargar las notificaciones desde el servidor.
     *
     * @param context El contexto de la aplicación.
     */

    fun loadNotifications(context: Context) {
        val sharedPreferencesHelper = SharedPreferencesHelper(context)
        val sessionToken = sharedPreferencesHelper.getSessionToken()

        if (sessionToken == null) {
            errorMessage.value = "No se encontró el token de sesión."
            Log.e("NotificationViewModel", "Error: No se encontró el token de sesión.")
            return
        }

        isLoading.value = true
        Log.d("NotificationViewModel", "Iniciando la carga de notificaciones...")

        NotificationInstance.api.getNotifications(sessionToken).enqueue(object : Callback<NotificationResponse> {
            override fun onResponse(call: Call<NotificationResponse>, response: Response<NotificationResponse>) {
                isLoading.value = false
                Log.d("NotificationViewModel", "Respuesta recibida. Código de respuesta: ${response.code()}")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("NotificationViewModel", "Respuesta exitosa. Cuerpo de la respuesta: $responseBody")

                    responseBody?.let { notificationResponse ->
                        _notifications.value = notificationResponse.data
                        Log.d("NotificationViewModel", "Notificaciones recibidas: ${_notifications.value}")
                    } ?: run {
                        Log.w("NotificationViewModel", "El cuerpo de la respuesta es null, asignando lista vacía a notificaciones")
                        _notifications.value = emptyList()
                    }
                } else {
                    errorMessage.value = "Error al obtener las notificaciones."
                    Log.e("NotificationViewModel", "Error en la respuesta: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                isLoading.value = false
                errorMessage.value = "Error de conexión"
                Log.e("NotificationViewModel", "Error de conexión al cargar notificaciones", t)
            }
        })
    }



    /**
     * Función para aceptar o rechazar una invitación.
     *
     * @param context El contexto de la aplicación.
     * @param invitationId El ID de la invitación.
     * @param action La acción a realizar ("accept" o "reject").
     * @param onSuccess Callback a ejecutar en caso de éxito.
     * @param onFailure Callback a ejecutar en caso de fallo con el mensaje de error.
     */
    fun respondToInvitation(
        context: Context,
        invitationId: Int?,
        action: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val sharedPreferencesHelper = SharedPreferencesHelper(context)
        val sessionToken = sharedPreferencesHelper.getSessionToken()
            ?: return onFailure("No se encontró el token de sesión.")

        val validInvitationId = invitationId
            ?: return onFailure("ID inválido")

        Log.d("NotificationViewModel", "Datos que se enviarán:")
        Log.d("NotificationViewModel", "invitationId: $validInvitationId")
        Log.d("NotificationViewModel", "action: $action")
        Log.d("NotificationViewModel", "sessionToken: $sessionToken")

        InvitationInstance.api.respondInvitation(validInvitationId, action, sessionToken)
            .enqueue(object : Callback<ApiResponse<Any>> {
                override fun onResponse(
                    call: Call<ApiResponse<Any>>,
                    response: Response<ApiResponse<Any>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            if (it.status == "success") {
                                Toast.makeText(context, "Acción realizada con éxito", Toast.LENGTH_SHORT).show()
                                onSuccess()
                            } else {
                                onFailure(it.message)
                            }
                        } ?: run {
                            onFailure("Respuesta vacía del servidor.")
                        }
                    } else {
                        // Aquí extraemos message del errorBody JSON:
                        val errorBody = response.errorBody()?.string()
                        val serverMessage = try {
                            JSONObject(errorBody ?: "").optString(
                                "message",
                                "Error desconocido del servidor"
                            )
                        } catch (e: Exception) {
                            "Error desconocido del servidor"
                        }
                        onFailure(serverMessage)
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                    onFailure("Error de conexión.")
                }
            })
    }
}