package com.example.gifloader.data

class GifInfo {
    var id = 0
    var description: String? = null
    var gifURL: String? = null
    override fun toString(): String {
        return "$description   $gifURL  $id  ${super.toString()}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GifInfo) {
            return false
        } else {
            if (other.description == this.description && other.gifURL == this.gifURL && other.id == this.id) {
                return true
            }
        }
        return false
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (gifURL?.hashCode() ?: 0)
        return result
    }
}