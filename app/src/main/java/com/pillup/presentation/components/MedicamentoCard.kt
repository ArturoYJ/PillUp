package com.pillup.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.pillup.data.model.Medicamento

@Composable
fun MedicamentoCard(
    medicamento: Medicamento,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .clickable { onCardClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Siguiente toma en: ${medicamento.proximaToma}",
                    fontSize = 12.sp,
                    color = Color(0xFF2563EB),
                    fontWeight = FontWeight.Bold
                )
            }

            if (medicamento.fotoUrl.isNotEmpty()) {
                Image(
                    painter = painterResource(id = android.R.drawable.ic_dialog_info),
                    contentDescription = medicamento.nombre,
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.LightGray, RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}