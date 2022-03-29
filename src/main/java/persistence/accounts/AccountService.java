package persistence.accounts;

import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;

import javax.xml.parsers.*;
import javax.xml.transform.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import util.XMLLoader;
import util.XMLSaver;

public class AccountService {
    private HashMap<String, Account> accounts = new HashMap<String, Account>();

    public boolean isNameTaken(String name) {
        return accounts.containsKey(name);
    }

    public boolean addAccount(String username, String password) {
        if (!accounts.containsKey(username)) {
            Account account = new Account(username, password, new EnumMap<AccountStat, Integer>(AccountStat.class));
            accounts.put(username, account);
            return true;
        }
        return false;
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
                for (AccountStat stat : account.getKeySet()) {
                    Element statElem = document.createElement(AccountStat.enumToString(stat));
                    statElem.setTextContent(account.getData(stat).toString());
                    accountElem.appendChild(statElem);
                }

                root.appendChild(accountElem);
            }

            document.appendChild(root);

            // Writes document to file
            XMLSaver.writeDocument(document, filePath);
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static AccountService load(String filePath) {
        AccountService accountService = new AccountService();
        try {
            Document doc = XMLLoader.readDocument(filePath);

            // Iterate through account nodes
            NodeList list = doc.getElementsByTagName("account");
            for (int i = 0; i < list.getLength(); i++) {
                Node accountNode = list.item(i);
                if (list.item(i) instanceof Element accountElem) {

                    // Parse account username + pass
                    String username = accountElem.getAttribute("username");
                    String password = accountElem.getAttribute("password");

                    // Parse account statistics
                    EnumMap<AccountStat, Integer> accountStats = new EnumMap<AccountStat, Integer>(AccountStat.class);
                    NodeList statNodes = accountElem.getChildNodes();
                    for (int j = 0; j <= statNodes.getLength(); j++) {
                        Node statNode = statNodes.item(j);
                        if(statNode.getNodeType() == Node.ELEMENT_NODE)
                        {
                            AccountStat stat = AccountStat.stringToEnum(statNode.getNodeName());
                            System.out.println(statNode.getNodeName());
                            Integer val = Integer.parseInt(statNode.getTextContent());
                            accountStats.put(stat, val);
                        }
                    }

                    // Create account
                    Account account = new Account(username, password, accountStats);
                    accountService.addAccount(account);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return accountService;
    }
}
