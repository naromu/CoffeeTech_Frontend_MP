import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coffetech.R

@Composable
fun ReusableInfoIcon(
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true },
        modifier = modifier ) {
        Icon(
            painter = painterResource(id = R.drawable.info),
            contentDescription = "Información",
            tint = Color.Gray
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier
            .width(250.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = "Tu contraseña debe incluir:",
            modifier = Modifier.padding(8.dp),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp
        )
        Divider()
        Text(
            text = "- Mínimo 8 caracteres",
            modifier = Modifier.padding(8.dp),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp
        )
        Text(
            text = "- Al menos una letra mayúscula",
            modifier = Modifier.padding(8.dp),
            color = MaterialTheme.colorScheme.onSurface, // Texto usando color de tu tema
            fontSize = 14.sp
        )
        Text(
            text = "- Al menos una letra minúscula",
            modifier = Modifier.padding(8.dp),
            color = MaterialTheme.colorScheme.onSurface, // Texto usando color de tu tema
            fontSize = 14.sp
        )
        Text(
            text = "- Al menos un número",
            modifier = Modifier.padding(8.dp),
            color = MaterialTheme.colorScheme.onSurface, // Texto usando color de tu tema
            fontSize = 14.sp
        )
        Text(
            text = "- Al menos un carácter especial",
            modifier = Modifier.padding(8.dp),
            color = MaterialTheme.colorScheme.onSurface, // Texto usando color de tu tema
            fontSize = 14.sp
        )
    }
}


