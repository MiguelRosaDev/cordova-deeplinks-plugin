document.addEventListener("deviceready", onDeviceReady, false)

function onDeviceReady() {
  console.log("Device is ready")

  cordova.plugins.DeeplinksPlugin.handleDeepLink(
    (uri) => {
      console.log("Deep link received: ", uri)
      // Handle the deep link here
    },
    (error) => {
      console.error("Error handling deep link: ", error)
    },
  )

  // This function will be called from native code
  window.handleDeepLink = (uri) => {
    console.log("Deep link received from native: ", uri)
    // Handle the deep link here
  }
}

