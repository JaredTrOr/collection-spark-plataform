package org.jared.trujillo;

import static spark.Spark.port;

public class Main {
    public static void main(String[] args) {
        final int PORT = 4567;

        System.out.println("Starting API...");
        port(PORT);
        ApiRouter router = new ApiRouter();
        router.startRoutes();
        System.out.println("Server running at http://localhost:"+PORT);
    }
}