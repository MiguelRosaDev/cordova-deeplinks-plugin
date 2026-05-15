#import "AppDelegate+CustomDeeplinksPlugin.h"
#import "CustomDeeplinksPlugin.h"

@implementation AppDelegate (CustomDeeplinksPlugin)

- (void)notifyAppsFlyerWithUserActivity:(NSUserActivity *)userActivity restorationHandler:(void (^)(NSArray *))restorationHandler {
    Class appsFlyerClass = NSClassFromString(@"AppsFlyerLib");
    if (!appsFlyerClass) return;

    SEL sharedSelector = NSSelectorFromString(@"shared");
    if (![appsFlyerClass respondsToSelector:sharedSelector]) return;
    
    id sharedLib = [appsFlyerClass performSelector:sharedSelector];
    if (!sharedLib) return;

    SEL devKeySelector = NSSelectorFromString(@"appsFlyerDevKey");
    SEL continueSelector = NSSelectorFromString(@"continueUserActivity:restorationHandler:");
    if (![sharedLib respondsToSelector:continueSelector]) return;

    #pragma clang diagnostic push
    #pragma clang diagnostic ignored "-Warc-performSelector-leaks"
    NSString *devKey = [sharedLib respondsToSelector:devKeySelector] ? [sharedLib performSelector:devKeySelector] : nil;
    #pragma clang diagnostic pop

    if (devKey && devKey.length > 0) {
        // WARM START: AppsFlyer já inicializado, envia imediatamente
        #pragma clang diagnostic push
        #pragma clang diagnostic ignored "-Warc-performSelector-leaks"
        [sharedLib performSelector:continueSelector withObject:userActivity withObject:restorationHandler];
        #pragma clang diagnostic pop
        NSLog(@"[CustomDeeplinks] Warm Start: Universal Link forwarded to AppsFlyer immediately");
    } else {
        // COLD START: Aguarda 2.5 segundos para o OutSystems correr o JS e inicializar o AppsFlyer
        NSLog(@"[CustomDeeplinks] Cold Start: Waiting 2.5s for AppsFlyer JS initialization...");
        
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(2.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            #pragma clang diagnostic push
            #pragma clang diagnostic ignored "-Warc-performSelector-leaks"
            [sharedLib performSelector:continueSelector withObject:userActivity withObject:restorationHandler];
            #pragma clang diagnostic pop
            NSLog(@"[CustomDeeplinks] Cold Start: Delayed Universal Link forwarded to AppsFlyer");
        });
    }
}

- (void)notifyAppsFlyerWithURL:(NSURL *)url options:(NSDictionary<UIApplicationOpenURLOptionsKey,id> *)options {
    Class appsFlyerClass = NSClassFromString(@"AppsFlyerLib");
    if (!appsFlyerClass) return;

    SEL sharedSelector = NSSelectorFromString(@"shared");
    if (![appsFlyerClass respondsToSelector:sharedSelector]) return;
    
    id sharedLib = [appsFlyerClass performSelector:sharedSelector];
    if (!sharedLib) return;

    SEL devKeySelector = NSSelectorFromString(@"appsFlyerDevKey");
    SEL openURLSelector = NSSelectorFromString(@"handleOpenURL:options:");
    if (![sharedLib respondsToSelector:openURLSelector]) return;

    #pragma clang diagnostic push
    #pragma clang diagnostic ignored "-Warc-performSelector-leaks"
    NSString *devKey = [sharedLib respondsToSelector:devKeySelector] ? [sharedLib performSelector:devKeySelector] : nil;
    #pragma clang diagnostic pop

    if (devKey && devKey.length > 0) {
        // WARM START: Envia imediatamente
        #pragma clang diagnostic push
        #pragma clang diagnostic ignored "-Warc-performSelector-leaks"
        [sharedLib performSelector:openURLSelector withObject:url withObject:options];
        #pragma clang diagnostic pop
        NSLog(@"[CustomDeeplinks] Warm Start: URL Scheme forwarded to AppsFlyer immediately");
    } else {
        // COLD START: Aguarda 2.5 segundos
        NSLog(@"[CustomDeeplinks] Cold Start: Waiting 2.5s for AppsFlyer JS initialization...");
        
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(2.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            #pragma clang diagnostic push
            #pragma clang diagnostic ignored "-Warc-performSelector-leaks"
            [sharedLib performSelector:openURLSelector withObject:url withObject:options];
            #pragma clang diagnostic pop
            NSLog(@"[CustomDeeplinks] Cold Start: Delayed URL Scheme forwarded to AppsFlyer");
        });
    }
}

// Universal Link handler
- (BOOL)application:(UIApplication *)application 
continueUserActivity:(NSUserActivity *)userActivity 
restorationHandler:(void (^)(NSArray *))restorationHandler {

    NSLog(@"[CustomDeeplinks] First click");
    
    if (![userActivity.activityType isEqualToString:NSUserActivityTypeBrowsingWeb] || userActivity.webpageURL == nil) {
        NSLog(@"[CustomDeeplinks] Invalid URL");
        return NO;
    }

    // Encaminha de forma segura para o AppsFlyer (trata Warm e Cold Start internamente)
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

// Deep link (URL scheme) handler
- (BOOL)application:(UIApplication *)app 
            openURL:(NSURL *)url 
            options:(NSDictionary<UIApplicationOpenURLOptionsKey,id> *)options {

    NSLog(@"[CustomDeeplinks] App opened via URL scheme: %@", url.absoluteString);

    // Encaminha de forma segura para o AppsFlyer (trata Warm e Cold Start internamente)
    [self notifyAppsFlyerWithURL:url options:options];

    return YES;
}

@end
