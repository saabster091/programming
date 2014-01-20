package na.ethan.CommentCollector

/**
 * Ethan Petuchowski
 * 1/19/14
 *
 * based on plus-cmd-line-sample:PlusSample.java
 */

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.util.store.DataStoreFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.plus.Plus
import com.google.api.services.plus.PlusScopes
import java.io.{FileInputStream, File, IOException, InputStreamReader}
import java.util.HashSet
import java.util.Set
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.plus.model.Comment
import scala.collection.JavaConverters._

object PlusForager {

  private val APPLICATION_NAME = "ethanp-CommentCollector/0.0"

  /** dir to store user credentials */
  private val DATA_STORE_DIR = new File(System.getProperty("user.home"), ".store/CommColl")

  /** best practice is to use this as a globally shared instance across app */
  private var dataStoreFactory: FileDataStoreFactory = _

  private val JSON_FACTORY = JacksonFactory.getDefaultInstance

  private var httpTransport: HttpTransport = _

  private var client: Plus = _

  @throws[Exception]
  private def authorize(): Credential = {
    // load client secrets
    val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
      new InputStreamReader(new FileInputStream("/etc/google_keys/client_secrets.json")))

    if (clientSecrets.getDetails.getClientId.startsWith("Enter")
        || clientSecrets.getDetails.getClientSecret.startsWith("Enter")) {
      System.out.println(
          "Overwrite the src/main/resources/client_secrets.json file with the client secrets file "
        + "you downloaded from the Quickstart tool or manually enter your Client ID and Secret "
        + "from https://code.google.com/apis/console/?api=plus#project:855358432800 "
        + "into src/main/resources/client_secrets.json")
      System.exit(1)
    }

    // Set up authorization code flow.
    // Remove scopes that you are not actually using.
    val scopes: Set[String] = new HashSet[String]
    scopes.add(PlusScopes.PLUS_LOGIN)
//    scopes.add(PlusScopes.PLUS_ME)
//    scopes.add(PlusScopes.USERINFO_EMAIL)
//    scopes.add(PlusScopes.USERINFO_PROFILE)

    val flow = new GoogleAuthorizationCodeFlow.Builder(
      httpTransport, JSON_FACTORY, clientSecrets, scopes)
      .setDataStoreFactory(dataStoreFactory)
      .build()

    // authorize
    new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user")
  }

  def main(args: Array[String]) {
    try {
      httpTransport = GoogleNetHttpTransport.newTrustedTransport
      dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR)
      val credential = authorize
      client = new Plus.Builder(httpTransport, JSON_FACTORY, credential)
        .setApplicationName(APPLICATION_NAME).build()
      // from https://developers.google.com/+/api/latest/comments/list#examples
      val activityId = "z12jcvs5spvzzlw2122bgrugssb1dna0e"  // <== comment on Gangnam Style
      val listComments = client.comments().list(activityId)
      listComments.setMaxResults(5L)

      var commentFeed = listComments.execute()
      var comments = commentFeed.getItems.asScala.toList
      // Loop through until we arrive at an empty page
      while (comments != null && commentFeed.getNextPageToken != null) {
        printComments(comments)
        listComments.setPageToken(commentFeed.getNextPageToken)
        commentFeed = listComments.execute()
        comments = commentFeed.getItems.asScala.toList
      }
      printComments(comments)
    }
    catch {
      case e: IOException => System.err.println(e.getMessage)
      case t: Throwable => t.printStackTrace()
    }
    System.exit(1)
  }
  def printComments(comments: List[Comment]) { comments.foreach(c =>
    println(c.getActor.getDisplayName + " commented " + c.getObject.getContent)) }
}
