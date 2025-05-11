package com.example.coffetech.model

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class ListTransactionsResponse(
    val status: String,
    val message: String,
    val data: TransactionData
)

data class Transaction(
    val transaction_id: Int,
    val plot_id: Int,
    val transaction_type_name: String, // "Ingreso" o "Gasto"
    val transaction_category_name: String, // Nuevo campo
    val description: String?,
    val value: Long,
    val transaction_date: String, // Formato "yyyy-MM-dd"
    val transaction_state: String
)

data class TransactionData(
    val transactions: List<Transaction>
)

data class CreateTransactionRequest(
    val plot_id: Int,
    val transaction_category_id: Int, // Nuevo campo
    val description: String,
    val value: Long,
    val transaction_date: String
)


data class CreateTransactionResponse(
    val status: String,
    val message: String,
    val data: Transaction
)

// TransactionType
data class TransactionType(
    val transaction_type_id: Int,
    val name: String
)

data class TransactionTypeResponse(
    val status: String,
    val message: String,
    val data: TransactionTypeData
)

data class TransactionTypeData(
    val transaction_types: List<TransactionType>
)

// TransactionCategory
data class TransactionCategory(
    val transaction_category_id: Int,
    val name: String,
    val transaction_type_id: Int,
    val transaction_type_name: String
)

data class TransactionCategoryResponse(
    val status: String,
    val message: String,
    val data: TransactionCategoryData
)

data class TransactionCategoryData(
    val transaction_categories: List<TransactionCategory>
)


//EditTransaction

data class EditTransactionRequest(
    val transaction_id: Int,
    val transaction_category_id: Int, // Nuevo campo
    val description: String,
    val value: Long,
    val transaction_date: String // Formato "yyyy-MM-dd"
)

data class EditTransactionResponse(
    val status: String,
    val message: String,
    val data: Transaction
)

//DeleteTransaction

data class TransactionDeleteRequest(
    val transaction_id: Int
)
data class TransactionDeleteResponse(
    val status: String,
    val message: String,
    val data: TransactionData?
)

interface TransactionService {

    @GET("/transaction/list-transactions/{plot_id}")
    fun listTransactions(
        @Path("plot_id") plotId: Int,
        @Query("session_token") sessionToken: String
    ): Call<ListTransactionsResponse>

    @POST("/transaction/create-transaction")
    fun createTransaction(
        @Query("session_token") sessionToken: String,
        @Body request: CreateTransactionRequest
    ): Call<CreateTransactionResponse>

    @POST("/transaction/edit-transaction")
    fun editTransaction(
        @Query("session_token") sessionToken: String,
        @Body request: EditTransactionRequest
    ): Call<EditTransactionResponse>


    @POST("/transaction/delete-transaction")
    fun deleteTransaction(
        @Query("session_token") sessionToken: String,
        @Body request: TransactionDeleteRequest
    ): Call<TransactionDeleteResponse>



    @GET("/transaction/transaction-types")
    fun getTransactionTypes(): Call<TransactionTypeResponse>

    @GET("/transaction/transaction-categories")
    fun getTransactionCategories(): Call<TransactionCategoryResponse>



}