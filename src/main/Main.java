package main;

import api.bot_management.BotManagement;
import api.bot_management.data.LaunchedClient;

import java.io.IOException;
import java.sql.Time;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        HashMap<String, String> optsMap = new HashMap<>();

        for (int i = 0; i < args.length; i++) {
            if (args[i].charAt(0) == '-') {
                if (args[i].length() < 2)
                    throw new IllegalArgumentException("Not a valid argument: " + args[i]);
                else if (args[i].charAt(1) == '-') {
                    if (args[i].length() < 3)
                        throw new IllegalArgumentException("Not a valid argument: " + args[i]);
                } else {
                    if (args.length - 1 == i)
                        throw new IllegalArgumentException("Expected arg after: " + args[i]);
                    // -opt
                    optsMap.put(args[i], args[i + 1]);
                    i++;
                }
            } else {// arg
                throw new IllegalArgumentException("Expected arg format: -arg");
            }
        }

        try {
            executeOrder66(optsMap);
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void executeOrder66(HashMap<String, String> optsMap) throws Exception {
        sleep(Long.parseLong(optsMap.get("-sleep")));

        if (optsMap.containsKey("-killEmail") && optsMap.get("-killEmail").equalsIgnoreCase("true")) {
            clientKill(optsMap.get("-email"), optsMap.get("-apiKey"), 10);
        }
        if (optsMap.containsKey("-killRSN")) {
            clientKill(optsMap.get("-killRSN"), optsMap.get("-apiKey"), 10);
        }
        if (optsMap.containsKey("-killTag")) {
            processKill(optsMap.get("-killTag"), 10);
        }
        if (optsMap.containsKey("-killKey")) {
            processKill(optsMap.get("-killKey"), 10);
        }

        if (optsMap.containsKey("-proxyIp") && optsMap.containsKey("-proxyPass")) {
            startClient(optsMap.get("-apiKey"), Integer.parseInt(optsMap.get("-world")), optsMap.get("-email"),
                    optsMap.get("-password"), optsMap.get("-proxyIp"), optsMap.get("-proxyUser"),
                    optsMap.get("-proxyPass"), optsMap.get("-proxyPort"), optsMap.get("-killKey"), 10);
        } else if (optsMap.containsKey("-proxyIp")) {
            startClient(optsMap.get("-apiKey"), Integer.parseInt(optsMap.get("-world")), optsMap.get("-email"),
                    optsMap.get("-password"), optsMap.get("-proxyIp"), optsMap.get("-proxyPort"), optsMap.get("-killKey"), 10);
        } else {
            startClient(optsMap.get("-apiKey"), Integer.parseInt(optsMap.get("-world")),
                    optsMap.get("-email"), optsMap.get("-password"), optsMap.get("-killKey"), 10);
        }
    }

    private static void startClient(String apiKey, int world, String email, String password, String killKey, int retries) throws Exception {
        startClient(apiKey, world, email, password, null, null, null, null, killKey, retries);
    }

    private static void startClient(String apiKey, int world, String email, String password, String proxyIp, String proxyPort, String killKey, int retries) throws Exception {
        startClient(apiKey, world, email, password, proxyIp, null, null, proxyPort, killKey, retries);
    }

    private static void startClient(String apiKey, int world, String email, String password, String proxyIp,
                                    String proxyUser, String proxyPass, String proxyPort, String killKey, int retries) throws Exception {

        String[] accountInfo = new String[]{email, password, proxyIp, proxyUser, proxyPass, proxyPort};
        try {
            new ClientQuickLauncher("Ultimate Script", false, world, killKey)
                    .launchClient(accountInfo, apiKey);

        } catch (IOException e) {
            if (retries > 0) {
                startClient(apiKey, world, email, password, proxyIp, proxyUser, proxyPass, proxyPort, killKey, retries - 1);
            }
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    private static void sleep(long minutes) throws Exception {
        try {
            System.out.println("Sleeping " + minutes + " min(s) While Questing");
            for (int s = 0; s < minutes; s++) {
                Thread.sleep(TimeUnit.MINUTES.toMillis(1));
                System.out.println("--> " + (minutes - s - 1));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    private static void clientKill(String killIdentifier, String apiKey, int retries) throws Exception {
        try {
            List<LaunchedClient> clients = BotManagement.getRunningClients(apiKey);
            if (clients.isEmpty() && retries > 0) {
                throw new Exception("Failed to get running clients");
            }
            for (LaunchedClient client : clients) {
                String email = client.getRunescapeEmail();
                String rsn = client.getRsn();
                if (email != null && email.equals(killIdentifier)) {
                    if (!client.kill(apiKey) && retries > 0) {
                        throw new Exception("Failed to kill via email");
                    }
                }
                if (rsn != null && rsn.equals(killIdentifier)) {
                    if (!client.kill(apiKey) && retries > 0) {
                        throw new Exception("Failed to kill via RSN");
                    }
                }
            }

        } catch (Exception e) {
            if (retries > 0) {
                clientKill(killIdentifier, apiKey, retries - 1);
            }
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    private static void processKill(String killKey, int retries) throws Exception {
        try {
            Process pk = Runtime.getRuntime().exec(
                    "cmd /c start cmd.exe /K \"" + "wmic Path win32_process Where \"CommandLine Like '%" + killKey + "%'\" Call Terminate" + "\"");
            pk.waitFor(15000, TimeUnit.MILLISECONDS);
            pk.destroy();

        } catch (Exception e) {
            if (retries > 0) {
                processKill(killKey, retries - 1);
            }
            e.printStackTrace();
            throw new Exception(e);
        }
    }
}
