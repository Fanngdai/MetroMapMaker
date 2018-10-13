package jtps;

import java.util.ArrayList;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author McKillaGorilla
 * @author Fanng Dai
 */
public class jTPS {
    private ArrayList<jTPS_Transaction> transactions = new ArrayList<>();
    private IntegerProperty mostRecentTransaction = new SimpleIntegerProperty(-1);
    
    public jTPS() {}
    
    public void addTransaction(jTPS_Transaction transaction) {
        // IS THIS THE FIRST TRANSACTION?
        if (getMostRecentTransaction() < 0) {
            // DO WE HAVE TO CHOP THE LIST?
            if (transactions.size() > 0) {
                transactions = new ArrayList<>();
            }
            transactions.add(transaction);
            
        }
        // ARE WE ERASING ALL THE REDO TRANSACTIONS?
        else if (getMostRecentTransaction() < (transactions.size()-1)) {
            transactions.set(getMostRecentTransaction()+1, transaction);
            transactions = new ArrayList<>(transactions.subList(0, getMostRecentTransaction()+2));
        }
        // IS IT JUST A TRANSACTION TO APPEND TO THE END?
        else {
            transactions.add(transaction);
        }
        doTransaction();
    }
    
    public void doTransaction() {
        if (getMostRecentTransaction() < (transactions.size()-1)) {
            jTPS_Transaction transaction = transactions.get(getMostRecentTransaction()+1);
            transaction.doTransaction();
            mostRecentTransaction.setValue(getMostRecentTransaction()+1);
        }
    }
    
    public void undoTransaction() {
        if (getMostRecentTransaction() >= 0) {
            jTPS_Transaction transaction = transactions.get(getMostRecentTransaction());
            transaction.undoTransaction();
            mostRecentTransaction.setValue(getMostRecentTransaction()-1);
        }
    }
    
    public int getMostRecentTransaction(){
        return mostRecentTransaction.getValue();
    }
    
    public IntegerProperty getMostRecentProperty(){
        return mostRecentTransaction;
    }
    
    public int getSizeOfJTPS(){
        return transactions.size();
    }
    
    public void resetJTPS(){
        transactions.clear();
        mostRecentTransaction.set(-1);
    }
    
    public String toString() {
        String text = "--Number of Transactions: " + transactions.size() + "\n";
        text += "--Current Index on Stack: " + mostRecentTransaction + "\n";
        text += "--Current Transaction Stack:\n";
        for (int i = 0; i <= getMostRecentTransaction(); i++) {
            jTPS_Transaction jT = transactions.get(i);
            text += "----" + jT.toString() + "\n";
        }
        return text;
    }
}