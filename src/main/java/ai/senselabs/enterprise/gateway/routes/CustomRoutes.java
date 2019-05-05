package ai.senselabs.enterprise.gateway.routes;


import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class CustomRoutes {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()

                /** Standard get, returns header foo with value
                 *  http :8080/get
                 *  http 192.168.99.100:30441/get
                 * */
                .route(r -> r.path("/get")
                                .filters(
                                        f -> f.addRequestHeader("foo", "bar")
                                )
                                .uri("https://shielded-ocean-99346.herokuapp.com/hi") //must always specify which port the downstream
                        //service is listening on
                )


                /** Add request header when route from myhost.org
                 *   http :8080/headers Host:www.myhost.org
                 *   http 192.168.99.100:30441/headers Host:www.myhost.org
                 * */
                .route(r -> r.host("*.myhost.org")
                        .filters(
                                f -> f.addRequestHeader("Anotherone", "BitesTheDust")
                        )
                        .uri("http://httpbin.org")
                )



                /** Example of how the path can be rewritten, returning just .../get
                 *   http :8080/foo/get Host:www.rewrite.org
                 *   http localhost:8080/foo/get Host:www.rewrite.org
                 * */
                .route(r -> r.host("*.rewrite.org")
                        .filters(
                                f -> f.rewritePath("/foo/(?<segment>.*)", "/${segment}")
                        )
                        .uri("http://httpbin.org:80")
                )


                /** Example of how to change the path - another way to do a rewrite
                 *  http :8080/foo/get Host:www.setpath.org				 *
                 *  http 192.168.99.100:30441/foo/get Host:www.setpath.org
                 * */
                .route(r -> r.host("*.setpath.org")
                        .and()
                        .path("/foo/{segment}")

                        .filters(
                                f -> f.setPath("/{segment}")
                        )
                        .uri("http://httpbin.org:80")
                )

                /**
                 * Example of hystrix timing out and throwing "504 Gateway Error"
                 * httpie will make a call and force a delay of 3 seconds
                 * http :8080/delay/3 Host:www.hystrix.org
                 * */
                .route("hystrix_route", r -> r.host("*.hystrix.org")
                        .filters(f -> f.hystrix(c -> c.setName("slowcmd")))
                        .uri("http://httpbin.org"))


                /**
                 *  Example calling a hystrix if the downstream service does not respond
                 *  http localhost:8080/delay/3 Host:www.hystrixfallback.org
                 * */
                .route("hystrix_fallback_route", r -> r.host("*.hystrixfallback.org")
                        .filters(f -> f.hystrix(c -> c.setName("slowcmd").setFallbackUri("forward:/hystrixfallback")))
                        .uri("http://httpbin.org")
                )

                .build();
    }


}
