package com.android.quantum.notas.persistance

import androidx.lifecycle.LiveData
import androidx.room.*
import com.android.quantum.notas.models.Nota

@Dao
interface NotaDao {

    @Insert
    fun insertar(nota: Nota)

    @Update
    fun actualizar(nota: Nota)

    @Delete
    fun eliminar(nota: Nota)

    @Query("DELETE FROM tabla_notas")
    fun eliminarNotas()

    @Query("SELECT * FROM tabla_notas ORDER BY prioridad DESC")
    fun mostrarNotas(): List<Nota>

}