package com.ts.alex.ts_chat



class Message(){
    var name: String? = null
    var text: String? = null
    var imageUrl: String? = null

    constructor (
        name: String,
        text: String,
        imageUrl: String?
    ) : this() {
        this.name = name
        this.text = text
        this.imageUrl = imageUrl
    }

}