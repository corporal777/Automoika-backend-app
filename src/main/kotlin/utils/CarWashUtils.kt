package kg.automoika.utils

import kg.automoika.data.remote.ReviewRemote
import kg.automoika.data.remote.UserRemote
import kg.automoika.data.response.ReviewResponse

object CarWashUtils {


    fun getShortReviews(review: ReviewRemote?, users : List<UserRemote>): List<ReviewResponse> {
        if (review == null) return emptyList<ReviewResponse>()

        val list = if (review.users.size > 3) review.users.subList(0,3) else review.users
        return list.map {
            val user = users.find { x -> x.id == it.userId }
            ReviewResponse(user?.name, user?.image?.imageUrl, it.text)
        }
    }
}