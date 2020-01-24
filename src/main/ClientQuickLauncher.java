package main;

import api.bot_management.BotManagement;
import api.bot_management.data.LaunchedClient;
import api.bot_management.data.QuickLaunch;
import org.rspeer.ui.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientQuickLauncher {

    private String scriptName;
    private boolean isRepositoryScript;
    private int world;
    private String scriptArgs = "";
    private String killKey;

    private List<LaunchedClient> runningClients;
    private boolean generateAccounts;

    public List<LaunchedClient> getRunningClients() {
        return runningClients;
    }

    public ClientQuickLauncher(String scriptName, boolean isRepositoryScript, int world, String killKey) {
        this.scriptName = scriptName;
        this.isRepositoryScript = isRepositoryScript;
        this.world = world;
        this.killKey = killKey;
    }

    public ClientQuickLauncher(String scriptName, boolean isRepositoryScript, int world, String scriptArgs, String killKey) {
        this.scriptName = scriptName;
        this.isRepositoryScript = isRepositoryScript;
        this.world = world;
        this.scriptArgs = scriptArgs;
        this.killKey = killKey;
    }

    public int launchClient(String[] accountInfo, String apiKey) throws IOException {
        System.out.println("Launching Client  |  " + scriptName + "  |  World " + world);

        QuickLaunch quickLaunch = setupQuickLauncher(accountInfo);

        BotManagement.startClient(0, quickLaunch.get().toString(), 10, null, 1, 10, apiKey, killKey);

        return 60_000;
    }

    private QuickLaunch setupQuickLauncher(String[] accountInfo) {
        QuickLaunch qL = new QuickLaunch();
        ArrayList<QuickLaunch.Client> clientList = new ArrayList<>();

        QuickLaunch.Config config = qL.new Config(
                true, true, 0, false, false);
        QuickLaunch.Script script = qL.new Script(
                scriptArgs, scriptName, "", isRepositoryScript);
        QuickLaunch.Proxy proxy;
        if (accountInfo.length == 6 && accountInfo[4] != null && !accountInfo[4].isEmpty()) {
            proxy = qL.new Proxy(
                    "0", "9/29/2019", "DrScatman", "proxy1", accountInfo[2], Integer.parseInt(accountInfo[5]), accountInfo[3], accountInfo[4]);
        }
        else if (accountInfo.length == 4 && accountInfo[2] != null && !accountInfo[2].isEmpty()) {
            proxy = qL.new Proxy(
                    "0", "9/29/2019", "DrScatman", "proxy1", accountInfo[2], Integer.parseInt(accountInfo[3]), "", "");
        }
        else {
            proxy = qL.new Proxy("", "", "", "", "", 80, "", "");
        }

        QuickLaunch.Client qLClient = qL.new Client(
                accountInfo[0], accountInfo[1], world, proxy, script, config);

        clientList.add(qLClient);
        qL.setClients(clientList);
        return qL;
    }
}
