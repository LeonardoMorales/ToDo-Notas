package com.android.quantum.notas.persistance

import androidx.room.*
import com.android.quantum.notas.models.Nota

@Dao
interface NotaDao {

    @Insert
    suspend fun insertar(nota: Nota)

    @Update
    suspend fun actualizar(nota: Nota)

    @Delete
    suspend fun eliminar(nota: Nota)

    @Query("DELETE FROM tabla_notas")
    suspend fun eliminarNotas()

    @Query("SELECT * FROM tabla_notas ORDER BY prioridad DESC")
    suspend fun mostrarNotas(): List<Nota>

}