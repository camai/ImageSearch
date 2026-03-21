import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.util.Properties

class AndroidNetworkConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val properties = Properties()
            val localPropertiesFile = rootProject.file("local.properties")
            if (localPropertiesFile.exists()) {
                properties.load(localPropertiesFile.inputStream())
            }

            val baseUrl = properties.getProperty("base_url", "")
            val clientId = properties.getProperty("naver_client_id", "")
            val clientSecret = properties.getProperty("naver_client_secret", "")

            extensions.configure<LibraryExtension> {
                buildFeatures {
                    buildConfig = true
                }
                defaultConfig {
                    buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
                    buildConfigField("String", "NAVER_CLIENT_ID", "\"$clientId\"")
                    buildConfigField("String", "NAVER_CLIENT_SECRET", "\"$clientSecret\"")
                }
            }
        }
    }
}
