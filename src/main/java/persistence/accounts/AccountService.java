package persistence.accounts;

import java.util.EnumMap;
import java.util.HashMap;

import javax.xml.parsers.*;

import org.w3c.dom.*;

import model.tracking.StatTracker;
import model.tracking.TrackedStat;
import util.GameLoader;
import util.GameSaver;

public class AccountService {
    private HashMap<String, Account> accounts = new HashMap<String, Account>();

    public boolean isNameTaken(String name) {
        return accounts.containsKey(name);
    }

    public Account addAccount(String username, String password) {
        if (!accounts.containsKey(username)) {
            Account account = new Account(username, password);
            accounts.put(username, account);
            return account;
        }
        return null;
    }

    public boolean addAccount(Account account) {
        if (!accounts.containsKey(account.getUsername())) {
            accounts.put(account.getUsername(), account);
            return true;
        }
        return false;
    }

    public Account authenticate(String username, String password) {
        Account account = accounts.get(username);
        if (account == null)
            return null;
        if (!account.getPassword().equals(password))
            return null;
        return account;
    }

    public void save(String filePath) {
        try {
            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = docBuilder.newDocument();

            Element root = document.createElement("accounts");
            for (Account account : accounts.values()) {
                // Create account element with username + pass
                Element accountElem = document.createElement("account");
                accountElem.setAttribute("username", account.getUsername());
                accountElem.setAttribute("password", account.getPassword());

                // Create each element for account statistic
                StatTracker accountTracker = account.getTracker();
                for (TrackedStat stat : TrackedStat.values()) {
                    // Process stat name and data
                    Element statElem = document.createElement(stat.name());
                    Integer statVal = accountTracker.getValue(stat);
                    statElem.setTextContent(statVal.toString());

                    // Only add element in value isn't 0
                    if (statVal != 0)
                        accountElem.appendChild(statElem);
                }

                root.appendChild(accountElem);
            }

            document.appendChild(root);

            // Writes document to file
            GameSaver.writeDocument(document, filePath);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static AccountService load(String filePath) {
        AccountService accountService = new AccountService();
        Document doc = GameLoader.readDocument(filePath);

        // Iterate through account nodes
        NodeList list = doc.getElementsByTagName("account");
        for (int i = 0; i < list.getLength(); i++) {
            Node accountNode = list.item(i);
            if (accountNode instanceof Element accountElem) {

                // Parse account username + pass
                String username = accountElem.getAttribute("username");
                String password = accountElem.getAttribute("password");

                // Parse account statistics
                EnumMap<TrackedStat, Integer> accountStats = new EnumMap<TrackedStat, Integer>(
                        TrackedStat.class);
                NodeList statNodes = accountElem.getChildNodes();
                for (int j = 0; j < statNodes.getLength(); j++) {
                    Node statNode = statNodes.item(j);
                    if (statNode.getNodeType() == Node.ELEMENT_NODE) {
                        TrackedStat stat = TrackedStat.valueOf(statNode.getNodeName());
                        Integer val = Integer.parseInt(statNode.getTextContent());
                        accountStats.put(stat, val);
                    }
                }

                // Create account
                Account account = new Account(username, password, new StatTracker(accountStats));
                accountService.addAccount(account);
            }
        }
        return accountService;
    }
}
