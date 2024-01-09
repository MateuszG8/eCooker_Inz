package com.example.ecooker.api

import com.example.ecooker.models.LoginInfo
import com.example.ecooker.models.Recipe
import com.example.ecooker.models.Register
import com.example.ecooker.models.UserInfo
import com.example.ecooker.models.AddRecipe
import com.example.ecooker.models.Comment
import com.example.ecooker.models.CommentRequest
import com.example.ecooker.models.EditRecipe
import com.example.ecooker.models.EditUser
import com.example.ecooker.models.LoginResponse
import com.example.ecooker.models.Rate
import com.example.ecooker.models.RateRequest
import com.example.ecooker.models.SaveRecipeRequest
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("user/register")
    fun register(@Body register: Register): Call<ResponseBody>

    @POST("user/login")
    fun login(@Body loginInfo: LoginInfo): Call<ResponseBody>

    @PUT("user/myProfile")
    fun editUser(@Header("authorization") token: String, @Body editUser: EditUser) : Call<ResponseBody>

    @GET("recipe/public")
    fun getAllRecipes(): Call<List<Recipe>>

    @GET("user/token")
    fun refreshToken(@Header("authorization") token: String): Call<LoginResponse>

    @GET("user/myProfile")
    fun getUserInfo(@Header("authorization") token: String): Call<UserInfo>

    @GET("recipe/tags")
    fun getTags(@Header("authorization") token: String): Call<Map<String, List<String>>>

    @POST("recipe/add ")
    fun addRecipe(@Header("authorization") token: String, @Body recipe: AddRecipe): Call<ResponseBody>

    @GET("recipe/public/search/fridge/")
    fun searchRecipesByIngredients(@Query("ingredients") ingredients: String): Call<List<Recipe>>

    @GET("recipe/public/search/")
    fun searchRecipesByName(@Query("name") name: String): Call<List<Recipe>>

    //TODO: ranking przepisow
    @GET()
    fun getRankingOfRecipes(): Call<List<Recipe>>

    @GET("recipe/saved/{id}")
    fun isRecipeSaved(@Header("authorization") token: String, @Path("id") recipeId: String): Call<Boolean>

    @Multipart
    @POST("image/recipe/{id}")
    fun uploadRecipeImage(@Header("authorization") token: String, @Path("id") recipeId: String, @Part image: MultipartBody.Part): Call<ResponseBody>

    @Multipart
    @POST("image/user")
    fun uploadImageProfile(@Header("authorization") token: String, @Part image: MultipartBody.Part): Call<ResponseBody>

    //Zapisz przepis
    @POST("recipe/saved")
    fun saveRecipe(@Header("authorization") token: String, @Body request: SaveRecipeRequest): Call<ResponseBody>

    //Usun z zapisanych
    @DELETE("recipe/saved/{id}")
    fun unfollowRecipe(@Header("authorization") token: String, @Path("id") recipeId: String): Call<ResponseBody>

    @DELETE("recipe/{id}")
    fun deleteRecipe(@Header("authorization") token: String, @Path("id") recipeId: String): Call<ResponseBody>

    @GET("recipe/all")
    fun getMyRecipes(@Header("authorization") token: String): Call<List<Recipe>>

    @GET("recipe/saved")
    fun getSavedRecipes(@Header("authorization") token: String): Call<List<Recipe>>

    @PUT("recipe")
    fun updateRecipe(@Header("authorization") token: String, @Body recipe: EditRecipe): Call<ResponseBody>

    @GET("comment/recipe/{id}")
    fun getCommentsToCurrentRecipe(@Path("id") recipeId: String): Call<List<Comment>>

    @POST("comment/recipe/{id}")
    fun addCommentsToRecipe(@Header("authorization") token: String, @Body commentRequest: CommentRequest, @Path("id") recipeId: String): Call<ResponseBody>

    @PUT("comment/recipe/{recipeId}/comment/{commentId}")
    fun updateComment(@Header("authorization") token: String, @Body commentRequest: CommentRequest, @Path("recipeId") recipeId: String, @Path("commentId") commentId: String): Call<ResponseBody>

    @DELETE("comment/recipe/{recipeId}/comment/{commentId}")
    fun deleteComment(@Header("authorization") token: String, @Path("recipeId") recipeId: String, @Path("commentId") commentId: String): Call<ResponseBody>

    @POST("rating/recipe/{id}")
    fun rateRecipe(@Header("authorization") token: String, @Body rateRequest: Rate, @Path("id") id: String): Call<ResponseBody>

    @GET("rating/recipe/{id}")
    fun getRecipeRating(@Header("authorization") token: String, @Path("id") id: String): Call<RateRequest>
}