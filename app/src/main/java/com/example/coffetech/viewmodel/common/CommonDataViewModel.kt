import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.coffetech.model.ApiResponse
import com.example.coffetech.model.CoffeeVariety
import com.example.coffetech.model.Role
import com.example.coffetech.model.UnitMeasure
import com.example.coffetech.utils.SharedPreferencesHelper
import com.example.coffetech.model.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommonDataViewModel : ViewModel() {

    // Verificar si los datos necesitan ser actualizados según la versión
    fun updateDataIfVersionChanged(context: Context) {
        val sharedPreferencesHelper = SharedPreferencesHelper(context)

        // Obtener el código de versión actual y la versión guardada
        val currentVersionCode = sharedPreferencesHelper.getCurrentVersionCode(context)
        val savedVersionCode = sharedPreferencesHelper.getSavedVersionCode()

        Log.d("CommonDataViewModel", "Version actual: $currentVersionCode, Version guardada: $savedVersionCode")

        if (currentVersionCode != savedVersionCode) {
            Log.d("CommonDataViewModel", "Nueva instalación o actualización detectada")

            // Actualizar roles y unidades de medida si la versión ha cambiado
            fetchRolesAndStore(sharedPreferencesHelper)
            fetchUnitMeasuresAndStore(sharedPreferencesHelper)
            fetchCoffeeVarietiesAndStore(sharedPreferencesHelper)

            // Guardar el nuevo código de versión
            sharedPreferencesHelper.saveVersionCode(currentVersionCode)
        } else {
            Log.d("CommonDataViewModel", "La versión no ha cambiado, no se necesitan actualizaciones")
        }
    }

    // Función para obtener roles desde el backend y almacenarlos
    private fun fetchRolesAndStore(sharedPreferencesHelper: SharedPreferencesHelper) {
        RetrofitInstance.api.getRoles().enqueue(object : Callback<ApiResponse<List<Role>>> {
            override fun onResponse(call: Call<ApiResponse<List<Role>>>, response: Response<ApiResponse<List<Role>>>) {
                if (response.isSuccessful && response.body()?.data != null) {
                    val roles = response.body()?.data!!
                    sharedPreferencesHelper.saveRoles(roles)
                    Log.d("fetchRolesAndStore", "Roles almacenados correctamente")
                } else {
                    Log.e("fetchRolesAndStore", "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<Role>>>, t: Throwable) {
                Log.e("fetchRolesAndStore", "Network Error: ${t.localizedMessage}")
            }
        })
    }

    // Función para obtener unidades de medida desde el backend y almacenarlas
    private fun fetchUnitMeasuresAndStore(sharedPreferencesHelper: SharedPreferencesHelper) {
        RetrofitInstance.api.getUnitMeasures().enqueue(object : Callback<ApiResponse<List<UnitMeasure>>> {
            override fun onResponse(call: Call<ApiResponse<List<UnitMeasure>>>, response: Response<ApiResponse<List<UnitMeasure>>>) {
                if (response.isSuccessful && response.body()?.data != null) {
                    val unitMeasures = response.body()?.data!!
                    sharedPreferencesHelper.saveUnitMeasures(unitMeasures)
                    Log.d("fetchUnitMeasuresAndStore", "Unidades de medida almacenadas correctamente")
                } else {
                    Log.e("fetchUnitMeasuresAndStore", "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<UnitMeasure>>>, t: Throwable) {
                Log.e("fetchUnitMeasuresAndStore", "Network Error: ${t.localizedMessage}")
            }
        })
    }
    private fun fetchCoffeeVarietiesAndStore(sharedPreferencesHelper: SharedPreferencesHelper) {
        RetrofitInstance.api.getCoffeeVarieties().enqueue(object : Callback<ApiResponse<List<CoffeeVariety>>> {
            override fun onResponse(call: Call<ApiResponse<List<CoffeeVariety>>>, response: Response<ApiResponse<List<CoffeeVariety>>>) {
                if (response.isSuccessful && response.body()?.data != null) {
                    val coffeeVarieties = response.body()?.data!!

                    // Extraer los nombres de las variedades de café para almacenarlos
                    val varietyNames = coffeeVarieties.map { it.name }

                    // Guardar las variedades de café en SharedPreferences
                    sharedPreferencesHelper.saveCoffeeVarieties(varietyNames)
                    Log.d("fetchCoffeeVarieties", "Variedades de café almacenadas correctamente")
                } else {
                    Log.e("fetchCoffeeVarieties", "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<CoffeeVariety>>>, t: Throwable) {
                Log.e("fetchCoffeeVarieties", "Network Error: ${t.localizedMessage}")
            }
        })
    }

}
