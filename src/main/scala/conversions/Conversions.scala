package conversions

object Conversions {

  case class TwitterUser(id: Long, name: String)

  case class FacebookUser(id: String, name: String)


















  object Implicits {
    implicit def twitterToFacebook(user: TwitterUser): FacebookUser =
      FacebookUser(user.id.toString, user.name)
  }

  // mostrar companion obj lookup

}
