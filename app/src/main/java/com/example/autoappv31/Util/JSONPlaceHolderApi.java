package com.example.autoappv31.Util;


import com.example.autoappv31.Models.AllExpenseSum;
import com.example.autoappv31.Models.Auto;
import com.example.autoappv31.Models.Category;
import com.example.autoappv31.Models.CategoryWork;
import com.example.autoappv31.Models.Expense;
import com.example.autoappv31.Models.LoginResult;
import com.example.autoappv31.Models.Mark;
import com.example.autoappv31.Models.Model;
import com.example.autoappv31.Models.NotificationAuto;
import com.example.autoappv31.Models.RecognizeModel;
import com.example.autoappv31.Models.Role;
import com.example.autoappv31.Models.TechnicalWork;
import com.example.autoappv31.Models.User;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface JSONPlaceHolderApi {

    @FormUrlEncoded
    @POST("api/signin")
    Call<User> signin(@Field("username") String username, @Field("password") String password);

//    @POST("/api/signup")
//    Call<User> signup(@Body User user);

    @FormUrlEncoded
    @POST("/api/signup")
    Call<Void> signup(@Field("username") String username, @Field("email") String email, @Field("password") String password, @Field("imageUrl") String imageUrl);

    @GET("/api/user/{userId}")
    Call<User> getUserbyId(@Path("userId") String userId);

    @GET("/api/auto/{autoId}")
    Call<Auto> getAutobyId(@Path("autoId") String autoId);

    @GET("/api/category/{categoryId}")
    Call<Category> getCategorybyId(@Path("categoryId") String categoryId);

    @GET("/api/mark/{markId}")
    Call<Mark> getMarkbyId(@Path("markId") String markId);

    @GET("/api/model/{modelId}")
    Call<Model> getModelbyId(@Path("modelId") String modelId);

    @GET("/api/technicalWork/{technicalWorkId}")
    Call<TechnicalWork> getTechnicalWorkbyId(@Path("technicalWorkId") String technicalWorkId);

    @GET("/api/technicalWork/{technicalWorkId}/categoryWork")
    Call<List<CategoryWork>> getCategoryWorkTechnicalWork(@Path("technicalWorkId") String technicalWorkId);

    @GET("/api/technicalWork/{technicalWorkId}/notificationAuto")
    Call<NotificationAuto> getNotificationAutoTechnicalWork(@Path("technicalWorkId") String technicalWorkId);

    @GET("/api/expense/{expenseId}")
    Call<Expense> getExpensebyId(@Path("expenseId") String expenseId);

    @FormUrlEncoded
    @POST("/api/resetpassword")
    Call<Void> resetPassword(@Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @PUT("/api/user/{userId}")
    Call<User> update(@Path("userId") String userId, @Field("username") String username, @Field("email") String email, @Field("imageUrl") String imageUrl, @Field("roleId") String roleId);

    @FormUrlEncoded
    @PUT("/api/user/{userId}/password")
    Call<User> updatePassword(@Path("userId") String userId, @Field("oldpassword") String oldpassword, @Field("password") String password) ;

    @GET("/api/user/{userId}/autos")
    Call<List<Auto>> findAutos(@Path("userId") String userId);

    @GET("/api/user/{userId}/autos/expense")
    Call<AllExpenseSum> findAutosExpense(@Path("userId") String userId);

    @GET("/api/mark")
    Call<List<Mark>> getAllMarks();

    @GET("/api/role")
    Call<List<Role>> getAllRole();

    @GET("/api/mark/{markId}/models")
    Call<List<Model>> getAllModel(@Path("markId") String markId);

    @FormUrlEncoded
    @POST("/api/auto")
    Call<Auto> createAuto(@Field("run") Integer run, @Field("year") Integer year, @Field("fuel") Integer fuel, @Field("comments") String comments, @Field("imageUrl") String imageUrl, @Field("modelId") String modelId,  @Field("markId") String markId,  @Field("userId") String userId,  @Field("modelName") String modelName);

    @FormUrlEncoded
    @POST("/api/expense")
    Call<Expense> createExpense(@Field("sum") Double sum, @Field("currentRun") Integer currentRun, @Field("comments") String comments, @Field("date") Date date, @Field("autoId") String autoId, @Field("categoryId") String categoryId);

    @FormUrlEncoded
    @POST("/api/expense/technicalWorkEnd")
    Call<Expense> createExpenseEnd(@Field("sum") Double sum, @Field("currentRun") Integer currentRun, @Field("comments") String comments, @Field("date") Date date, @Field("autoId") String autoId, @Field("technicalWorkId") String technicalWorkId);

    @GET("/api/category")
    Call<List<Category>> getAllCategory();

    @FormUrlEncoded
    @POST("/api/category")
    Call<Void> createCategory(@Field("name") String name);

    @GET("/api/categoryWork")
    Call<List<CategoryWork>> getAllCategoryWork();

    @GET("/api/user")
    Call<List<User>> getAllUsers();

    @FormUrlEncoded
    @POST("/api/categoryWork")
    Call<Void> createCategoryWork(@Field("name") String name);

    @DELETE("/api/category/{categoryId}")
    Call<Void> deleteCategory(@Path("categoryId") String categoryId);

    @DELETE("/api/category/{categoryWorkId}")
    Call<Void> deleteCategoryWork(@Path("categoryWorkId") String categoryWorkId);

    @FormUrlEncoded
    @PUT("/api/auto/{autoId}")
    Call<Void> updateAuto(@Path("autoId") String autoId,@Field("run") Integer run, @Field("year") Integer year, @Field("fuel") Integer fuel, @Field("comments") String comments, @Field("imageUrl") String imageUrl, @Field("modelId") String modelId,  @Field("markId") String markId,  @Field("userId") String userId,  @Field("modelName") String modelName);

    @FormUrlEncoded
    @PUT("/api/auto/{autoId}/run")
    Call<Void> updateAutoRun(@Path("autoId") String autoId, @Field("run") Integer run);

    @GET("/api/auto/{autoId}/expenses")
    Call<List<Expense>> getAllExpenseAuto(@Path("autoId") String autoId);

    @FormUrlEncoded
    @POST("/api/auto/{autoId}/expenses/month")
    Call<List<Expense>> getAllExpenseAutoMonth(@Path("autoId") String autoId, @Field("startDate") java.sql.Date startDate, @Field("endDate") java.sql.Date endDate);

    @FormUrlEncoded
    @POST("/api/auto/{autoId}/expenses/category")
    Call<List<Expense>> getAllExpenseAutoCategory(@Path("autoId") String autoId, @Field("categoryId") String categoryId);

    @FormUrlEncoded
    @POST("/api/expense/category")
    Call<List<Expense>> getAllExpenseCategory(@Field("categoryId") String categoryId, @Field("userId") String userId);

    @DELETE("/api/technicalWork/{technicalWorkId}")
    Call<Void> deleteTechnicalWork(@Path("technicalWorkId") String technicalWorkId);

    @DELETE("/api/expense/{expenseId}")
    Call<Void> deleteExpense(@Path("expenseId") String expenseId);

    @DELETE("/api/auto/{autoId}")
    Call<Void> deleteAuto(@Path("autoId") String autoId);

    @DELETE("/api/user/{userId}")
    Call<Void> deleteUser(@Path("userId") String userId);

    @FormUrlEncoded
    @PUT("/api/expense/{expenseId}")
    Call<Void> updateExpennse(@Path("expenseId") String expenseId,@Field("sum") Double sum, @Field("currentRun") Integer currentRun, @Field("comments") String comments, @Field("date") Date date, @Field("autoId") String autoId, @Field("categoryId") String categoryId);

    @FormUrlEncoded
    @POST("/api/recognize")
    Call<RecognizeModel> recognizeAuto(@Field("img") String img);

    @FormUrlEncoded
    @POST("/api/recognize/createAuto")
    Call<RecognizeModel> recognizeCreateAuto(@Field("img") String img);

    @FormUrlEncoded
    @POST("/api/technicalWork")
    Call<TechnicalWork> createTechnicalWork(@Field("name") String name, @Field("comments") String comments, @Field("date") Date date, @Field("dateStart") Date dateStart,@Field("status") boolean status ,@Field("autoId") String autoId, @Field("listWorkId") List<String> listWorkId);

    @FormUrlEncoded
    @PUT("/api/technicalWork/{technicalWorkId}")
    Call<Void> updateTechnicalWork(@Path("technicalWorkId") String technicalWorkId,@Field("name") String name, @Field("comments") String comments, @Field("date") Date date, @Field("dateStart") Date dateStart, @Field("autoId") String autoId, @Field("listWorkId") List<String> listWorkId);

    @GET("/api/auto/{autoId}/technicalworks")
    Call<List<TechnicalWork>> getAllTechnicalWorkAuto(@Path("autoId") String autoId);

    @GET("/api/auto/{autoId}/technicalworks/now")
    Call<TechnicalWork> getAllTechnicalWorkAutoNow(@Path("autoId") String autoId);

    @GET("/api/technicalWork/{technicalWorkId}/categoryWork")
    Call<List<CategoryWork>> getCategoryWorklWorkAutoNow(@Path("technicalWorkId") String technicalWorkId);

    @PUT("/api/technicalWork/{technicalWorkId}/end")
    Call<Void> endTechnicalWorkNow(@Path("technicalWorkId") String technicalWorkId);

    @GET("/api/auto/{autoId}/technicalworks/all")
    Call<List<TechnicalWork>> getAllTechnicalWorkAutoAll(@Path("autoId") String autoId);
}