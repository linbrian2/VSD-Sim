function urlBase64ToUint8Array(base64String) {
  const padding = '='.repeat((4 - (base64String.length % 4)) % 4);
  const base64 = (base64String + padding).replace(/-/g, '+').replace(/_/g, '/');

  const rawData = window.atob(base64);
  const outputArray = new Uint8Array(rawData.length);

  for (let i = 0; i < rawData.length; ++i) {
    outputArray[i] = rawData.charCodeAt(i);
  }
  return outputArray;
}

export function registerServiceWorker(registerCallback) {
  // Check that service workers are supported
  if ('serviceWorker' in navigator) {
    navigator.serviceWorker
      .register('./service-worker.js')
      .then(function(registration) {
        console.log('Service worker successfully registered.');

        //Checks if current browser supports Push notification
        if (!('PushManager' in window)) {
          console.log("Sorry, Push notification isn't supported in your browser.");
        } else {
          if (registerCallback) {
            registration.pushManager.getSubscription().then(function(subscription) {
              // 0 - Not subscriped, 1 - subscriped, 2 - denied
              const status = Notification.permission === 'denied' ? 2 : subscription === null ? 0 : 1;
              registerCallback(subscription, status);
            });
          }
        }

        return registration;
      })
      .catch(err => {
        console.error('Unable to register service worker.', err);
      });
  } else {
    console.log('Service Worker Not Supported');
  }
}

export function isPushSupported() {
  //checks if user has granted permission to Push notifications
  if (Notification.permission === 'denied') {
    console.log('User has blocked push notification.');
    return false;
  }

  //Checks if current browser supports Push notification
  if (!('PushManager' in window)) {
    console.log("Sorry, Push notification isn't supported in your browser.");
    return false;
  }

  //If `serviceWorker` is registered and ready
  navigator.serviceWorker.ready.then(function(registration) {
    registration.pushManager.getSubscription().catch(function(error) {
      console.error('Error occurred while enabling push ', error);
      return false;
    });
  });

  return true;
}

export function getSubscription() {
  //checks if user has granted permission to Push notifications
  if (Notification.permission === 'denied') {
    console.log('User has blocked push notification.');
    return false;
  }

  //Checks if current browser supports Push notification
  if (!('PushManager' in window)) {
    console.log("Sorry, Push notification isn't supported in your browser.");
    return false;
  }

  //If `serviceWorker` is registered and ready
  navigator.serviceWorker.ready.then(function(registration) {
    registration.pushManager.getSubscription().catch(function(error) {
      console.error('Error occurred while enabling push ', error);
      return false;
    });
  });

  return true;
}

export function askPermission() {
  return new Promise(function(resolve, reject) {
    const permissionResult = Notification.requestPermission(function(result) {
      if (result !== 'granted') {
        reject(new Error('Bad permission result'));
      }
      resolve(result);
    });

    if (permissionResult) {
      permissionResult.then(resolve, reject);
    }
  }).then(function(permissionResult) {
    if (permissionResult !== 'granted') {
      throw new Error("We weren't granted permission.");
    }
  });
}

export function getNotificationPermissionState() {
  if (navigator.permissions) {
    return navigator.permissions.query({ name: 'notifications' }).then(result => {
      return result.state;
    });
  }

  return new Promise(resolve => {
    resolve(Notification.permission);
  });
}

export async function subscribe(publicKey, subscribeCallback) {
  navigator.serviceWorker.ready.then(function(serviceWorkerRegistration) {
    // Contact the third party push server. Which one is contacted by
    // pushManager is  configured internally in the browser, so we don't
    // need to worry about browser differences here.
    //
    // When .subscribe() is invoked, a notification will be shown in the
    // user's browser, asking the user to accept push notifications from
    // <yoursite.com>. This is why it is async and requires a catch.

    const subscribeOptions = {
      userVisibleOnly: true,
      applicationServerKey: urlBase64ToUint8Array(publicKey)
    };

    serviceWorkerRegistration.pushManager
      .subscribe(subscribeOptions)
      .then(function(subscription) {
        console.info('Push notification subscribed.');
        if (subscribeCallback) {
          subscribeCallback(subscription);
        }
        return subscription;
      })
      .catch(function(e) {
        if (Notification.permission === 'denied') {
          console.warn('Permission for Notifications was denied');
        } else {
          console.error('Unable to subscribe to push.', e);
        }
      });
  });
}

export function unsubscribe(unsubscribeCallback) {
  navigator.serviceWorker.ready.then(function(registration) {
    // Get subscription
    registration.pushManager
      .getSubscription()
      .then(function(subscription) {
        //If no `push subscription`, then return
        if (!subscription) {
          alert('Unable to unregister push notification.');
          return;
        }

        // Unsubscribes user
        subscription
          .unsubscribe()
          .then(function() {
            console.info('Push notification unsubscribed.');
            if (unsubscribeCallback) {
              unsubscribeCallback(subscription);
            }
          })
          .catch(function(error) {
            console.error(error);
          });
      })
      .catch(function(error) {
        console.error('Failed to unsubscribe push notification.' + error);
      });
  });
}
