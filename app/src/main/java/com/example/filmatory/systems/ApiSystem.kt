package com.example.filmatory.systems

import com.example.filmatory.api.*
import com.example.filmatory.api.data.movie.Movie
import com.example.filmatory.api.data.movie.MovieFrontpage
import com.example.filmatory.api.data.review.ApprovedReview
import com.example.filmatory.api.data.review.DeniedReview
import com.example.filmatory.api.data.review.PendingReview
import com.example.filmatory.api.data.tv.Tv
import com.example.filmatory.api.data.tv.TvFrontpage
import com.example.filmatory.api.data.user.User
import com.google.gson.GsonBuilder
import okhttp3.FormBody
import kotlin.reflect.KFunction1

class ApiSystem : OnApiRequestFinishedListener {
    private var api = Api()
        get() = field

    //GET
    fun requestApprovedReviewById(id : String, function: (approvedReview : ApprovedReview) -> Unit) {
        api.runRequestGet("/review/approved/get/$id", this, 1, function as (Any) -> Unit);
    }
    fun requestDeniedReviewById(id : String, function: (deniedReview : DeniedReview) -> Unit) {
        api.runRequestGet("/review/denied/get/$id", this, 2, function as (Any) -> Unit);
    }
    fun requestPendingReviewById(id : String, function: (pendingReview : PendingReview) -> Unit) {
        api.runRequestGet("/review/pending/get/$id", this, 3, function as (Any) -> Unit);
    }
    fun requestUser(id : String, function: (user : User) -> Unit) {
        api.runRequestGet("/user/get/$id", this, 4, function as (Any) -> Unit);
    }
    fun requestMovie(id : String, function: (movie : Movie) -> Unit) {
        api.runRequestGet("/movie/get/$id", this, 5, function as (Any) -> Unit);
    }
    fun requestMovieFrontpage(function: (movieFrontPage : MovieFrontpage) -> Unit) {
        api.runRequestGet("/movie/frontpage", this, 6, function as (Any) -> Unit);
    }
    fun requestTV(id : String, function: (tv : Tv) -> Unit) {
        api.runRequestGet("/tv/get/$id", this, 7, function as (Any) -> Unit);
    }
    fun requestTvFrontpage(function: (tvFrontPage : TvFrontpage) -> Unit) {
        api.runRequestGet("/tv/frontpage", this, 8, function as (Any) -> Unit);
    }

    //POST
    fun postUser(email : String, password : String, passwordRepeat : String, function: (any : Any) -> Unit) {
        val formBody = FormBody.Builder()
            .add("email", email)
            .add("password", password)
            .add("passwordRepeat", passwordRepeat)
            .build()
        println(formBody)
        api.runRequestPostForm("/user/new", formBody,this, 9, function);
    }

    override fun onSuccessRequest(result: String?, requestId: Int?, function: (any : Any) -> Unit) {
        val gson = GsonBuilder().create()
        when (requestId) {
            1 -> function(gson.fromJson(result, ApprovedReview::class.java))
            2 -> function(gson.fromJson(result, DeniedReview::class.java))
            3 -> function(gson.fromJson(result, PendingReview::class.java))
            4 -> function(gson.fromJson(result, User::class.java))
            5 -> function(gson.fromJson(result, Movie::class.java))
            6 -> function(gson.fromJson(result, MovieFrontpage::class.java))
            7 -> function(gson.fromJson(result, Tv::class.java))
            8 -> function(gson.fromJson(result, TvFrontpage::class.java))
            else -> { // Note the block
                print("Something went wrong, cant find requestId")
            }
        }
    }

    override fun onErrorRequest() {
        TODO("Not yet implemented")
    }
}