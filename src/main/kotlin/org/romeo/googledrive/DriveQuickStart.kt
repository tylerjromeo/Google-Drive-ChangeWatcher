package org.romeo.googledrive

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.FileList
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

/**
 * User: tylerromeo
 * Date: 10/25/16
 * Time: 2:57 PM
 *
 */

class DriveQuickstart {
    /** Application name. */
    private val APPLICATION_NAME =
    "Drive API Java Quickstart"

    /** Directory to store user credentials for this application.  */
    private val DATA_STORE_DIR = java.io.File(
            System.getProperty("user.home"), ".credentials/drive-java-quickstart")


    /** Global instance of the {@link FileDataStoreFactory}. */
    private var DATA_STORE_FACTORY: FileDataStoreFactory? = null

    /** Global instance of the JSON factory. */
    private val JSON_FACTORY: JsonFactory =
    JacksonFactory.getDefaultInstance()

    /** Global instance of the HTTP transport. */
    private var HTTP_TRANSPORT: HttpTransport? = null

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/drive-java-quickstart
     */
    private val SCOPES =
    Arrays.asList(DriveScopes.DRIVE_METADATA_READONLY)

    init {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
            DATA_STORE_FACTORY = FileDataStoreFactory(DATA_STORE_DIR)
        } catch (t: Throwable) {
            t.printStackTrace()
            System.exit(1)
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    @Throws(IOException::class)
    fun authorize(): Credential {
        // Load client secrets.
        val input = javaClass.getResourceAsStream("/client_id.json");
        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(input))

        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
        .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build()
        val credential: Credential = AuthorizationCodeInstalledApp(
                flow, LocalServerReceiver()).authorize("user")
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential
    }

    /**
     * Build and return an authorized Drive client service.
     * @return an authorized Drive client service
     * @throws IOException
     */
    @Throws(IOException::class)
    fun getDriveService(): Drive {
        val credential: Credential = authorize()
        return Drive.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
        .setApplicationName(APPLICATION_NAME)
                .build()
    }

}

fun main(args : Array<String>) {

    val driveQuickStart = DriveQuickstart()

    // Build a new authorized API client service.
    val service: Drive = driveQuickStart.getDriveService()

    // Print the names and IDs for up to 10 files.
    val result: FileList = service.files().list()
            .setPageSize(10)
            .setFields("nextPageToken, files(id, name)")
            .execute()
    val files = result.files
    if (files == null || files.size == 0) {
        System.out.println("No files found.")
    } else {
        System.out.println("Files:")
        for (file in files) {
            System.out.printf("%s (%s)\n", file.name, file.id)
        }
    }
}