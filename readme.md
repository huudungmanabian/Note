# Android fundamental
## Language

- Android app can be written by Java, Kotlin, C++ languages.

## Compile

- Android SDK tools will compile the code, data and resources into an APK or an App Bundle.

    - An Android package (APK), which have `.apk` suffix, contains the contents of an Android app that are required at runtime. Android device can use this file to install the app.

    - An Android App Bundle (AAB), which is an archive file with an `.aab` suffix, contains the contents of an Android app project including some additional metadata that is not required at runtime. This is used when publishing the app on Google Play and can't install directly. When user download the app Google Play will serve the optimized APK file for every device to install the app.

## Security

- Each Android app lives in its own security sandbox, protected by the following Android security features:

    - The Android operating system is a multi-user Linux system in which each app is a different user.

    - By default, the system assigns each app a unique Linux user ID (the ID is used only by the system and is unknown to the app). The system sets permissions for all the files in an app so that only the user ID assigned to that app can access them.

    - Each process has its own virtual machine (VM), so an app's code runs in isolation from other apps.

    - By default, every app runs in its own Linux process. The Android system starts the process when any of the app's components need to be executed, and then shuts down the process when it's no longer needed or when the system must recover memory for other apps.

## Apps communication

- The Android system implements the *principle of least privilege*. That is, each app, by default, has access only to the components that it requires to do its work and no more. This creates a very secure environment in which an app cannot access parts of the system for which it is not given permission. However, there are ways for an app to share data with other apps and for an app to access system services:

    - It's possible to arrange for two apps to share the same Linux user ID, in which case they are able to access each other's files. To conserve system resources, apps with the same user ID can also arrange to run in the same Linux process and share the same VM. The apps must also be signed with the same certificate.

    - An app can request permission to access device data such as the device's location, camera, and Bluetooth connection. The user has to explicitly grant these permissions.

# App component

## Activities

An activity is the entry point for interacting with user. It represents a single screen with a user interface.

Each activity is independent to each others. However, an app can start an activity of an other app if it allows like we can open Momo app when we use Momo payment method from Grab app.

An activity facilitates the following key interactions between system and app:

- Keeping track of what the user currently cares about (what is on screen) to ensure that the system keeps running the process that is hosting the activity.

- Knowing that previously used processes contain things the user may return to (stopped activities), and thus more highly prioritize keeping those processes around.

- Helping the app handle having its process killed so the user can return to activities with their previous state restored.

- Providing a way for apps to implement user flows between each other, and for the system to coordinate these flows. (The most classic example here being share.)

