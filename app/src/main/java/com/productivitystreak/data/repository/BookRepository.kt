package com.productivitystreak.data.repository

import android.database.sqlite.SQLiteException
import com.productivitystreak.data.local.dao.BookDao
import com.productivitystreak.data.local.dao.ReadingSessionDao
import com.productivitystreak.data.local.entity.BookEntity
import com.productivitystreak.data.local.entity.ReadingSessionEntity
import kotlinx.coroutines.flow.Flow

class BookRepository(
    private val bookDao: BookDao,
    private val readingSessionDao: ReadingSessionDao
) {

    fun observeAllBooks(): Flow<List<BookEntity>> = bookDao.getAllBooks()

    fun observeCurrentlyReading(): Flow<List<BookEntity>> = bookDao.getCurrentlyReadingBooks()

    fun observeFinished(): Flow<List<BookEntity>> = bookDao.getFinishedBooks()

    fun searchBooks(query: String): Flow<List<BookEntity>> = bookDao.searchBooks(query)

    suspend fun getBookById(id: Long): BookEntity? = bookDao.getBookById(id)

    suspend fun addBook(
        title: String,
        author: String,
        totalPages: Int,
        genre: String? = null,
        isbn: String? = null,
        coverImageUrl: String? = null
    ): RepositoryResult<Long> {
        return try {
            val book = BookEntity(
                title = title,
                author = author,
                totalPages = totalPages,
                genre = genre,
                isbn = isbn,
                coverImageUrl = coverImageUrl,
                startedAt = System.currentTimeMillis()
            )
            val id = bookDao.insertBook(book)
            RepositoryResult.Success(id)
        } catch (e: SQLiteException) {
            RepositoryResult.DbError(e)
        } catch (e: Exception) {
            RepositoryResult.UnknownError(e)
        }
    }

    suspend fun updateBook(book: BookEntity): RepositoryResult<Unit> {
        return try {
            bookDao.updateBook(book)
            RepositoryResult.Success(Unit)
        } catch (e: SQLiteException) {
            RepositoryResult.DbError(e)
        } catch (e: Exception) {
            RepositoryResult.UnknownError(e)
        }
    }

    suspend fun logReadingSession(
        bookId: Long,
        pagesRead: Int,
        startPage: Int,
        notes: String? = null
    ): RepositoryResult<Unit> {
        return try {
            val book = bookDao.getBookById(bookId) 
                ?: return RepositoryResult.DbError(IllegalStateException("Book not found"))

            val endPage = startPage + pagesRead
            val session = ReadingSessionEntity(
                bookId = bookId,
                pagesRead = pagesRead,
                startPage = startPage,
                endPage = endPage,
                notes = notes
            )
            readingSessionDao.insertSession(session)

            // Update book progress
            val updatedBook = book.copy(currentPage = endPage)
            bookDao.updateBook(updatedBook)

            // Mark as finished if completed
            if (endPage >= book.totalPages && book.finishedAt == null) {
                val finishedBook = updatedBook.copy(finishedAt = System.currentTimeMillis())
                bookDao.updateBook(finishedBook)
            }

            RepositoryResult.Success(Unit)
        } catch (e: SQLiteException) {
            RepositoryResult.DbError(e)
        } catch (e: Exception) {
            RepositoryResult.UnknownError(e)
        }
    }

    fun observeReadingSessions(bookId: Long): Flow<List<ReadingSessionEntity>> =
        readingSessionDao.getSessionsForBook(bookId)

    suspend fun getTotalPagesRead(bookId: Long): Int =
        readingSessionDao.getTotalPagesReadForBook(bookId) ?: 0

    suspend fun deleteBook(book: BookEntity): RepositoryResult<Unit> {
        return try {
            bookDao.deleteBook(book)
            RepositoryResult.Success(Unit)
        } catch (e: SQLiteException) {
            RepositoryResult.DbError(e)
        } catch (e: Exception) {
            RepositoryResult.UnknownError(e)
        }
    }

    suspend fun archiveBook(bookId: Long): RepositoryResult<Unit> {
        return try {
            bookDao.archiveBook(bookId)
            RepositoryResult.Success(Unit)
        } catch (e: SQLiteException) {
            RepositoryResult.DbError(e)
        } catch (e: Exception) {
            RepositoryResult.UnknownError(e)
        }
    }

    suspend fun getBookCount(): Int = bookDao.getBookCount()

    suspend fun getFinishedBookCount(): Int = bookDao.getFinishedBookCount()
}
