package com.app.spacenow.ui.components

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.app.spacenow.data.model.Space
import com.app.spacenow.ui.theme.AvailableGreen

@Composable
fun SpaceCard(
    space: Space,
    onSpaceClick: (Space) -> Unit,
    modifier: Modifier = Modifier
) {
    // Log para depuración
    Log.d("SpaceCard", "Mostrando espacio: ${space.name}, imageUri: ${space.imageUri}, imageResource: ${space.imageResource}")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSpaceClick(space) }
    ) {
        Column {
            // Aquí está la modificación principal:
            // Primero intenta usar la imageUri, si está disponible
            val imageModel = if (!space.imageUri.isNullOrEmpty()) {
                // Log para confirmar que estamos usando la URI
                Log.d("SpaceCard", "Usando URI para ${space.name}: ${space.imageUri}")
                Uri.parse(space.imageUri)
            } else {
                // Solo usa el recurso drawable como fallback
                Log.d("SpaceCard", "Usando recurso drawable para ${space.name}: ${space.imageResource}")
                space.imageResource
            }

            AsyncImage(
                model = imageModel,
                contentDescription = space.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop,
                onError = {
                    // Si hay error al cargar la imagen, loguearlo y usar recurso predeterminado
                    Log.e("SpaceCard", "Error cargando imagen para ${space.name}: ${it.result.throwable.message}")
                }
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = space.name,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = space.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = "Capacidad: ${space.capacity} personas",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                if (space.available) {
                    Surface(
                        color = AvailableGreen,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = "Disponible",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}