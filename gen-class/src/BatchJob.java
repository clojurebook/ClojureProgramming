import com.clojurebook.CustomException;
import clojure.lang.PersistentHashMap;

public class BatchJob {
    private static void performOperation (String jobId, String priority) {
        throw new CustomException(PersistentHashMap.create("jobId", jobId, "priority", priority),
                "Operation failed");
    }
    
    private static void runBatchJob (int customerId) {
        try {
            performOperation("verify-billings", "critical");
        } catch (CustomException e) {
            e.addInfo("customer-id", customerId);
            e.addInfo("timestamp", System.currentTimeMillis());
            throw e;
        }
    }
    
    public static void main (String[] args) {
        try {
            runBatchJob(89045);
        } catch (CustomException e) {
            System.out.println("Error! " + e.getMessage() + " " + e.getInfo());
        }
    }
}