You implement an activity as a subclass of the [`Activity`](https://developer.android.com/reference/android/app/Activity) class.

## Services

A service is a general-purpose entry point for keeping an app running in the background for all kinds of reasons: fetching data from the remote, sync data or playing music in the background.

It is a component that runs in the background to perform long-running operations or to perform work for remote processes. 

A service does not provide a user interface.

There are 2 service types:

- **Started services** tell the system to keep them running until their work is completed. This could be to sync some data in the background or play music even after the user leaves the app. Syncing data in the background or playing music also represent two different types of started services that modify how the system handles them:

    - Music playback is something the user is directly aware of, so the app tells the system this by saying it wants to be foreground with a notification to tell the user about it; in this case the system knows that it should try really hard to keep that service's process running, because the user will be unhappy if it goes away.

    - A regular background service is not something the user is directly aware as running, so the system has more freedom in managing its process. It may allow it to be killed (and then restarting the service sometime later) if it needs RAM for things that are of more immediate concern to the user.
- **Bound services** run because some other app (or the system) has said that it wants to make use of the service. This is basically the service providing an API to another process like Live wallpapers, notification listeners, screen savers, input methods, accessibility services, and many other core system features are all built as services that applications implement and the system binds to when they should be running.

A service is implemented as a subclass of [`Service`](https://developer.android.com/reference/android/app/Service).

## Broadcast receivers

A broadcast receiver is a component that enables the system to deliver events to the app outside of a regular user flow, allowing the app to respond to system-wide broadcast announcements.

**Broadcast receivers** are another well-defined entry into the app, the system can deliver broadcasts even to apps that aren't currently running.

Many broadcasts originate from the system—for example, a broadcast announcing that the screen has turned off, the battery is low, or a picture was captured. 

Apps can also initiate broadcasts—for example, to let other apps know that some data has been downloaded to the device and is available for them to use.

They may create a *status bar notification* to alert the user when a broadcast event occurs.

More commonly, though, a broadcast receiver is just a gateway to other components and is intended to do a very minimal amount of work. For instance, it might schedule a `JobService` to perform some work based on the event with `JobScheduler`

A broadcast receiver is implemented as a subclass of [`BroadcastReceiver`](https://developer.android.com/reference/android/content/BroadcastReceiver) and each broadcast is delivered as an Intent object.

## Content providers

A content provider manages a shared set of app data that you can store in the file system, in a SQLite database, on the web, or on any other persistent storage location that your app can access. Through the content provider, other apps can query or modify the data if the content provider allows it.

Through the content provider, other apps can query or modify the data if the content provider allows it. For example, the Android system provides a content provider that manages the user's contact information like [`ContactsContract.Data`](https://developer.android.com/reference/android/provider/ContactsContract.Data), to read and write information about a particular person.

Thus an app can decide how it wants to map the data it contains to a URI namespace, handing out those URIs to other entities which can in turn use them to access the data. There are a few particular things this allows the system to do in managing an app:

- Assigning a URI doesn't require that the app remain running, so URIs can persist after their owning apps have exited. The system only needs to make sure that an owning app is still running when it has to retrieve the app's data from the corresponding URI.

- These URIs also provide an important fine-grained security model. For example, an app can place the URI for an image it has on the clipboard, but leave its content provider locked up so that other apps cannot freely access it. When a second app attempts to access that URI on the clipboard, the system can allow that app to access the data via a temporary URI permission grant so that it is allowed to access the data only behind that URI, but nothing else in the second app.

Content providers are also useful for reading and writing data that is private to your app and not shared. A content provider is implemented as a subclass of [`ContentProvider`](https://developer.android.com/reference/android/content/ContentProvider) and must implement a standard set of APIs that enable other apps to perform transactions.

A unique aspect of the Android system design is that any app can start another app’s component. You can simply start the activity in the camera app that captures a photo. When complete, the photo is even returned to your app so you can use it.

When the system starts a component, it starts the process for that app if it's not already running and instantiates the classes needed for the component. Therefore, unlike apps on most other systems, Android apps don't have a single entry point.

Because the system runs each app in a separate process with file permissions that restrict access to other apps, your app cannot directly activate a component from another app. However, the Android system can. To activate a component in another app, deliver a message to the system that specifies your intent to start a particular component. The system then activates the component for you.

## Activating components

Three of the four component types—activities, services, and broadcast receivers—are activated by an asynchronous message called an intent. 

Intents bind individual components to each other at runtime. You can think of them as the messengers that request an action from other components, whether the component belongs to your app or another.

An intent is created with an [`Intent`](https://developer.android.com/reference/android/content/Intent) object, which defines a message to activate either a specific component (explicit intent) or a specific type of component (implicit intent).

For activities and services, an intent defines the action to perform (for example, to view or send something) and may specify the URI of the data to act on, among other things that the component being started might need to know. For example, an intent might convey a request for an activity to show an image or to open a web page. In some cases, you can start an activity to receive a result, in which case the activity also returns the result in an Intent. For example, you can issue an intent to let the user pick a personal contact and have it returned to you. The return intent includes a URI pointing to the chosen contact.

For broadcast receivers, the intent simply defines the announcement being broadcast. For example, a broadcast to indicate the device battery is low includes only a known action string that indicates battery is low.

Unlike activities, services, and broadcast receivers, content providers are not activated by intents. Rather, they are activated when targeted by a request from a ContentResolver. The content resolver handles all direct transactions with the content provider so that the component that's performing transactions with the provider doesn't need to and instead calls methods on the ContentResolver object. This leaves a layer of abstraction between the content provider and the component requesting information (for security).

There are separate methods for activating each type of component:

- You can start an activity or give it something new to do by passing an Intent to startActivity() or startActivityForResult() (when you want the activity to return a result).

- With Android 5.0 (API level 21) and later, you can use the JobScheduler class to schedule actions. For earlier Android versions, you can start a service (or give new instructions to an ongoing service) by passing an Intent to startService(). You can bind to the service by passing an Intent to bindService().

- You can initiate a broadcast by passing an Intent to methods such as sendBroadcast(), sendOrderedBroadcast(), or sendStickyBroadcast().

- You can perform a query to a content provider by calling query() on a ContentResolver.

# The manifest file

## Overview

Before the Android system can start an app component, the system must know that the component exists by reading the app's manifest file, AndroidManifest.xml. Your app must declare all its components in this file, which must be at the root of the app project directory.

Things is included in manifest file:

- User permissions.

- Minimum API Level required by the app

- Hardware and software features used or required by the app, such as a camera, bluetooth services, or a multitouch screen.

- API libraries the app needs to be linked against (other than the Android framework APIs)

## Declaring components

The primary task of the manifest is to inform the system about the app's components.
For example, declare an activity:

    <?xml version="1.0" encoding="utf-8"?>
    <manifest ... >
        <application android:icon="@drawable/app_icon.png" ... >
            <activity android:name="com.example.project.ExampleActivity"
                    android:label="@string/example_label" ... >
            </activity>
            ...
        </application>
    </manifest>

You must declare all app components using the following elements:

- `<activity>` elements for activities.
- `<service>` elements for services.
- `<receiver>` elements for broadcast receivers.
- `<provider>` elements for content providers.

Activities, services, and content providers that you include in your source but do not declare in the manifest are not visible to the system and, consequently, can never run. However, broadcast receivers can be either declared in the manifest or created dynamically in code as BroadcastReceiver objects and registered with the system by calling registerReceiver().

## Declaring component capabilities

As discussed above, in Activating components, you can use an `Intent` to start activities, services, and broadcast receivers. You can use an `Intent` by explicitly naming the target component (using the component class name) in the intent. You can also use an implicit intent, which describes the type of action to perform and, optionally, the data upon which you’d like to perform the action. The implicit intent allows the system to find a component on the device that can perform the action and start it. If there are multiple components that can perform the action described by the intent, the user selects which one to use.

> **Caution**: If you use an intent to start a Service, ensure that your app is secure by using an explicit intent. Using an implicit intent to start a service is a security hazard because you cannot be certain what service will respond to the intent, and the user cannot see which service starts. Beginning with Android 5.0 (API level 21), the system throws an exception if you call bindService() with an implicit intent. Do not declare intent filters for your services.

The system identifies the components that can respond to an intent by comparing the intent received to the intent filters provided in the manifest file of other apps on the device.

For example, if you build an email app with an activity for composing a new email, you can declare an intent filter to respond to "send" intents (in order to send a new email), as shown in the following example:

    <manifest ... >
        ...
        <application ... >
            <activity android:name="com.example.project.ComposeEmailActivity">
                <intent-filter>
                    <action android:name="android.intent.action.SEND" />
                    <data android:type="*/*" />
                    <category android:name="android.intent.category.DEFAULT" />
                </intent-filter>
            </activity>
        </application>
    </manifest>

If another app creates an intent with the `ACTION_SEND` action and passes it to `startActivity()`, the system may start your activity so the user can draft and send an email.

For more about creating intent filters, see the [Intents and Intent Filters](https://developer.android.com/guide/components/intents-filters) document.

## Declaring app requirements

The values for `minSdkVersion` and `targetSdkVersion` are set in your app module's `build.gradle` file:

    android {
    ...
    defaultConfig {
        ...
        minSdkVersion 26
        targetSdkVersion 29
    }
    }

Declare the camera feature directly in your app's manifest file:

    <manifest ... >
        <uses-feature android:name="android.hardware.camera.any"
                    android:required="true" />
        ...
    </manifest>

# App resources

An Android app is composed of more than just code—it requires resources that are separate from the source code, such as images, audio files, and anything relating to the visual presentation of the app. For example, you can define animations, menus, styles, colors, and the layout of activity user interfaces with XML files. Using app resources makes it easy to update various characteristics of your app without modifying code. Providing sets of alternative resources enables you to optimize your app for a variety of device configurations, such as different languages and screen sizes.

For every resource that you include in your Android project, the SDK build tools define a unique integer ID, which you can use to reference the resource from your app code or from other resources defined in XML. For example, if your app contains an image file named logo.png (saved in the res/drawable/ directory), the SDK tools generate a resource ID named R.drawable.logo. This ID maps to an app-specific integer, which you can use to reference the image and insert it in your user interface.

One of the most important aspects of providing resources separate from your source code is the ability to provide alternative resources for different device configurations. For example, by defining UI strings in XML, you can translate the strings into other languages and save those strings in separate files. Then Android applies the appropriate language strings to your UI based on a language qualifier that you append to the resource directory's name (such as res/values-fr/ for French string values) and the user's language setting.

Android supports many different qualifiers for your alternative resources. The qualifier is a short string that you include in the name of your resource directories in order to define the device configuration for which those resources should be used. For example, you should create different layouts for your activities, depending on the device's screen orientation and size. When the device screen is in portrait orientation (tall), you might want a layout with buttons to be vertical, but when the screen is in landscape orientation (wide), the buttons could be aligned horizontally. To change the layout depending on the orientation, you can define two different layouts and apply the appropriate qualifier to each layout's directory name. Then, the system automatically applies the appropriate layout depending on the current device orientation.