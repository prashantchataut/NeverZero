package com.productivitystreak.data.local.dao

import androidx.room.*
import com.productivitystreak.data.local.entity.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books WHERE isArchived = 0 ORDER BY createdAt DESC")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE isArchived = 0 AND finishedAt IS NULL ORDER BY startedAt DESC")
    fun getCurrentlyReadingBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE isArchived = 0 AND finishedAt IS NOT NULL ORDER BY finishedAt DESC")
    fun getFinishedBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getBookById(id: Long): BookEntity?

    @Query("SELECT * FROM books WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%'")
    fun searchBooks(query: String): Flow<List<BookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Update
    suspend fun updateBook(book: BookEntity)

    @Delete
    suspend fun deleteBook(book: BookEntity)

    @Query("UPDATE books SET isArchived = 1 WHERE id = :id")
    suspend fun archiveBook(id: Long)

    @Query("SELECT COUNT(*) FROM books WHERE isArchived = 0")
    suspend fun getBookCount(): Int

    @Query("SELECT COUNT(*) FROM books WHERE isArchived = 0 AND finishedAt IS NOT NULL")
    suspend fun getFinishedBookCount(): Int

    @Query("DELETE FROM books")
    suspend fun deleteAllBooks()
}
