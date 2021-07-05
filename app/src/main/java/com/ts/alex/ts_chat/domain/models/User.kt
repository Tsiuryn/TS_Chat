package com.ts.alex.ts_chat.domain.models

class User(){
    var name: String? = null
    var email: String? = null
    var id: String? = null
    var password: String? = null
    var avatarMockUpResource: Int = 0

   constructor(name: String? = null, email: String, id: String? = null, password: String? = null, avatarMockUpResource: Int = 0): this(){
       this.name = name
       this.email = email
       this.id = id
       this.password = password
       this.avatarMockUpResource = avatarMockUpResource

   }

    override fun toString(): String {
        return "User(name=$name, email=$email, id=$id)"
    }

}

