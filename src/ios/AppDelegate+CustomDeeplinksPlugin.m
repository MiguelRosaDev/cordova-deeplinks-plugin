#import "AppDelegate+CustomDeeplinksPlugin.h"
#import "CustomDeeplinksPlugin.h"

@implementation AppDelegate (CustomDeeplinksPlugin)

- (void)notifyAppsFlyerWithUserActivity:(NSUserActivity *)userActivity {
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
        // WARM START: AppsFlyer já inicializado
        #pragma clang diagnostic push
        #pragma clang diagnostic ignored "-Warc-performSelector-leaks"
        [sharedLib performSelector:continueSelector withObject:userActivity withObject:nil];
        #pragma clang diagnostic pop
        NSLog(@"[CustomDeeplinks] Warm Start: Universal Link forwarded to AppsFlyer immediately");
    } else {
        // COLD START: Aguarda que o OutSystems inicialize o AppsFlyer via JS
        NSLog(@"[CustomDeeplinks] Cold Start: AppsFlyer not ready. Queuing Universal Link...");
        
        NSString *urlString = userActivity.webpageURL.absoluteString;
        
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            int attempts = 0;
            while (attempts < 30) { // Aguarda até 15 segundos (30 * 0.5s)
                [NSThread sleepForTimeInterval:0.5];
                
                __block BOOL ready = NO;
                dispatch_sync(dispatch_get_main_queue(), ^{
                    #pragma clang diagnostic push
                    #pragma clang diagnostic ignored "-Warc-performSelector-leaks"
                    NSString *currentKey = [sharedLib respondsToSelector:devKeySelector] ? [sharedLib performSelector:devKeySelector] : nil;
                    #pragma clang diagnostic pop
                    
                    if (currentKey && currentKey.length > 0) {
                        ready = YES;
                        // Cria uma atividade nova e limpa para evitar expiração do objeto pelo iOS
                        NSUserActivity *clonedActivity = [[NSUserActivity alloc] initWithActivityType:NSUserActivityTypeBrowsingWeb];
                        clonedActivity.webpageURL = [NSURL URLWithString:urlString];
                        
                        #pragma clang diagnostic push
                        #pragma clang diagnostic ignored "-Warc-performSelector-leaks"
                        [sharedLib performSelector:continueSelector withObject:clonedActivity withObject:nil];
                        #pragma clang diagnostic pop
                        NSLog(@"[CustomDeeplinks] Cold Start: AppsFlyer ready! Universal Link forwarded.");
                    }
                });
                
                if (ready) break;
                attempts++;
            }
        });
    }
}

- (void)notifyAppsFlyerWithURL:(NSURL *)url options:(NSDictionary<UIApplicationOpenURLOptionsKey,id> *)options {
    Class appsFlyerClass = NSClassFromString(@"AppsFlyerLib");
    if (!appsFlyerClass) return;

    SEL sharedSelector = NSSelectorFromString(@"shared");
    if (![appsFlyClass respondsToSelector:sharedSelector]) return;
    
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
        // WARM START
        #pragma clang diagnostic push
        #pragma clang diagnostic ignored "-Warc-performSelector-leaks"
        [sharedLib performSelector:openURLSelector withObject:url withObject:options];
        #pragma clang diagnostic pop
        NSLog(@"[CustomDeeplinks] Warm Start: URL Scheme forwarded to AppsFlyer immediately");
    } else {
        // COLD START
        NSLog(@"[CustomDeeplinks] Cold Start: AppsFlyer not ready. Queuing URL Scheme...");
        
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            int attempts = 0;
            while (attempts < 30) {
                [NSThread sleepForTimeInterval:0.5];
                
                __block BOOL ready = NO;
                dispatch_sync(dispatch_get_main_queue(), ^{
                    #pragma clang diagnostic push
                    #pragma clang diagnostic ignored "-Warc-performSelector-leaks"
                    NSString *currentKey = [sharedLib respondsToSelector:devKeySelector] ? [sharedLib performSelector:devKeySelector] : nil;
                    #pragma clang diagnostic pop
                    
                    if (currentKey && currentKey.length > 0) {
                        ready = YES;
                        #pragma clang diagnostic push
                        #pragma clang diagnostic ignored "-Warc-performSelector-leaks"
                        [sharedLib performSelector:openURLSelector withObject:url withObject:options];
                        #pragma clang diagnostic pop
                        NSLog(@"[CustomDeeplinks] Cold Start: AppsFlyer ready! URL Scheme forwarded.");
                    }
                });
                
                if (ready) break;
                attempts++;
            }
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

    // Encaminha de forma segura (Warm ou Cold start)
    [self notifyAppsFlyerWithUserActivity:userActivity];

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

    // Encaminha de forma segura (Warm ou Cold start)
    [self notifyAppsFlyerWithURL:url options:options];

    return YES;
}

@end
