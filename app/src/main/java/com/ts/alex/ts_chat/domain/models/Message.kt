package com.ts.alex.ts_chat.domain.models

class Message(){
    var name: String? = null
    var text: String? = null
    var sender: String? = null
    var recipient: String? = null
    var imageUrl: String? = null
    var isMine: Boolean = true

    constructor (
        name: String,
        text: String,
        sender: String?,
        recipient: String?,
        imageUrl: String?,
        isMine: Boolean = true
    ) : this() {
        this.name = name
        this.text = text
        this.imageUrl = imageUrl
        this.sender = sender
        this.recipient = recipient
        this.isMine = isMine

    }

    override fun toString(): String {
        return "Message(name=$name, text=$text, imageUrl=$imageUrl)"
    }

}