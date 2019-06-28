package conversions

object Extensions {

  implicit class PullRequestExtensions(val hash: String) extends AnyVal {
    def pullRequestUrl: String =
      "https://github.com/tadp-utn-frba/tadp-utn-frba.github.io/pull/" +
        hash.stripPrefix("#")
  }

}
