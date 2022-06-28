package com.cursoudemy.gamelib

class Console {
    // Variables must match those in Firebase
    var id: String = ""
    var console: String = ""
    var timestamp: Long = 0
    var uid:String = ""

    // Empty constructor required by Firebase
    constructor()
    // Non empty constructor
    constructor(id: String, console: String, timestamp: Long, uid: String) {
        this.id = id
        this.console = console
        this.timestamp = timestamp
        this.uid = uid
    }
}