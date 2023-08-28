# Ktor Client Implementation
Ktor Client is a Kotlin framework that allow developers to make asynchronous network calls.
In this repo, we will implement the same step by step

## How to integrate into your app?

#### Step 1. Add the plugin to your build.gradle (project) :

    plugins {
        id ("org.jetbrains.kotlin.plugin.serialization") version "1.6.21"
    }

#### Step 2. Add the plugin and dependencies to your build.gradle (module) :

    plugins {
        id 'kotlinx-serialization'
    }
    
    dependencies {
        implementation 'io.ktor:ktor-client-android:1.6.4'
        implementation 'io.ktor:ktor-client-serialization:1.6.4'
        implementation 'org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2'
        implementation 'io.ktor:ktor-client-logging-jvm:1.6.4'
    }

#### Step 3. Download Files :

* [ApiCalling](./app/src/main/java/com/nakul/ktorexample/api_helper/ApiCalling.kt) : Handles network calling
* [ApiUtil](./app/src/main/java/com/nakul/ktorexample/api_helper/ApiUtil.kt) : Handles Http Client instance with tweaks such as Json Parcing, Logs, etc.
* [RequestHandler](./app/src/main/java/com/nakul/ktorexample/api_helper/RequestHandler.kt) : Used to share the required network call function to [ApiUtil](./app/src/main/java/com/nakul/ktorexample/api_helper/ApiUtil.kt)

#### Step 4. Create an Object Class containing all the endPoints and BASE URL [Sample](./app/src/main/java/com/nakul/ktorexample/api_handling/ApiUrls.kt)

    object ApiUrls {
        const val BASE_URL = "https://newsapi.org"
        const val NEWS = "/v2/everything"
    }

#### Step 5. Create an Interface containing all the Network Calls [Sample](./app/src/main/java/com/nakul/ktorexample/api_handling/ApiInterface.kt)

    object ApiInterface {
        suspend fun getUserData(): ResponseModel {
            return ApiUtil.getHttpClient().use {
                it.get("${ApiUrls.BASE_URL}${ApiUrls.NEWS}"){
                    parameter("q","Android")
                    parameter("sortBy","publishedAt")
                    parameter("apiKey","3e36f11979ac41178fe55d05b52516c9")
                }
            }
        }
    }


#### Step 5. Call the desired api inside viewModelScope with the help of [ApiCalling](./app/src/main/java/com/nakul/ktorexample/api_helper/ApiCalling.kt)

    viewModelScope.launch {
        ApiCalling.hitApi(
            context = context,
            layoutId = R.layout.progress_loader,
            requestHandler = { ApiInterface.getUserData() },
            onResponse = {
                Log.e("Data onResponse", it.articles?.get(0).toString())
            }
        )
    }

## Additional Functionality
### To enable/disable cache (Enabled by default)
####  In [ApiUtil](./app/src/main/java/com/nakul/ktorexample/api_helper/ApiUtil.kt), while creating HttpClient, install HttpCache as below
             fun getHttpClient(): HttpClient {
                    ...
                     install(DefaultRequest) {
                        header(HttpHeaders.ContentType, ContentType.Application.Json)
                    }
                    ...
             }
         
