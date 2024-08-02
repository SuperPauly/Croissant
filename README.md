# Croissant

### [Full Java Documentation](https://anready.github.io/documentation/croissant/java/setup.html) | [Full Kotlin Documentation](https://anready.github.io/documentation/croissant/kotlin/setup.html)

## What is the 'Croissant' and why is it needed
Croissant was specifically developed for Android developers or teams requiring the MANAGE_EXTERNAL_STORAGE permission. This permission is crucial as it allows an app to access and control all files on a user's device. Google Play's review process is stringent regarding this permission: if your app is not a file manager, it will be rejected since Google’s documentation specifies that this permission should be exclusively used by file managers. This means that non-file manager apps cannot be granted this permission, and any attempt to use it outside a file manager context will result in a rejection of your app’s publication.

Our team faced this challenge, which prompted us to develop Croissant not only for our own use but also for the broader community of developers who might need it. Croissant is a simple file manager designed to be easy to use while fulfilling the necessary requirements to obtain and use the MANAGE_EXTERNAL_STORAGE permission.

The core functionality of Croissant revolves around its integration with ContentProvider, which allows it to be used as an API. 
Currently, Croissant supports the following features:
  - Requesting a list of all files and folders within a specified directory.
  - Opening files by specifying the file path.
    
In order for your app to interact with Croissant's API, users will need to have this app installed on their device. This is necessary for Croissant to provide access to the file system as required by your app.

These features enable developers to use Croissant as a foundation for creating apps that need to manage files while still complying with Google Play’s policies. To avoid issues with content duplication and to ensure compliance with Google’s guidelines, developers should make adjustments before publishing the app on Google Play. Recommended modifications include changing the app’s design, adding new features, and, importantly, altering the package name to differentiate it from other apps.

The documentation will provide comprehensive guidance on working with Croissant in both Java and Kotlin, covering all relevant aspects with code examples and explanations. This will help developers understand how to integrate and utilize the library effectively in their projects.
