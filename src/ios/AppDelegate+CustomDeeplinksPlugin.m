#import "AppDelegate+CustomDeeplinksPlugin.h"
#import "CustomDeeplinksPlugin.h"

@implementation AppDelegate (CustomDeeplinksPlugin)

- (void)notifyAppsFlyerWithUserActivity:(NSUserActivity *)userActivity restorationHandler:(void (^)(NSArray *))restorationHandler {
    Class appsFlyerClass = NSClassFromString(@"AppsFlyerLib");
    if (appsFlyerClass) {
        SEL sharedSelector = NSSelectorFromString(@"shared");
        if ([appsFlyerClass respondsToSelector:sharedSelector]) {
            id sharedLib = [appsFlyerClass performSelector:sharedSelector];
            SEL continueSelector = NSSelectorFromString(@"continueUserActivity:restorationHandler:");
            
            if (sharedLib && [sharedLib respondsToSelector:continueSelector]) {
                #pragma clang diagnostic push
                #pragma clang diagnostic ignored "-Warc-performSelector-leaks"
                [sharedLib performSelector:continueSelector withObject:userActivity withObject:restorationHandler];
                #pragma clang diagnostic pop
                NSLog(@"[CustomDeeplinks] Successfully forwarded Universal Link to AppsFlyer");
            }
        }
    }
}

- (void)notifyAppsFlyerWithURL:(NSURL *)url options:(NSDictionary<UIApplicationOpenURLOptionsKey,id> *)options {
    Class appsFlyerClass = NSClassFromString(@"AppsFlyerLib");
    if (appsFlyerClass) {
        SEL sharedSelector = NSSelectorFromString(@"shared");
        if ([appsFlyerClass respondsToSelector:sharedSelector]) {
            id sharedLib = [appsFlyerClass performSelector:sharedSelector];
            SEL openURLSelector = NSSelectorFromString(@"handleOpenURL:options:");
            
            if (sharedLib && [sharedLib respondsToSelector:openURLSelector]) {
                #pragma clang diagnostic push
                #pragma clang diagnostic ignored "-Warc-performSelector-leaks"
                [sharedLib performSelector:openURLSelector withObject:url withObject:options];
                #pragma clang diagnostic pop
                NSLog(@"[CustomDeeplinks] Successfully forwarded URL Scheme to AppsFlyer");
            }
        }
    }
}

- (BOOL)application:(UIApplication *)application 
continueUserActivity:(NSUserActivity *)userActivity 
restorationHandler:(void (^)(NSArray *))restorationHandler {

    NSLog(@"[CustomDeeplinks] First click");
    
    if (![userActivity.activityType isEqualToString:NSUserActivityTypeBrowsingWeb] || userActivity.webpageURL == nil) {
        NSLog(@"[CustomDeeplinks] Invalid URL");
        return NO;
    }

    // Change: Notify Appsflyer
    [self notifyAppsFlyerWithUserActivity:userActivity restorationHandler:restorationHandler];

    CustomDeeplinksPlugin *plugin = [self.viewController getCommandInstance:@"CustomDeeplinks"];
    if (plugin == nil) {
        NSLog(@"[Deeplinks] Plugin not found");
    }

    NSLog(@"[CustomDeeplinks] URL: %@", userActivity.webpageURL.absoluteString);

    BOOL handled = [plugin handleUserActivity:userActivity];

    NSLog(@"[CustomDeeplinks] handleUserActivity result: %@", handled ? @"YES" : @"NO");

    return handled;
}

- (BOOL)application:(UIApplication *)app 
            openURL:(NSURL *)url 
            options:(NSDictionary<UIApplicationOpenURLOptionsKey,id> *)options {

    NSLog(@"[CustomDeeplinks] App opened via URL scheme: %@", url.absoluteString);

    // ALTERAÇÃO: Notifica o AppsFlyer antes de terminar a execução
    [self notifyAppsFlyerWithURL:url options:options];

    return YES;
}

@end
