package com.cursoudemy.gamelib

class Category {
    // Variables must match those in Firebase
    var id: String = ""
    var category: String = ""
    var timestamp: Long = 0
    var uid:String = ""

    // Empty constructor required by Firebase
    constructor()
    // Non empty constructor
    constructor(id: String, category: String, timestamp: Long, uid: String) {
        this.id = id
        this.category = category
        this.timestamp = timestamp
        this.uid = uid
    }
}