package com.pillup.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pillup.data.model.Medicamento
import coil.compose.AsyncImage
import androidx.compose.ui.draw.clip
import java.io.File

@Composable
fun MedicamentoCard(
    medicamento: Medicamento,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = medicamento.nombre,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF02316E)
                )
                Text(
                    text = "${medicamento.dosis} Dosis",
                    fontSize = 13.sp,
                    color = Color(0xFF02316E),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Siguiente toma en:",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Text(
                    text = medicamento.proximaToma,
                    fontSize = 14.sp,
                    color = Color(0xFF02316E),
                    fontWeight = FontWeight.Bold
                )
            }

            if (medicamento.fotoUrl.isNotEmpty()) {
                AsyncImage(
                    model = File(medicamento.fotoUrl), // Cargamos desde el archivo local
                    contentDescription = "Foto ${medicamento.nombre}",
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(8.dp)) // Redondeamos la imagen
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Si no tiene foto, mostramos el ícono por defecto (opcional)
                Image(
                    painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                    contentDescription = "Sin foto",
                    modifier = Modifier
                        .size(70.dp)
                        .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
                        .padding(15.dp),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Gray)
                )
            }
        }
    }
}